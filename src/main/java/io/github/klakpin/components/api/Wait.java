package io.github.klakpin.components.api;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;

public interface Wait {
    void waitWhile(String message, CompletableFuture<Void> waitWhile);

    void waitWhileWithDetails(String message, SubmissionPublisher<String> details, int maxLines);
}
