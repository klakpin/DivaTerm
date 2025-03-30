package io.github.klakpin.components.impl;

import io.github.klakpin.terminal.TerminalWrapper;
import io.github.klakpin.components.api.Message;
import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Terminal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TerminalMessage implements Message {

    private final TerminalWrapper terminal;
    private final ColorPalette colorPalette;

    public TerminalMessage(Terminal terminal, ColorPalette colorPalette) {
        this.terminal = new TerminalWrapper(terminal);
        this.colorPalette = colorPalette;
    }

    @Override
    public void printMessage(String message) {
        terminal.writer().println(message);
        terminal.flush();
    }

    @Override
    public void printMessage(String message, ColorPalette.ColorFeature... colorFeature) {
        terminal.writer().println(colorPalette.apply(message, colorFeature));
        terminal.flush();
    }

    @Override
    public void printInBracket(String message) {
        bracketPrint(message, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public void printInBracket(String message, List<ColorPalette.ColorFeature> bracketFeatures, List<ColorPalette.ColorFeature> textFeatures) {
        bracketPrint(message, bracketFeatures, textFeatures);
    }

    private void bracketPrint(String payload, List<ColorPalette.ColorFeature> bracketFeatures, List<ColorPalette.ColorFeature> textFeatures) {
        var bracketFeaturesArr = bracketFeatures.toArray(new ColorPalette.ColorFeature[0]);
        var textFeaturesArr = textFeatures.toArray(new ColorPalette.ColorFeature[0]);

        terminal.writer().println(colorPalette.apply("┌", bracketFeaturesArr));

        Arrays.stream(payload.split("\n"))
                .forEach(s -> {
                    var prefix = colorPalette.apply("│ ", bracketFeaturesArr);
                    var message = colorPalette.apply(s, textFeaturesArr);

                    terminal.writer().println(prefix + message);
                });

        terminal.writer().println(colorPalette.apply("└", bracketFeaturesArr));
    }
}
