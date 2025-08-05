package io.github.klakpin.utils;

import io.github.klakpin.theme.ColorPalette;

public class NoopColorPalette implements ColorPalette {

    public static final NoopColorPalette INSTANCE = new NoopColorPalette();
    @Override
    public String apply(String string, ColorFeature... features) {
        return string;
    }
}
