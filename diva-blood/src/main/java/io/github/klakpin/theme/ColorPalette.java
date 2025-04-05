package io.github.klakpin.theme;

public interface ColorPalette {

    String apply(String string, ColorFeature... features);

    /**
     * Color features that can be used with color palette. You can overwrite them if you want; check specific colors
     * used in the component implementations.
     */
    enum ColorFeature {
        info,
        success,
        error,
        warning,

        bold,
        call_to_action,


        active_text,

        active_background,
        inactive_background,

        secondary_info,
        secondary_info_bracket,
        error_bracket,

        loading_spinner
    }
}