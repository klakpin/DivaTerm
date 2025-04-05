package io.github.klakpin.components.api.choice;

import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Terminal;

import java.util.List;

/**
 * A component for interactively filtering and selecting one or multiple options from a set of options.
 */
public interface Choice {

    /**
     * Gets a single selected option. If the choice has MaxSelectResults > 1, returns a random element
     * from the selected options.
     *
     * @return the selected option
     */
    ChoiceOption get();

    /**
     * Gets multiple selected options. Should be used when MaxSelectResults > 1; otherwise,
     * returns a list containing a single option.
     *
     * @return a list of selected options
     */
    List<ChoiceOption> getMulti();

    interface ChoiceBuilder {
        /**
         * Sets the underlying terminal for displaying text. This is typically configured by the component factory.
         *
         * @param terminal the terminal to use for display
         * @return this builder for method chaining
         */
        ChoiceBuilder withTerminal(Terminal terminal);

        /**
         * Sets the color palette for displaying text. This is typically configured by the component factory.
         *
         * @param palette the color palette to use
         * @return this builder for method chaining
         */
        ChoiceBuilder withColorPalette(ColorPalette palette);

        /**
         * Sets the question displayed at the top of the choice component.
         *
         * @param question the prompt/question to display
         * @return this builder for method chaining
         */
        ChoiceBuilder withQuestion(String question);

        /**
         * Sets the maximum number of options to display at once. If there are more options than this value,
         * some will be hidden and a hint ("Showing incomplete (%s of %s) options, narrow list using a filter")
         * will be displayed.
         *
         * @param maxResults the maximum number of options to display
         * @return this builder for method chaining
         */
        ChoiceBuilder withMaxDisplayResults(int maxResults);

        /**
         * Sets the maximum number of options that can be selected.
         * <ul>
         *   <li>1: single-selection mode</li>
         *   <li>>1: multi-selection mode</li>
         * </ul>
         *
         * @param maxSelectResults the maximum number of selectable options
         * @return this builder for method chaining
         */
        ChoiceBuilder withMaxSelectResults(int maxSelectResults);

        /**
         * Enables or disables filtering options by typing text.
         *
         * @param isFilteringEnabled true to enable filtering, false to disable
         * @return this builder for method chaining
         */
        ChoiceBuilder withFilteringEnabled(Boolean isFilteringEnabled);

        /**
         * Sets the similarity threshold for filtering options. Options with similarity scores below this value
         * will be hidden. Useful for hiding filtered-out elements.
         * <p>
         * Use with caution as setting this too high may filter out all options if the comparator
         * doesn't account for it properly.
         *
         * @param cutoff the minimum similarity score (0.0 to 1.0)
         * @return this builder for method chaining
         */
        ChoiceBuilder withFilterSimilarityCutoff(double cutoff);

        /**
         * Sets the predefined list of options for selection.
         * <p>
         * Note: Only one of {@code withOptions} or {@code withOptionsProvider} should be used.
         *
         * @param options the list of available options
         * @return this builder for method chaining
         */
        ChoiceBuilder withOptions(List<ChoiceOption> options);

        /**
         * Sets a function that dynamically provides options based on an input filter.
         * <p>
         * Note: Only one of {@code withOptions} or {@code withOptionsProvider} should be used.
         *
         * @param provider the function that provides options
         * @return this builder for method chaining
         */
        ChoiceBuilder withOptionsProvider(OptionsProvider provider);

        /**
         * Sets the comparator function used for sorting and comparing options.
         *
         * @param provider the comparator function
         * @return this builder for method chaining
         */
        ChoiceBuilder withOptionsComparator(OptionsComparator provider);

        /**
         * Configures whether to display the selected option(s). By default, selections are shown.
         *
         * @param dontShowSelected true to hide selected options, false to show them
         * @return this builder for method chaining
         */
        ChoiceBuilder withDontShowSelected(boolean dontShowSelected);

        /**
         * Builds and returns the configured Choice component.
         *
         * @return the configured Choice instance
         */
        Choice build();
    }
}