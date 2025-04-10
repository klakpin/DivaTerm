package io.github.klakpin.components.impl;

import io.github.klakpin.components.api.Wait;
import io.github.klakpin.terminal.NoopIntConsumer;
import io.github.klakpin.terminal.TerminalWrapper;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import io.github.klakpin.components.helper.TerminalCleaner;
import io.github.klakpin.theme.ColorPalette;
import org.apache.commons.collections4.queue.SynchronizedQueue;
import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static io.github.klakpin.theme.ColorPalette.ColorFeature.*;

public class TerminalWait implements Wait {

    private final TerminalWrapper terminal;
    private final ScheduledExecutorService drawingExecutor;
    private final ColorPalette colorPalette;
    private final TerminalCleaner cleaner;

    private int step = -1;
    private final char[] steps = {'⡿', '⣟', '⣯', '⣷', '⣾', '⣽', '⣻', '⢿'};

    private Queue<String> detailsBuffer;
    private Cursor initialPosition;

    public TerminalWait(Terminal terminal,
                        ScheduledExecutorService drawingExecutor,
                        ColorPalette colorPalette,
                        TerminalCleaner cleaner) {
        this.terminal = new TerminalWrapper(terminal);
        this.drawingExecutor = drawingExecutor;
        this.colorPalette = colorPalette;
        this.cleaner = cleaner;
    }

    @Override
    public void waitWhile(String message, CompletableFuture<Void> waitWhile) {
        detailsBuffer = null;

        initialPosition = terminal.getCursorPosition(new NoopIntConsumer());

        try {
            var task = drawingExecutor.scheduleAtFixedRate(() -> updateAnimation(message), 0, 85, TimeUnit.MILLISECONDS);

            waitWhile.whenComplete((unused, throwable) -> task.cancel(true));

            waitWhile.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            cleaner.cleanLines(1, initialPosition);
            terminal.setCursorPosition(initialPosition.getY(), 0);
        }
    }


    private void updateAnimation(String message) {
        terminal.puts(InfoCmp.Capability.cursor_address, initialPosition.getY(), 0);
        terminal.writer().flush();


        if (step < steps.length - 1) {
            step++;
        } else {
            step = 0;
        }

        if (detailsBuffer != null) {
            detailsBuffer.forEach(s -> terminal.writer().println(colorPalette.apply(s, secondary_info)));
        }

        terminal.writer().print(colorPalette.apply(String.valueOf(steps[step]), loading_spinner) + " " + message);
    }

    @Override
    public void waitWhileWithDetails(String message, SubmissionPublisher<String> details, int maxLines) {
        cleaner.forwardCleanup(maxLines);

        initialPosition = terminal.getCursorPosition(new NoopIntConsumer());
        initialPosition = new Cursor(initialPosition.getX(), initialPosition.getY() - maxLines);

        initDetailsBuffer(maxLines);

        try {
            var drawingTask = drawingExecutor.scheduleAtFixedRate(() -> updateAnimation(message),
                    25 /* small initial delay for forward cleanup to be rendered */,
                    85,
                    TimeUnit.MILLISECONDS
            );

            var waitingSemaphore = new Semaphore(1);
            waitingSemaphore.acquire();

            var subscriber = new BufferFillingSubscriber(detailsBuffer, throwable -> {
                drawingTask.cancel(false);
                waitingSemaphore.release();
            });

            details.subscribe(subscriber);
            waitingSemaphore.acquire();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // +1 for the line with a spinner
            cleaner.cleanLines(maxLines + 1, initialPosition);
            terminal.setCursorPosition(initialPosition.getY(), initialPosition.getX());
        }
    }

    private void initDetailsBuffer(int maxLines) {
        detailsBuffer = SynchronizedQueue.synchronizedQueue(new CircularFifoQueue<>(maxLines));
        for (int i = 0; i < maxLines; i++) {
            detailsBuffer.add("");
        }
    }


    /**
     * Subscriber that is used solely to track completion of a given flow.
     */
    static final class BufferFillingSubscriber implements Flow.Subscriber<String> {

        private final Consumer<Throwable> completionConsumer;
        private final Queue<String> detailsBuffer;

        BufferFillingSubscriber(Queue<String> detailsBuffer,
                                Consumer<Throwable> completionConsumer) {
            this.detailsBuffer = detailsBuffer;
            this.completionConsumer = completionConsumer;
        }

        public void onSubscribe(Flow.Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        public void onError(Throwable ex) {
            completionConsumer.accept(ex);
        }

        public void onComplete() {
            completionConsumer.accept(null);
        }

        public void onNext(String item) {
            detailsBuffer.add(item);
        }
    }
}