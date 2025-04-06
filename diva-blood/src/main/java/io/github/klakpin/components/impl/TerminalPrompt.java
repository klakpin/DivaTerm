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
        return doGetPrompt(text, null);
    }

    @Override
    public String prompt(String text, String defaultValue) {
        return doGetPrompt(text, defaultValue);
    }

    private String doGetPrompt(String text, String defaultValue) {
        String result;
        if (defaultValue == null) {
            result = doPrompt(text);
        } else {
            result = doPrompt(text, defaultValue);
        }

        terminal.printlnFull("\r" + colorPalette.apply("✔ ", success, bold) + text + ": " + colorPalette.apply(result, bold));
        terminal.flush();

        return result;
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

        var result = removeNulls(inputBuffer);
        if (result.isEmpty()) {
            return defaultValueHint;
        } else {
            return result;
        }
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
