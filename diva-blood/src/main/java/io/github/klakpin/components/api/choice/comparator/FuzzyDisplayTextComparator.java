package io.github.klakpin.components.api.choice.comparator;

import io.github.klakpin.components.api.choice.ChoiceOption;
import io.github.klakpin.components.api.choice.OptionsComparator;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;

/**
 * Display text comparator that uses the Jaro Winker algorithm for string similarity comparison.
 */
public class FuzzyDisplayTextComparator implements OptionsComparator {

    @Override
    public Double getSimilarity(String filter, ChoiceOption value) {
        return new JaroWinklerSimilarity().apply(value.displayText().toLowerCase(), filter.toLowerCase());
    }
}
