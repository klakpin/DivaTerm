package io.github.klakpin.components.api.choice;

import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Terminal;

import java.util.List;

public interface Choice {

    /**
     * Get a single choice result. If choice has MaxSelectResults > 1 will return a random element
     */
    ChoiceOption get();

    /**
     * Get a multiple choice results. Should be used with parameter MaxSelectResults > 1, otherwise it will return
     * a list with one option.
     */
    List<ChoiceOption> getMulti();

    interface ChoiceBuilder {
        /**
         * Underlying terminal for displaying text. Should be filled by component factory
         */
        ChoiceBuilder withTerminal(Terminal terminal);

        /**
         * Color palette for displaying text. Should be filled by component factory
         */
        ChoiceBuilder withColorPalette(ColorPalette palette);

        /**
         * Question that will be displayed at the first line of choice.
         */
        ChoiceBuilder withQuestion(String question);

        /**
         * Maximum number of displayed options lines. If the number of options is greater than MaxDisplayResults, then
         * some lines will be invisible and hint "Showing incomplete (%s of %s) options, narrow list using a filter" will be printed
         */
        ChoiceBuilder withMaxDisplayResults(int maxResults);

        /**
         * Number of results that can be selected.
         * 1: choice will be displayed in single-selection mode
         * > 1: choice will be displayed in multi-selection mode
         */
        ChoiceBuilder withMaxSelectResults(int maxSelectResults);

        /**
         * Disable elements filtering by typing some text.
         */
        ChoiceBuilder withFilteringEnabled(Boolean isFilteringEnabled);

        /**
         * Hide choice if options comparator returns value less that filter similarity. Can be useful when
         * you want to hide filtered out elements.
         * <p>
         * Use with caution because it may filter everything out if comparator doesn't account for it.
         */
        ChoiceBuilder withFilterSimilarityCutoff(double cutoff);

        /**
         * Predefined list of options for a choice.
         * <p>
         * One of `Options` or `OptionsProvider` should be used, not both.
         */
        ChoiceBuilder withOptions(List<ChoiceOption> options);

        /**
         * Function that will provide a list of options based on input filter.
         * <p>
         * One of `Options` or `OptionsProvider` should be used, not both.
         */
        ChoiceBuilder withOptionsProvider(OptionsProvider provider);

        /**
         * Function used for comparison and sorting options.
         */
        ChoiceBuilder withOptionsComparator(OptionsComparator provider);

        /**
         * By default, a choice element will display a selected option. Pass false to this method to not show it.
         */
        ChoiceBuilder withDontShowSelected(boolean dontShowSelected);

        Choice build();
    }
}