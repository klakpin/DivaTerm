package io.github.klakpin.components.impl.choice;

import io.github.klakpin.components.api.choice.Choice;
import io.github.klakpin.components.api.choice.ChoiceOption;
import io.github.klakpin.components.api.choice.OptionsComparator;
import io.github.klakpin.components.api.choice.OptionsProvider;
import io.github.klakpin.terminal.TerminalWrapper;
import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Terminal;

import java.util.ArrayList;
import java.util.List;

public class TerminalChoiceBuilder implements Choice.ChoiceBuilder {

    private TerminalWrapper terminalWrapper;
    private ColorPalette colorPalette;

    private String question = null;
    private int maxDisplayResults = 10;
    private int maxSelectResults = 10;
    private boolean multiSelect = false;
    private boolean filteringEnabled = false;

    private Double filteringSimilarityCutoff = 0.0;
    private List<ChoiceOption> options = null;
    private OptionsProvider optionsProvider = null;

    private OptionsComparator optionsComparator = null;


    @Override
    public Choice.ChoiceBuilder withTerminal(Terminal terminal) {
        this.terminalWrapper = new TerminalWrapper(terminal);
        return this;
    }

    @Override
    public Choice.ChoiceBuilder withColorPalette(ColorPalette palette) {
        this.colorPalette = palette;
        return this;
    }

    @Override
    public Choice.ChoiceBuilder withQuestion(String question) {
        this.question = question;
        return this;
    }

    @Override
    public Choice.ChoiceBuilder withMaxDisplayResults(int maxDisplayResults) {
        this.maxDisplayResults = maxDisplayResults;
        return this;
    }

    @Override
    public Choice.ChoiceBuilder withMaxSelectResults(int maxSelectResults) {
        this.maxSelectResults = maxSelectResults;
        return this;
    }

    @Override
    public Choice.ChoiceBuilder withMultiSelect(Boolean multiSelect) {
        this.multiSelect = multiSelect;
        return this;
    }

    @Override
    public Choice.ChoiceBuilder withFilteringEnabled(Boolean isFilteringEnabled) {
        this.filteringEnabled = isFilteringEnabled;
        return this;
    }

    @Override
    public Choice.ChoiceBuilder withFilterSimilarityCutoff(double cutoff) {
        this.filteringSimilarityCutoff = cutoff;
        return this;
    }

    @Override
    public Choice.ChoiceBuilder withOptions(List<ChoiceOption> options) {
        this.optionsProvider = null;
        this.options = new ArrayList<>(options);
        return this;
    }

    @Override
    public Choice.ChoiceBuilder withOptionsProvider(OptionsProvider provider) {
        this.options = null;
        this.optionsProvider = provider;
        return this;
    }

    @Override
    public Choice.ChoiceBuilder withOptionsComparator(OptionsComparator comparator) {
        this.optionsComparator = comparator;
        return this;
    }

    @Override
    public Choice build() {
        return new TerminalChoice(
                terminalWrapper,
                colorPalette,
                question,
                maxDisplayResults,
                maxSelectResults,
                multiSelect,
                filteringEnabled,
                filteringSimilarityCutoff,
                options,
                optionsProvider,
                optionsComparator
        );
    }
}
