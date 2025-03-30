package io.github.klakpin.components.impl.choice;

import io.github.klakpin.components.api.choice.*;
import io.github.klakpin.terminal.NoopIntConsumer;
import io.github.klakpin.terminal.TerminalWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Cursor;
import org.jline.utils.NonBlockingReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.github.klakpin.theme.ColorPalette.ColorFeature.*;
import static org.jline.utils.NonBlockingReader.READ_EXPIRED;

public class TerminalChoice implements Choice {

    private final TerminalWrapper terminal;
    private final ColorPalette colorPalette;

    private final String question;
    private final int maxResults;
    private final boolean multiSelect;
    private final boolean filteringEnabled;

    private final Double filteringSimilarityCutoff;

    private final List<ChoiceOption> options;
    private final OptionsProvider optionsProvider;

    private final OptionsComparator optionsComparator;

    Cursor initialPosition;

    private int activeElementIndex = 0;
    private StringBuffer filter = new StringBuffer();
    private List<Integer> selected = new ArrayList<>();

    public TerminalChoice(TerminalWrapper terminal, ColorPalette colorPalette, String question, int maxResults, boolean multiSelect, boolean filteringEnabled, Double filteringSimilarityCutoff, List<ChoiceOption> options, OptionsProvider provider, OptionsComparator optionsComparator) {
        this.terminal = terminal;
        this.colorPalette = colorPalette;
        this.question = question;
        this.maxResults = maxResults;
        this.multiSelect = multiSelect;
        this.filteringEnabled = filteringEnabled;
        this.filteringSimilarityCutoff = filteringSimilarityCutoff;
        this.options = options;
        optionsProvider = provider;
        this.optionsComparator = optionsComparator;
    }


    @Override
    public ChoiceOption get() {
        terminal.forwardCleanup(maxResults);
        var currentPosition = terminal.getCursorPosition(new NoopIntConsumer());
        initialPosition = new Cursor(currentPosition.getX(), currentPosition.getY() - maxResults);

        var result = interactiveChoiceLoop();
        terminal.setCursorPosition(initialPosition.getY(), 0);

//        terminal.puts(InfoCmp.Capability.save_cursor);
//        cleaner.forwardCleanup(maxResults + headerOffset);
//        terminal.puts(InfoCmp.Capability.restore_cursor);

        terminal.flush();
        return result;
    }


    private ChoiceOption interactiveChoiceLoop() {
        NonBlockingReader reader = terminal.reader();

        List<ChoiceOption> visibleOptions = new ArrayList<>();

        boolean needRedraw = true;
        while (true) {
            if (needRedraw) {
                needRedraw = false;
                visibleOptions = getVisibleOptions();
                drawChoice(visibleOptions);
            }

            int input = terminal.pollInput(reader);

            if (input == TerminalWrapper.ENTER) {
                break;
            } else if (input != READ_EXPIRED) {
                updateState(input, visibleOptions.size());
                needRedraw = true;
            }
        }

        return visibleOptions.get(activeElementIndex);
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
                .limit(maxResults)
                .map(Pair::getKey)
                .toList();

        terminal.printDebugInfo("coefficients", debugLines, 0);
        return result;
    }

    private void drawChoice(List<ChoiceOption> visibleOption) {
        var hint = buildHintString();
        terminal.setCursorPosition(initialPosition.getY(), 0);
        terminal.flush();

        terminal.printlnFull(hint);

        for (int i = 0; i < visibleOption.size(); i++) {
            if (i == activeElementIndex) {
                terminal.printlnFull(selectedChoice(visibleOption.get(i).displayText()));
            } else {
                terminal.printlnFull(notSelectedChoice(visibleOption.get(i).displayText()));
            }
        }

        for (int i = 1; i < maxResults - visibleOption.size(); i++) {
            terminal.emptyLine();
        }

        terminal.flush();
    }

    private String selectedChoice(String choice) {
        return colorPalette.apply("> " + choice, call_to_action);
    }

    private String notSelectedChoice(String choice) {
        return "  " + choice;
    }

    private String buildHintString() {
        var result = new StringBuilder();

        result.append(colorPalette.apply("?", bold, info)).append(" ").append(question).append(colorPalette.apply(" [Use arrows to move, type to search and filter]:", call_to_action)).append(" ").append(filter);

        return result.append(" ".repeat(terminal.getWidth() - result.length())).toString();
    }

    private void updateState(int input, int visibleOptionsCount) {
        if (input == TerminalWrapper.ARROW_DOWN || input == TerminalWrapper.ARROW_UP) {
            updateActiveElement(input, visibleOptionsCount);
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

