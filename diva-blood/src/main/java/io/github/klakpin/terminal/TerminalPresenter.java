package io.github.klakpin.terminal;

import io.github.klakpin.components.api.choice.Choice;
import io.github.klakpin.components.api.choice.ChoiceOption;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface TerminalPresenter extends AutoCloseable {

    /**
     * Displays a basic message using the {@link io.github.klakpin.components.api.Wait} component.
     *
     * @param message the text to display
     */
    void message(String message);

    /**
     * Displays a success message (with success coloring) using the {@link io.github.klakpin.components.api.Wait} component.
     *
     * @param message the text to display
     */
    void successMessage(String message);

    /**
     * Displays an error message (with error coloring) using the {@link io.github.klakpin.components.api.Wait} component.
     *
     * @param message the text to display
     */
    void errorMessage(String message);

    /**
     * Displays a basic message enclosed in square brackets using the {@link io.github.klakpin.components.api.Message} component.
     *
     * @param message the text to display
     */
    void messageInBracket(String message);

    /**
     * Displays an error-styled message enclosed in square brackets using the {@link io.github.klakpin.components.api.Message} component.
     *
     * @param message the text to display
     */
    void errorInBracket(String message);

    /**
     * Blocks the current thread and displays a spinner animation while waiting for the {@code waitWhile} future to complete.
     * Uses the {@link io.github.klakpin.components.api.Wait} component.
     *
     * @param message   the message to display during the wait
     * @param waitWhile the future whose completion will end the wait
     */
    void waitWhile(String message, CompletableFuture<Void> waitWhile);

    /**
     * Blocks the current thread, prints lines from the publisher, and waits until the details publisher is closed.
     * Uses the {@link io.github.klakpin.components.api.Wait} component.
     *
     * @param message  the message to display during the wait
     * @param details  the publisher providing additional details to display
     * @param maxLines the maximum number of detail lines to display
     */
    void waitWhileWithDetails(String message, SubmissionPublisher<String> details, int maxLines);

    /**
     * Displays a prompt and waits for user input using the {@link io.github.klakpin.components.api.Prompt} component.
     *
     * @param question the prompt question to display
     * @return the user's input
     */
    String prompt(String question);

    /**
     * Displays a prompt and waits for user input, returning the default value if no input is provided.
     * Uses the {@link io.github.klakpin.components.api.Prompt} component.
     *
     * @param question     the prompt question to display
     * @param defaultValue the value to return if no input is provided
     * @return the user's input or the default value
     */
    String promptWithDefault(String question, String defaultValue);

    /**
     * Provides a simplified single-choice selection from string options using the {@link io.github.klakpin.components.api.choice.Choice} component.
     *
     * @param question the prompt question to display
     * @param options  the available choices
     * @return the selected option
     */
    String stringChoice(String question, List<String> options);

    /**
     * Provides a simplified multiple-choice selection from string options using the {@link io.github.klakpin.components.api.choice.Choice} component.
     *
     * @param question the prompt question to display
     * @param options  the available choices
     * @param limit    the maximum number of selections allowed
     * @return the list of selected options
     */
    List<String> stringMultiChoice(String question, List<String> options, int limit);

    /**
     * Creates a choice selection using a builder pattern with the {@link io.github.klakpin.components.api.choice.Choice} component.
     *
     * @param builder the function to configure the choice builder
     * @return the selected option
     */
    ChoiceOption choice(Function<Choice.ChoiceBuilder, Choice.ChoiceBuilder> builder);

    /**
     * Creates a multiple-choice selection using a builder pattern with the {@link io.github.klakpin.components.api.choice.Choice} component.
     *
     * @param builder the function to configure the choice builder
     * @return the list of selected options
     */
    List<ChoiceOption> multiChoice(Function<Choice.ChoiceBuilder, Choice.ChoiceBuilder> builder);

    /**
     * Displays a confirmation dialog with default text using the {@link io.github.klakpin.components.api.Confirm} component.
     *
     * @return {@code true} if confirmed, {@code false} otherwise
     */
    Boolean confirm();

    /**
     * Displays a confirmation dialog with custom text using the {@link io.github.klakpin.components.api.Confirm} component.
     *
     * @param confirmationText the custom confirmation prompt text
     * @return {@code true} if confirmed, {@code false} otherwise
     */
    Boolean confirm(String confirmationText);
}
