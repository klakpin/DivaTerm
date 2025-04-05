package io.github.klakpin.components.api.choice;

/**
 * Compares Choice options based on their similarity to a given filter string.
 * <p>
 * Implementations should return a similarity score between {@link #MIN_MATCH} and {@link #MAX_MATCH}.
 */
public interface OptionsComparator {
    /**
     * Maximum possible similarity score, indicating a perfect match.
     */
    double MAX_MATCH = 1.0;

    /**
     * Minimum possible similarity score, indicating no match.
     */
    double MIN_MATCH = 0.0;

    /**
     * Calculates the similarity between a filter string and an option.
     *
     * @param filter the string to compare against
     * @param value the option to evaluate
     * @return similarity score between {@link #MIN_MATCH} and {@link #MAX_MATCH},
     *         or null if similarity cannot be determined
     */
    Double getSimilarity(String filter, ChoiceOption value);
}