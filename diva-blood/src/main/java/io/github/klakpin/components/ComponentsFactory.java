package io.github.klakpin.components;

import io.github.klakpin.components.api.Confirm;
import io.github.klakpin.components.api.choice.Choice;
import io.github.klakpin.components.api.Message;
import io.github.klakpin.components.api.Prompt;
import io.github.klakpin.components.api.Wait;

public interface ComponentsFactory extends AutoCloseable {
    /**
     * Builds a Wait component.
     *
     * @return a new Wait component instance
     */
    Wait buildWaitComponent();

    /**
     * Creates a new choice builder for constructing and executing choices.
     *
     * @return a new ChoiceBuilder instance
     */
    Choice.ChoiceBuilder choiceBuilder();

    /**
     * Builds a Message component.
     *
     * @return a new Message component instance
     */
    Message buildMessageComponent();

    /**
     * Builds a Prompt component.
     *
     * @return a new Prompt component instance
     */
    Prompt buildPrompt();

    /**
     * Builds a Confirm component.
     *
     * @return a new Confirm component instance
     */
    Confirm buildConfirm();
}