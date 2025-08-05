package io.github.klakpin.theme;

import java.util.HashMap;
import java.util.Map;

public class DefaultTerminalColorPalette implements ColorPalette {

    private final Map<ColorFeature, String> colors = new HashMap<>();

    @SuppressWarnings("FieldCanBeLocal")
    private final String reset = "\u001B[0m";

    public DefaultTerminalColorPalette() {
        initDefault24BitColors();
    }

    @Override
    public String apply(String string, ColorFeature... features) {
        var result = new StringBuilder();
        for (ColorFeature feature : features) {
            result.append(colors.get(feature));
        }
        result.append(string);
        result.append(reset);

        return result.toString();
    }

    private void initDefault24BitColors() {
        colors.put(ColorFeature.primary, "");
        colors.put(ColorFeature.secondary, "\u001B[38;5;178m");

        colors.put(ColorFeature.success, "\u001B[38;2;22;219;147m");
        colors.put(ColorFeature.error, "\u001B[38;2;186;39;74m");
        colors.put(ColorFeature.warning, "\u001B[38;5;178m");
        colors.put(ColorFeature.accent, "\u001B[38;5;51m");

        colors.put(ColorFeature.bold, "\u001B[1m");

        colors.put(ColorFeature.active_background, "\u001B[48;2;119;119;221m");
        colors.put(ColorFeature.muted_background, "\u001B[48;5;237m");

        colors.put(ColorFeature.muted, "\u001B[38;5;246m");

    }
}
