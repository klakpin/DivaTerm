package io.github.klakpin.components;

import io.github.klakpin.components.api.InteractiveChoice;
import io.github.klakpin.components.api.Message;
import io.github.klakpin.components.api.Prompt;
import io.github.klakpin.components.api.Wait;

public interface ComponentsFactory {
    Wait buildWaitComponent();

    InteractiveChoice terminalInteractiveChoice();

    Message buildMessageComponent();

    Prompt buildPrompt();
}