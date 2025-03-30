package io.github.klakpin.terminal;

import io.github.klakpin.components.impl.choice.OptionsComparator;
import io.github.klakpin.components.impl.choice.OptionsProvider;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;

public interface TerminalPresenter {

    void message(String message);

    void successMessage(String message);

    void errorMessage(String message);

    void messageInBracket(String message);

    void errorInBracket(String message);

    void waitWhile(String message, CompletableFuture<Void> waitWhile);

    void waitWhileWithDetails(String message, SubmissionPublisher<String> details, CompletableFuture<Void> waitWhile, int maxLines);

    String prompt(String question);

    String promptWithDefault(String question, String defaultValue);

    Boolean promptBoolean(String question);

    void interactiveChoice(String question,
                           int maxResults,
                           OptionsProvider optionsProvider,
                           OptionsComparator comparator,
                           Double filterValuesCutoff);
}
