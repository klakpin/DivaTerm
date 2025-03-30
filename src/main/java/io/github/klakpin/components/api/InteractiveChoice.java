package io.github.klakpin.components.api;

import io.github.klakpin.components.impl.choice.OptionsComparator;
import io.github.klakpin.components.impl.choice.OptionsProvider;

public interface InteractiveChoice {
    String interactiveChoice(String question,
                             int maxResults,
                             OptionsProvider optionsProvider,
                             OptionsComparator comparator,
                             Double filterValuesCutoff);
}