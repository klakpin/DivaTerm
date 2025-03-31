package io.github.klakpin.theme;

import java.util.HashMap;
import java.util.Map;

public class TerminalColorPalette implements ColorPalette {

    private final Map<ColorFeature, String> colors = new HashMap<>();

    @SuppressWarnings("FieldCanBeLocal")
    private final String reset = "\u001B[0m";

    public TerminalColorPalette() {
        initDefaultColors();
    }

    @SuppressWarnings("unused")
    public TerminalColorPalette(Map<ColorFeature, String> customColors) {
        initDefaultColors();
        this.colors.putAll(customColors);
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


    private void initDefaultColors() {
        colors.put(ColorFeature.info, "");
        colors.put(ColorFeature.success, "\u001B[1m\u001B[38;2;22;219;147m");
//        colors.put(ColorFeature.success, "\u001B[38;5;112m");
//        colors.put(ColorFeature.error, "\u001B[38;5;197m");
        colors.put(ColorFeature.error, "\u001B[1m\u001B[38;2;186;39;74m");
        colors.put(ColorFeature.warning, "");

        colors.put(ColorFeature.bold, "\u001B[1m");

        colors.put(ColorFeature.call_to_action, "\u001B[38;5;51m");

        colors.put(ColorFeature.active_background, "\u001B[48;2;119;119;221m");
//        colors.put(ColorFeature.active_background, "\u001B[48;5;213m");

        colors.put(ColorFeature.inactive_background, "\u001B[48;5;237m");


        colors.put(ColorFeature.secondary_info, "\u001B[38;5;246m"); // or maybe 246
        colors.put(ColorFeature.secondary_info_bracket, "\u001B[38;5;247m"); // or maybe 246
        colors.put(ColorFeature.error_bracket, "\u001B[38;5;124m");


        colors.put(ColorFeature.loading_spinner, "\u001B[38;5;178m"); // or maybe 246
    }
}
