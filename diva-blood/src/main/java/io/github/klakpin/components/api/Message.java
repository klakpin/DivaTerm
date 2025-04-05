package io.github.klakpin.components.api;

import io.github.klakpin.theme.ColorPalette;

import java.util.List;

/**
 * Interface defining a set of message printing operations.
 */
public interface Message {

    /**
     * Prints a simple message without any formatting.
     *
     * @param message the text to be printed
     */
    void printMessage(String message);

    /**
     * Prints a message enclosed in square brackets.
     *
     * @param message the text to be printed inside brackets
     */
    void printInBracket(String message);

    /**
     * Prints a message with specified color styling.
     *
     * @param message       the text to be printed
     * @param colorFeatures one or more color features to apply to the message
     */
    void printMessage(String message, ColorPalette.ColorFeature... colorFeatures);

    /**
     * Prints a bracketed message with separate styling for brackets and text.
     *
     * @param message         the text to be printed inside brackets
     * @param bracketFeatures color features to apply to the brackets
     * @param textFeatures    color features to apply to the message text
     */
    void printInBracket(String message,
                        List<ColorPalette.ColorFeature> bracketFeatures,
                        List<ColorPalette.ColorFeature> textFeatures);
}
