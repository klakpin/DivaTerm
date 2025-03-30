package io.github.klakpin.components.api.choice;

import java.util.List;

public interface OptionsProvider {
    List<ChoiceOption> get(String filter);
}
