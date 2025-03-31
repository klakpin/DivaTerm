package io.github.klakpin.components.impl;

import io.github.klakpin.terminal.TerminalWrapper;
import io.github.klakpin.components.api.Prompt;
import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Terminal;
import org.jline.utils.NonBlockingReader;

import static io.github.klakpin.theme.ColorPalette.ColorFeature.bold;
import static io.github.klakpin.theme.ColorPalette.ColorFeature.success;

public class TerminalPrompt implements Prompt {

    private final TerminalWrapper terminal;
    private final ColorPalette colorPalette;

    public TerminalPrompt(Terminal terminal, ColorPalette colorPalette) {
        this.terminal = new TerminalWrapper(terminal);
        this.colorPalette = colorPalette;
    }

    @Override
    public String prompt(String text) {
        return doPrompt(text);
    }

    @Override
    public String prompt(String text, String defaultValue) {
        var result = doPrompt(text, defaultValue);
//        terminal.printDebugInfo("default prompt raw res", List.of(String.format("raw result: '%s'", result)), 1);
        if (result.isEmpty()) {
            return defaultValue;
        } else {
            return result;
        }
    }

    @Override
    public Boolean promptBoolean(String text) {
        var result = doPrompt(text, "yes/no");

        if (result.equalsIgnoreCase("yes")) {
            return true;
        } else if (result.equalsIgnoreCase("no") || result.equalsIgnoreCase("n")) {
            return false;
        } else {
            return promptBoolean(text);
        }
    }

    private String doPrompt(String text) {
        return doPrompt(text, "");
    }

    private String doPrompt(String text, String defaultValueHint) {
        NonBlockingReader reader = terminal.reader();

        String prefix;
        if (defaultValueHint.isEmpty()) {
            prefix = "\r" + colorPalette.apply("? ", bold) + text + ": ";
        } else {
            prefix = "\r" + colorPalette.apply("? ", bold) + text + " (" + defaultValueHint + "): ";
        }

        var needRedraw = true;
        var currentIndex = 0;
        var inputBuffer = new char[terminal.getWidth() - prefix.length()];

        while (true) {
            if (needRedraw) {
                terminal.printFull(prefix + replaceNullWithSpace(inputBuffer));
                terminal.flush();
                needRedraw = false;
            }

            var input = terminal.pollInput(reader);
            if (input == TerminalWrapper.ENTER) {
                break;
            } else if (input == TerminalWrapper.BACKSPACE && currentIndex > 0) {
                currentIndex--;
                inputBuffer[currentIndex] = '\u0000';
                needRedraw = true;
            } else if (input != TerminalWrapper.BACKSPACE && input >= 32) {
                inputBuffer[currentIndex] = (char) input;
                currentIndex++;
                needRedraw = true;
            }
        }

        terminal.writer().println("\r" + colorPalette.apply("✔ ", bold, success));
        return removeNulls(inputBuffer);
    }


    private String replaceNullWithSpace(char[] inputText) {
        var result = new StringBuilder();

        for (char c : inputText) {
            if (c == '\u0000') {
                result.append(' ');
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private String removeNulls(char[] inputText) {
        var result = new StringBuilder();

        for (char c : inputText) {
            if (c != '\u0000') {
                result.append(c);
            }
        }

        return result.toString();
    }
}
