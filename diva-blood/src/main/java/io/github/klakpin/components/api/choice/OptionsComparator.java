package io.github.klakpin.components.api.choice;

/**
 * Used for comparing options for sorting
 */
public interface OptionsComparator {
    double MAX_MATCH = 1.0;
    double MIN_MATCH = 0.0;

    Double getSimilarity(String filter, ChoiceOption value);

}
