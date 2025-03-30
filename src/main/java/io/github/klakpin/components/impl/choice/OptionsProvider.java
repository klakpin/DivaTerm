package io.github.klakpin.components.impl.choice;

import java.util.List;

public interface OptionsProvider {
    List<String> get(String filter);
}
