package io.github.klakpin.components.impl.choice;

import io.github.klakpin.components.api.choice.*;
import io.github.klakpin.components.api.choice.comparator.FuzzyDisplayTextComparator;
import io.github.klakpin.terminal.NoopIntConsumer;
import io.github.klakpin.terminal.TerminalWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Cursor;
import org.jline.utils.NonBlockingReader;

import java.util.*;

import static io.github.klakpin.terminal.TerminalWrapper.TAB;
import static io.github.klakpin.theme.ColorPalette.ColorFeature.*;
import static org.jline.utils.NonBlockingReader.READ_EXPIRED;

public class TerminalChoice implements Choice {

    private final int HEADER_OFFSET = 2;
    private final int FOOTER_OFFSET = 0;

    private final TerminalWrapper terminal;
    private final ColorPalette colorPalette;

    private final String question;

    private final int maxDisplayResults;
    private final int maxSelectResults;

    private final boolean multiSelect;
    private final boolean filteringEnabled;
    private final boolean dontShowSelected;

    private final Double filteringSimilarityCutoff;

    private List<ChoiceOption> options;
    private final OptionsProvider optionsProvider;

    private final OptionsComparator optionsComparator;

    Cursor initialPosition;

    private int activeElementIndex = 0;
    private final StringBuffer filter = new StringBuffer();
    private final List<Integer> selectedIds = new ArrayList<>();

    List<ChoiceOption> visibleOptions = new ArrayList<>();

    public TerminalChoice(TerminalWrapper terminal,
                          ColorPalette colorPalette,
                          String question,
                          int maxDisplayResults,
                          int maxSelectResults,
                          boolean multiSelect,
                          boolean filteringEnabled,
                          Double filteringSimilarityCutoff,
                          List<ChoiceOption> options,
                          OptionsProvider provider,
                          OptionsComparator optionsComparator,
                          boolean dontShowSelected) {
        this.terminal = terminal;
        this.colorPalette = colorPalette;
        this.question = question;
        this.maxSelectResults = maxSelectResults;
        this.multiSelect = multiSelect;
        this.filteringEnabled = filteringEnabled;
        this.filteringSimilarityCutoff = filteringSimilarityCutoff;
        this.options = options;
        optionsProvider = provider;

        this.optionsComparator = Objects.requireNonNullElseGet(optionsComparator, FuzzyDisplayTextComparator::new);

        if (optionsProvider == null) {
            // The count of elements is known beforehand
            this.maxDisplayResults = Math.min(maxDisplayResults, options.size());
        } else {
            this.maxDisplayResults = maxDisplayResults;
        }
        this.dontShowSelected = dontShowSelected;
    }


    @Override
    public ChoiceOption get() {
        doGet();
        if (!dontShowSelected) {
            printSelected(List.of(visibleOptions.get(activeElementIndex)));
        }
        return visibleOptions.get(activeElementIndex);
        // TODO fix case when all elements are filtered out

    }

    @Override
    public List<ChoiceOption> getMulti() {
        doGet();
        if (!dontShowSelected) {
            printSelected(visibleOptions.stream()
                    .filter(vo -> selectedIds.contains(vo.id()))
                    .toList());
        }
        return visibleOptions.stream()
                .filter(vo -> selectedIds.contains(vo.id()))
                .toList();
    }

    private void doGet() {
        terminal.forwardCleanup(maxDisplayResults + HEADER_OFFSET + FOOTER_OFFSET);
        var currentPosition = terminal.getCursorPosition(new NoopIntConsumer());
        initialPosition = new Cursor(currentPosition.getX(), currentPosition.getY() - maxDisplayResults - HEADER_OFFSET - FOOTER_OFFSET);

        interactiveChoiceLoop();

        terminal.setCursorPosition(initialPosition.getY(), 0);
        terminal.forwardCleanup(maxDisplayResults + HEADER_OFFSET + FOOTER_OFFSET);

        terminal.setCursorPosition(initialPosition.getY(), 0);
        terminal.flush();
    }

    private void interactiveChoiceLoop() {
        NonBlockingReader reader = terminal.reader();

        boolean needRedraw = true;
        while (true) {
            if (needRedraw) {
                needRedraw = false;
                visibleOptions = getVisibleOptions();
                drawChoice();
//                terminal.printDebugInfo("Choice state", Arrays.asList(
//                        String.format("activeElementIndex: '%s'", activeElementIndex),
//                        String.format("selectedIds: '%s'", selectedIds)
//                ), 15);
            }

            int input = terminal.pollInput(reader);

            if (input == TerminalWrapper.ENTER) {
                break;
            } else if (input != READ_EXPIRED) {
                updateState(input);
                needRedraw = true;
            }
        }
    }

    private void printSelected(List<ChoiceOption> result) {
        terminal.setCursorPosition(initialPosition.getY(), 0);
        terminal.writer().print(colorPalette.apply("✔ ", bold, success));
        terminal.writer().print(question + ": ");

        var resultStr = StringUtils.join(result.stream().map(ChoiceOption::displayText).toList(), ", ");
        terminal.writer().println(colorPalette.apply(resultStr, bold));
        terminal.flush();
    }

    private List<ChoiceOption> getVisibleOptions() {
        if (optionsProvider != null) {
            options = optionsProvider.get(filter.toString());
        }

        if (!filteringEnabled) {
            return options.stream().limit(maxDisplayResults).toList();
        } else if (options != null) {
            return filterAndSortOptions(options);
        } else if (optionsProvider != null) {
            return filterAndSortOptions(optionsProvider.get(filter.toString()));
        } else {
            throw new IllegalStateException("One of options or optionsProvider should be set for Choice component, but none was set");
        }
    }

    private List<ChoiceOption> filterAndSortOptions(List<ChoiceOption> options) {
        var debugLines = new ArrayList<String>();

        var result = options.stream()
                .map(option -> Pair.of(option, optionsComparator.getSimilarity(filter.toString(), option)))
                .peek(value -> debugLines.add(String.format("'%s' = '%s'", value.getKey().displayText(), value.getValue())))
                .filter(value -> filter.isEmpty() || (value.getRight() >= filteringSimilarityCutoff))
                .sorted(Comparator.comparingDouble(Pair::getRight))
                .toList()
                .reversed()
                .stream()
                .limit(maxDisplayResults)
                .map(Pair::getKey)
                .toList();

//        terminal.printDebugInfo("coefficients", debugLines, 5);
        return result;
    }

    private void drawChoice() {
        var hint = buildHintString();
        terminal.setCursorPosition(initialPosition.getY(), 0);
        terminal.flush();

        terminal.printlnFull(hint);

        for (int i = 0; i < visibleOptions.size(); i++) {
            printChoiceRow(i);
        }


        for (int i = 0; i < maxDisplayResults - visibleOptions.size(); i++) {
            terminal.emptyLine();
        }

        terminal.flush();
    }

    private void printChoiceRow(int index) {
        if (index == activeElementIndex) {
            terminal.printlnFull(activeChoice(visibleOptions.get(index).displayText()));
        } else if (selectedIds.contains(visibleOptions.get(index).id())) {
            terminal.printlnFull(selectedChoice(visibleOptions.get(index).displayText()));
        } else {
            terminal.printlnFull(inactiveChoice(visibleOptions.get(index).displayText()));
        }
    }

    private String activeChoice(String choice) {
        if (multiSelect && selectedIds.contains(visibleOptions.get(activeElementIndex).id())) {
            return colorPalette.apply("> ✓ " + choice, active_text, bold);
        } else if (multiSelect && !selectedIds.contains(visibleOptions.get(activeElementIndex).id())) {
            return colorPalette.apply("> • " + choice, active_text, bold);
        } else {
            return colorPalette.apply("> " + choice, active_text, bold);
        }
    }

    private String selectedChoice(String choice) {
        return colorPalette.apply("  ✓ " + choice, active_text);
    }

    private String inactiveChoice(String choice) {
        if (multiSelect) {
            return "  • " + choice;
        } else {
            return "  " + choice;
        }
    }

    private String buildHintString() {
        var result = new StringBuilder();
        String howtoString;
        if (multiSelect) {
            howtoString = colorPalette.apply(" [Use Tab to select, arrows to move, type to filter]:", secondary_info);
        } else {
            howtoString = colorPalette.apply(" [Use arrows to move, type to filter]:", secondary_info);
        }
        if (maxDisplayResults < options.size()) {
            result.append(colorPalette.apply(String.format("Showing incomplete (%s of %s) options, narrow list using a filter", maxDisplayResults, options.size()), secondary_info));
        }

        result.append("\n")
                .append(colorPalette.apply("?", bold, info))
                .append(" ")
                .append(colorPalette.apply(question))
                .append(howtoString)
                .append(" ")
                .append(filter);

        var questionStrLength = terminal.getWidth() - question.length() - howtoString.length() - filter.length();
        return result.append(" ".repeat(Math.max(questionStrLength, 0))).toString();
    }

    private void updateState(int input) {
        if (input == TAB && multiSelect) {
            var selectedId = visibleOptions.get(activeElementIndex).id();

            if (selectedIds.contains(selectedId)) {
                selectedIds.remove((Integer) selectedId);
            } else if (maxSelectResults != -1 && selectedIds.size() < maxSelectResults) {
                selectedIds.add(selectedId);
            }
        } else if (input == TerminalWrapper.ARROW_DOWN || input == TerminalWrapper.ARROW_UP) {
            updateActiveElement(input, visibleOptions.size());
        } else if (input == TerminalWrapper.BACKSPACE && !filter.isEmpty()) {
            filter.deleteCharAt(filter.length() - 1);
        } else if (Character.isLetterOrDigit(input)) {
            filter.append((char) input);
        }
    }

    private void updateActiveElement(int input, int visibleOptionsCount) {
        switch (input) {
            case TerminalWrapper.ARROW_DOWN ->
                    activeElementIndex = Math.floorMod(activeElementIndex + 1, visibleOptionsCount);
            case TerminalWrapper.ARROW_UP ->
                    activeElementIndex = Math.floorMod(activeElementIndex - 1, visibleOptionsCount);
            default -> throw new RuntimeException("Unexpected element index update command " + input);
        }
    }
}