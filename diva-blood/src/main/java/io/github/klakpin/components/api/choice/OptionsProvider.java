package io.github.klakpin.components.api.choice;

import java.util.List;

/**
 * Provides options for a Choice terminal component based on user input. Can be useful in complex input scenarios
 * when you want to give a set of options based on input.
 * <p>
 * OptionsProvider does not cancel the consecutive filtering and sorting.
 */
public interface OptionsProvider {

    /**
     * Retrieves a list of options matching the user's filter input.
     *
     * @param filter the filter string entered by the user; an empty string if no input was provided
     * @return a list of options for a Choice element.
     */
    List<ChoiceOption> get(String filter);
}