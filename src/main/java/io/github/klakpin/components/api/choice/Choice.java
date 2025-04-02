package io.github.klakpin.components.api.choice;

import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Terminal;

import java.util.List;

public interface Choice {

    ChoiceOption get();

    List<ChoiceOption> getMulti();

    interface ChoiceBuilder {

        ChoiceBuilder withTerminal(Terminal terminal);

        ChoiceBuilder withColorPalette(ColorPalette palette);

        ChoiceBuilder withQuestion(String question);

        ChoiceBuilder withMaxDisplayResults(int maxResults);

        ChoiceBuilder withMaxSelectResults(int maxSelectResults);

        ChoiceBuilder withMultiSelect(Boolean multiSelect);

        ChoiceBuilder withFilteringEnabled(Boolean isFilteringEnabled);

        ChoiceBuilder withFilterSimilarityCutoff(double cutoff);

        ChoiceBuilder withOptions(List<ChoiceOption> options);

        ChoiceBuilder withOptionsProvider(OptionsProvider provider);

        ChoiceBuilder withOptionsComparator(OptionsComparator provider);

        ChoiceBuilder withDontShowSelected(boolean dontShowSelected);

        Choice build();
    }
}