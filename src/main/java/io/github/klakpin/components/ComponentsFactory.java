package io.github.klakpin.components;

import io.github.klakpin.components.api.choice.Choice;
import io.github.klakpin.components.api.Message;
import io.github.klakpin.components.api.Prompt;
import io.github.klakpin.components.api.Wait;

public interface ComponentsFactory extends AutoCloseable {
    Wait buildWaitComponent();

    Choice.ChoiceBuilder choiceBuilder();

    Message buildMessageComponent();

    Prompt buildPrompt();
}