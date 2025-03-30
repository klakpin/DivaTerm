package io.github.klakpin.components.impl.choice;

/*
const val maxMatch = 1.0
const val minMatch = 0.0
 */
public interface OptionsComparator {
    Double getSimilarity(String filter, String value);
}
