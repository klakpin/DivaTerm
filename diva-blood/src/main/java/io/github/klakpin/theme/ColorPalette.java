package io.github.klakpin.theme;

public interface ColorPalette {

    String apply(String string, ColorFeature... features);

    /**
     * Color features that can be used with color palette. You can overwrite them if you want; check specific colors
     * used in the component implementations.
     */
    enum ColorFeature {
        primary,
        secondary,
        success,
        error,
        warning,
        muted,
        accent,

        call_to_action,

        active_background,
        muted_background,

        bold,
    }
}