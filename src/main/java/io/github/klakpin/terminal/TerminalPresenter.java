package io.github.klakpin.terminal;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;

public interface TerminalPresenter extends AutoCloseable {

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

    String stringChoice(String question, List<String> options);

    List<String> stringMultiChoice(String question, List<String> options, int limit);
}
