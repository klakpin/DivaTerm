package io.github.klakpin.components.api;

import io.github.klakpin.theme.ColorPalette;

import java.util.List;

public interface Message {

    void printMessage(String message);

    void printInBracket(String message);

    void printMessage(String message, ColorPalette.ColorFeature... colorFeature);

    void printInBracket(String message, List<ColorPalette.ColorFeature> bracketFeatures, List<ColorPalette.ColorFeature> textFeatures);
}
