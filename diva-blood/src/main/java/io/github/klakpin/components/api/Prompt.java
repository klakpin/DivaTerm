package io.github.klakpin.components.api;

import java.util.Optional;

public interface Prompt {
    String prompt(String text);

    String prompt(String text, String defaultValue);
}
