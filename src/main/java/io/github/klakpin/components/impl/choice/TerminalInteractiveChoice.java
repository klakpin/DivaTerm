package io.github.klakpin.components.impl.choice;

import io.github.klakpin.terminal.NoopIntConsumer;
import io.github.klakpin.terminal.TerminalWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import io.github.klakpin.components.api.InteractiveChoice;
import io.github.klakpin.components.helper.TerminalCleaner;
import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;
import org.jline.utils.NonBlockingReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static io.github.klakpin.theme.ColorPalette.ColorFeature.*;
import static org.jline.utils.NonBlockingReader.READ_EXPIRED;

public class TerminalInteractiveChoice implements InteractiveChoice {

    private final TerminalWrapper terminal;
    private final ScheduledExecutorService drawingExecutor;
    private final ColorPalette colorPalette;
    private final TerminalCleaner cleaner;

    final int headerOffset = 2;

    Cursor initialPosition;

    public TerminalInteractiveChoice(Terminal terminal,
                                     ScheduledExecutorService drawingExecutor,
                                     ColorPalette colorPalette,
                                     TerminalCleaner cleaner) {
        this.terminal = new TerminalWrapper(terminal);
        this.drawingExecutor = drawingExecutor;
        this.colorPalette = colorPalette;
        this.cleaner = cleaner;
    }

    @Override
    public String interactiveChoice(String question,
                                    int maxResults,
                                    OptionsProvider optionsProvider,
                                    OptionsComparator comparator,
                                    Double filterValuesCutoff) {
        cleaner.forwardCleanup(maxResults);
        var currentPosition = terminal.getCursorPosition(new NoopIntConsumer());
        initialPosition = new Cursor(currentPosition.getX(), currentPosition.getY() - maxResults);

        var result = interactiveChoiceLoop(question, maxResults, optionsProvider, comparator, filterValuesCutoff);
        terminal.setCursorPosition(initialPosition.getY(), 0);

//        terminal.puts(InfoCmp.Capability.save_cursor);
//        cleaner.forwardCleanup(maxResults + headerOffset);
//        terminal.puts(InfoCmp.Capability.restore_cursor);

        terminal.flush();
        return result;
    }

    private String interactiveChoiceLoop(String question,
                                         int maxResults,
                                         OptionsProvider optionsProvider,
                                         OptionsComparator comparator,
                                         Double filterValuesCutoff) {
        InteractiveChoiceState choiceState = new InteractiveChoiceState(0, "");

        NonBlockingReader reader = terminal.reader();

        List<String> visibleOptions = new ArrayList<>();

        boolean needRedraw = true;
        while (true) {
            if (needRedraw) {
                needRedraw = false;
                visibleOptions = filterOptions(choiceState, maxResults, optionsProvider, comparator, filterValuesCutoff);
                drawChoice(question, choiceState, maxResults, visibleOptions);
            }

            int input = terminal.pollInput(reader);

            if (input == TerminalWrapper.ENTER) {
                break;
            } else if (input != READ_EXPIRED) {
                choiceState = updateState(input, choiceState, visibleOptions.size());
                needRedraw = true;
            }
        }

        return visibleOptions.get(choiceState.selection);
    }


    private List<String> filterOptions(InteractiveChoiceState state,
                                       int maxResults,
                                       OptionsProvider optionsProvider,
                                       OptionsComparator comparator,
                                       Double filterValuesCutoff) {
        return optionsProvider.get(state.filter)
                .stream()
                .map(option -> Pair.of(option, comparator.getSimilarity(state.filter, option)))
                .filter(value -> value.getRight() > filterValuesCutoff)
                .sorted(Comparator.comparingDouble(Pair::getRight))
                .limit(maxResults)
                .map(Pair::getKey)
                .toList();
    }

    private void drawChoice(String question,
                            InteractiveChoiceState state,
                            int maxResults,
                            List<String> visibleOption) {
        var hint = buildHintString(question, state.filter);
        terminal.setCursorPosition(initialPosition.getY(), 0);
        terminal.flush();

        terminal.printlnFull(hint);

        for (int i = 0; i < visibleOption.size(); i++) {
            if (i == state.selection) {
                terminal.printlnFull(selectedChoice(visibleOption.get(i)));
            } else {
                terminal.printlnFull(notSelectedChoice(visibleOption.get(i)));
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

    private String buildHintString(String question, String filter) {
        var result = new StringBuilder();
        result.append(colorPalette.apply("?", bold, info))
                .append(" ")
                .append(question)
                .append(colorPalette.apply(" [Use arrows to move, type to search and filter]:", call_to_action))
                .append(" ")
                .append(filter);

        return result
                .append(" ".repeat(terminal.getWidth() - result.length()))
                .toString();
    }

    private InteractiveChoiceState updateState(int input, InteractiveChoiceState currentState, int visibleOptionsCount) {
        if (input == TerminalWrapper.ARROW_DOWN || input == TerminalWrapper.ARROW_UP) {
            return updateSelectedElement(input, currentState, visibleOptionsCount);
        } else if (input == TerminalWrapper.BACKSPACE) {
            return new InteractiveChoiceState(currentState.selection, StringUtils.chop(currentState.filter));
        } else if (Character.isLetterOrDigit(input)) {
            return new InteractiveChoiceState(currentState.selection, currentState.filter + (char) input);
        }
        return currentState;
    }

    private InteractiveChoiceState updateSelectedElement(int input, InteractiveChoiceState currentState, int visibleOptionsCount) {
        switch (input) {
            case TerminalWrapper.ARROW_DOWN -> {
                return new InteractiveChoiceState(Math.floorMod(currentState.selection + 1, visibleOptionsCount), currentState.filter);
            }
            case TerminalWrapper.ARROW_UP -> {
                return new InteractiveChoiceState(Math.floorMod(currentState.selection - 1, visibleOptionsCount), currentState.filter);
            }
            default -> throw new RuntimeException("Unexpected element index update command " + input);
        }
    }

    record InteractiveChoiceState(int selection, String filter) {
    }
}

