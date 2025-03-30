package io.github.klakpin.components.api.choice;

import io.github.klakpin.components.helper.TerminalCleaner;
import io.github.klakpin.terminal.TerminalWrapper;
import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Terminal;

import java.util.List;

public interface Choice {

    ChoiceOption get();

    interface ChoiceBuilder {

        ChoiceBuilder withTerminal(Terminal terminal);

        ChoiceBuilder withColorPalette(ColorPalette palette);

        ChoiceBuilder withQuestion(String question);

        ChoiceBuilder withMaxResults(int maxResults);

        ChoiceBuilder withMultiSelect(Boolean multiSelect);

        ChoiceBuilder withFilteringEnabled(Boolean isFilteringEnabled);

        ChoiceBuilder withFilterSimilarityCutoff(double cutoff);

        ChoiceBuilder withOptions(List<ChoiceOption> options);

        ChoiceBuilder withOptionsProvider(OptionsProvider provider);

        ChoiceBuilder withOptionsComparator(OptionsComparator provider);

        Choice build();
    }
}