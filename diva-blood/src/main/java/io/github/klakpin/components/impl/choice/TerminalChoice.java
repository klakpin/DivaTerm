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

import static io.github.klakpin.terminal.TerminalWrapper.SPACE;
import static io.github.klakpin.terminal.TerminalWrapper.TAB;
import static io.github.klakpin.theme.ColorPalette.ColorFeature.*;
import static org.jline.utils.NonBlockingReader.READ_EXPIRED;

public class TerminalChoice implements Choice {

    private final int HEADER_OFFSET = 2;
    private final int FOOTER_OFFSET = 2;

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
    private final Map<Integer, ChoiceOption> selectedOptions = new HashMap<>();

    List<ChoiceOption> visibleOptions = new ArrayList<>();

    public TerminalChoice(TerminalWrapper terminal,
                          ColorPalette colorPalette,
                          String question,
                          int maxDisplayResults,
                          int maxSelectResults,
                          boolean filteringEnabled,
                          Double filteringSimilarityCutoff,
                          List<ChoiceOption> options,
                          OptionsProvider provider,
                          OptionsComparator optionsComparator,
                          boolean dontShowSelected) {
        if (options == null && provider == null) {
            throw new IllegalStateException("One of 'options' or 'optionsProvider' should be set for Choice component, but none was set");
        }
        if (terminal.jlineTerminal().getType().equals("dumb")) {
            throw new IllegalStateException("Interactive options are disabled for dumb terminal");
        }
        if (maxDisplayResults < 1) {
            throw new IllegalStateException("maxDisplayResults should be more than 0");
        }
        if (maxSelectResults < 1) {
            throw new IllegalStateException("maxSelectResults should be more than 0");
        }

        if (filteringSimilarityCutoff < 0 || filteringSimilarityCutoff > 1) {
            throw new IllegalStateException("filteringSimilarityCutoff must be between 0 and 1");
        }

        this.terminal = terminal;
        this.colorPalette = colorPalette;
        this.question = question;
        this.maxSelectResults = maxSelectResults;
        this.multiSelect = maxSelectResults > 1;
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
        return getMulti().getFirst();
    }

    @Override
    public List<ChoiceOption> getMulti() {
        doGet();
        var results = selectedOptions.values().stream().toList();

        if (!dontShowSelected) {
            printSelected(results);
        }
        return results;
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
            }

            int input = terminal.pollInput(reader);

            if (input == TerminalWrapper.ENTER) {
                break;
            } else if (input != READ_EXPIRED) {
                updateState(input);
                needRedraw = true;
            }
        }

        if (maxSelectResults == 1 && selectedOptions.isEmpty()) {
            selectedOptions.put(visibleOptions.get(activeElementIndex).id(), visibleOptions.get(activeElementIndex));
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
        var beforeCutoff = options.stream()
                .map(option -> Pair.of(option, optionsComparator.getSimilarity(filter.toString(), option)))
                .sorted(Comparator.comparingDouble(Pair::getRight))
                .toList()
                .reversed()
                .stream()
                .limit(maxDisplayResults)
                .toList();

        var afterCutoff = beforeCutoff.stream()
                .filter(value -> filter.isEmpty() || (value.getRight() >= filteringSimilarityCutoff))
                .toList();

        if (!afterCutoff.isEmpty()) { // If everything was filtered out, show the first matching element
            return afterCutoff.stream().map(Pair::getKey).toList();
        } else {
            return beforeCutoff.stream().limit(1).map(Pair::getKey).toList();
        }
    }

    private void drawChoice() {
        var hint = buildHintString();
        terminal.setCursorPosition(initialPosition.getY(), 0);
        terminal.flush();

        terminal.printlnFull(hint);

        if (activeElementIndex > visibleOptions.size() - 1) {
            activeElementIndex = visibleOptions.size() - 1;
        }

        for (int i = 0; i < visibleOptions.size(); i++) {
            printChoiceRow(i);
        }

        terminal.printlnFull("");
        terminal.printlnFull(howToString());

        for (int i = 0; i < maxDisplayResults - visibleOptions.size(); i++) {
            terminal.emptyLine();
        }

        terminal.flush();
    }

    private void printChoiceRow(int index) {
        if (index == activeElementIndex) {
            terminal.printlnFull(activeChoice(visibleOptions.get(index).displayText()));
        } else if (selectedOptions.containsKey(visibleOptions.get(index).id())) {
            terminal.printlnFull(selectedChoice(visibleOptions.get(index).displayText()));
        } else {
            terminal.printlnFull(inactiveChoice(visibleOptions.get(index).displayText()));
        }
    }

    private String activeChoice(String choice) {
        if (multiSelect && selectedOptions.containsKey(visibleOptions.get(activeElementIndex).id())) {
            return colorPalette.apply("> ✓ " + choice, active_text, bold);
        } else if (multiSelect && !selectedOptions.containsKey(visibleOptions.get(activeElementIndex).id())) {
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

    private String howToString() {
        if (multiSelect) {
            return colorPalette.apply(" tab - select, arrows - move, enter - confirm, type to filter", secondary_info);
        } else {
            return colorPalette.apply(" arrows - move, enter - confirm, type to filter", secondary_info);
        }
    }

    private String buildHintString() {
        var result = new StringBuilder();

        if (maxDisplayResults < options.size()) {
            result.append(colorPalette.apply(String.format("Showing incomplete (%s of %s) options, use filter for lookup", visibleOptions.size(), options.size()), secondary_info));
        }

        result.append("\n")
                .append(colorPalette.apply("?", bold, info))
                .append(" ")
                .append(colorPalette.apply(question))
                .append(": ")
                .append(filter);

        var questionStrLength = terminal.getWidth() - question.length() - filter.length() - result.length() - 1;
        return result.append(" ".repeat(Math.max(questionStrLength, 0))).toString();
    }

    private void updateState(int input) {
        if (input == TAB && multiSelect) {
            var selectedOption = visibleOptions.get(activeElementIndex);

            if (selectedOptions.containsKey(selectedOption.id())) {
                selectedOptions.remove(selectedOption.id());
            } else if (maxSelectResults != -1 && selectedOptions.size() < maxSelectResults) {
                selectedOptions.put(selectedOption.id(), selectedOption);
            }
        } else if (input == TerminalWrapper.ARROW_DOWN || input == TerminalWrapper.ARROW_UP) {
            updateActiveElement(input, visibleOptions.size());
        } else if (input == TerminalWrapper.BACKSPACE && !filter.isEmpty()) {
            filter.deleteCharAt(filter.length() - 1);
        } else if (Character.isLetterOrDigit(input) || input == SPACE) {
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