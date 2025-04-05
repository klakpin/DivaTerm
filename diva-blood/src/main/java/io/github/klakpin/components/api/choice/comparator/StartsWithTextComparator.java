package io.github.klakpin.components.api.choice.comparator;

import io.github.klakpin.components.api.choice.ChoiceOption;
import io.github.klakpin.components.api.choice.OptionsComparator;

/**
 * Display text comparator that gives max match if choice starts with filter.
 */
public class StartsWithTextComparator implements OptionsComparator {

    @Override
    public Double getSimilarity(String filter, ChoiceOption value) {
        if (value.displayText().startsWith(filter)) {
            return MAX_MATCH;
        } else {
            return MIN_MATCH;
        }
    }
}
