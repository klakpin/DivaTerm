package io.github.klakpin.components.impl.choice;

import io.github.klakpin.components.api.choice.*;
import io.github.klakpin.terminal.NoopIntConsumer;
import io.github.klakpin.terminal.TerminalWrapper;
import org.apache.commons.lang3.tuple.Pair;
import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Cursor;
import org.jline.utils.NonBlockingReader;

import java.util.*;

import static io.github.klakpin.terminal.TerminalWrapper.TAB;
import static io.github.klakpin.theme.ColorPalette.ColorFeature.*;
import static org.jline.utils.NonBlockingReader.READ_EXPIRED;

public class TerminalChoice implements Choice {

    private final TerminalWrapper terminal;
    private final ColorPalette colorPalette;

    private final String question;

    private final int maxDisplayResults;
    private final int maxSelectResults;

    private final boolean multiSelect;
    private final boolean filteringEnabled;

    private final Double filteringSimilarityCutoff;

    private final List<ChoiceOption> options;
    private final OptionsProvider optionsProvider;

    private final OptionsComparator optionsComparator;

    Cursor initialPosition;

    private int activeElementIndex = 0;
    private final StringBuffer filter = new StringBuffer();
    private final List<Integer> selectedIds = new ArrayList<>();

    List<ChoiceOption> visibleOptions = new ArrayList<>();

    public TerminalChoice(TerminalWrapper terminal, ColorPalette colorPalette, String question, int maxDisplayResults, int maxSelectResults, boolean multiSelect, boolean filteringEnabled, Double filteringSimilarityCutoff, List<ChoiceOption> options, OptionsProvider provider, OptionsComparator optionsComparator) {
        this.terminal = terminal;
        this.colorPalette = colorPalette;
        this.question = question;
        this.maxDisplayResults = maxDisplayResults;
        this.maxSelectResults = maxSelectResults;
        this.multiSelect = multiSelect;
        this.filteringEnabled = filteringEnabled;
        this.filteringSimilarityCutoff = filteringSimilarityCutoff;
        this.options = options;
        optionsProvider = provider;
        this.optionsComparator = optionsComparator;
    }


    @Override
    public ChoiceOption get() {
        doGet();
        return visibleOptions.get(activeElementIndex);
    }

    @Override
    public List<ChoiceOption> getMulti() {
        return doGet();
    }

    private List<ChoiceOption> doGet() {
        terminal.forwardCleanup(maxDisplayResults);
        var currentPosition = terminal.getCursorPosition(new NoopIntConsumer());
        initialPosition = new Cursor(currentPosition.getX(), currentPosition.getY() - maxDisplayResults);

        var result = interactiveChoiceLoop();

        terminal.setCursorPosition(initialPosition.getY(), 0);
        terminal.forwardCleanup(maxDisplayResults);

        terminal.setCursorPosition(initialPosition.getY(), 0);
        terminal.flush();
        return result;
    }

    private List<ChoiceOption> interactiveChoiceLoop() {
        NonBlockingReader reader = terminal.reader();

        boolean needRedraw = true;
        while (true) {
            if (needRedraw) {
                needRedraw = false;
                visibleOptions = getVisibleOptions();
                drawChoice();
                terminal.printDebugInfo("Choice state", Arrays.asList(
                        String.format("activeElementIndex: '%s'", activeElementIndex),
                        String.format("selectedIds: '%s'", selectedIds)
                ), 15);
            }

            int input = terminal.pollInput(reader);

            if (input == TerminalWrapper.ENTER) {
                break;
            } else if (input != READ_EXPIRED) {
                updateState(input);
                needRedraw = true;
            }
        }

        return visibleOptions.stream()
                .filter(vo -> selectedIds.contains(vo.id()))
                .toList();
    }

    private List<ChoiceOption> getVisibleOptions() {
        if (!filteringEnabled) {
            return options;
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
                .filter(value -> value.getRight() >= filteringSimilarityCutoff)
                .sorted(Comparator.comparingDouble(Pair::getRight))
                .toList()
                .reversed()
                .stream()
                .limit(maxDisplayResults)
                .map(Pair::getKey)
                .toList();

        terminal.printDebugInfo("coefficients", debugLines, 5);
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

        var usage = "x toggle • ←↓↑→ navigate • enter submit";

        for (int i = 1; i < maxDisplayResults - visibleOptions.size(); i++) {
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
            return colorPalette.apply("> ✓ " + choice, call_to_action);
        } else if (multiSelect && !selectedIds.contains(visibleOptions.get(activeElementIndex).id())) {
            return colorPalette.apply("> • " + choice, call_to_action);
        } else {
            return colorPalette.apply("> " + choice, call_to_action);
        }
    }

    private String selectedChoice(String choice) {
        return colorPalette.apply("  ✓ " + choice, call_to_action);
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

        result.append(colorPalette.apply("?", bold, info))
                .append(" ")
                .append(question)
                .append(colorPalette.apply(" [Use arrows to move, type to search and filter]:", call_to_action))
                .append(" ")
                .append(filter);

        return result.append(" ".repeat(terminal.getWidth() - result.length())).toString();
    }

    private void updateState(int input) {
        if (input == TAB && multiSelect) {
            var selectedId = visibleOptions.get(activeElementIndex).id();

            if (selectedIds.contains(selectedId)) {
                selectedIds.remove((Integer) selectedId);
            } else if (selectedIds.size() < maxSelectResults) {
                selectedIds.add(selectedId);
            }
        } else if (input == TerminalWrapper.ARROW_DOWN || input == TerminalWrapper.ARROW_UP) {
            updateActiveElement(input, visibleOptions.size());
        } else if (input == TerminalWrapper.BACKSPACE && filter.length() != 0) {
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


/*
  • A ♠
  • A ♥
> • A ♣
  • A ♦
  • K ♠
  > ✓ K ♥
  • K ♥
  • K ♣
  • K ♦
  • Q ♠
  • Q ♥
  • Q ♣
  • Q ♦
  • J ♠
  • J ♥
  • J ♣

  ••••
x toggle • ←↓↑→ navigate • enter submit
 */