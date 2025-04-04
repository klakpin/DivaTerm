package io.github.klakpin.components.api;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;

public interface Wait {
    /**
     * Display waiting animation until waitWhile is completed.
     */
    void waitWhile(String message, CompletableFuture<Void> waitWhile);

    /**
     * Displays waiting animation and `maxLines` lines of strings coming from details publisher until details publisher
     * is completed.
     */
    void waitWhileWithDetails(String message, SubmissionPublisher<String> details, int maxLines);
}
