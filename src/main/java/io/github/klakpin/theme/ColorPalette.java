package io.github.klakpin.theme;

public interface ColorPalette {

    String apply(String string, ColorFeature... features);

    enum ColorFeature {
        info,
        success,
        error,
        warning,

        bold,
        call_to_action,
        secondary_info,
        secondary_info_bracket,
        error_bracket,

        loading_spinner
    }
}