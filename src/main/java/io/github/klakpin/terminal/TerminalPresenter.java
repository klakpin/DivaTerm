package io.github.klakpin.terminal;

import io.github.klakpin.components.api.choice.Choice;
import io.github.klakpin.components.api.choice.ChoiceOption;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

public interface TerminalPresenter extends AutoCloseable {

    void message(String message);

    void successMessage(String message);

    void errorMessage(String message);

    void messageInBracket(String message);

    void errorInBracket(String message);

    void waitWhile(String message, CompletableFuture<Void> waitWhile);

    void waitWhileWithDetails(String message, SubmissionPublisher<String> details, int maxLines);

    String prompt(String question);

    String promptWithDefault(String question, String defaultValue);

    Boolean promptBoolean(String question);

    String stringChoice(String question, List<String> options);

    List<String> stringMultiChoice(String question, List<String> options, int limit);

    List<ChoiceOption> stringMultiChoiceRaw(Function<Choice.ChoiceBuilder, Choice.ChoiceBuilder> builder);

    Boolean confirm();
}
