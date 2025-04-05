package io.github.klakpin.components.api;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;

/**
 * Component for waiting animation for long-running tasks.
 */
public interface Wait {
    /**
     * Blocks current thread, displays a waiting animation until the {@code waitWhile} future completes.
     *
     * @param message   the message to display while waiting
     * @param waitWhile the future whose completion will end the waiting animation
     */
    void waitWhile(String message, CompletableFuture<Void> waitWhile);

    /**
     * Blocks current thread, displays a waiting animation with additional details until the details publisher completes.
     * Shows up to {@code maxLines} of detail messages.
     *
     * @param message  the primary message to display while waiting
     * @param details  the publisher providing additional detail messages
     * @param maxLines the maximum number of detail lines to display
     */
    void waitWhileWithDetails(String message, SubmissionPublisher<String> details, int maxLines);
}