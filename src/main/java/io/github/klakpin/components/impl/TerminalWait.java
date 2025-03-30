package io.github.klakpin.components.impl;

import io.github.klakpin.components.api.Wait;
import io.github.klakpin.terminal.NoopIntConsumer;
import io.github.klakpin.terminal.TerminalWrapper;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import io.github.klakpin.components.helper.TerminalCleaner;
import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

import java.util.concurrent.*;

import static io.github.klakpin.theme.ColorPalette.ColorFeature.*;

public class TerminalWait implements Wait {

    private final Terminal terminal;
    private final ScheduledExecutorService drawingExecutor;
    private final ColorPalette colorPalette;
    private final TerminalCleaner cleaner;

    private int step = -1;
    private final char[] steps = {'⡿', '⣟', '⣯', '⣷', '⣾', '⣽', '⣻', '⢿'};

    private CircularFifoQueue<String> detailsBuffer;
    private Cursor initialPosition;

    public TerminalWait(Terminal terminal,
                        ScheduledExecutorService drawingExecutor,
                        ColorPalette colorPalette,
                        TerminalCleaner cleaner) {
        this.terminal = terminal;
        this.drawingExecutor = drawingExecutor;
        this.colorPalette = colorPalette;
        this.cleaner = cleaner;
    }

    @Override
    public void waitWhile(String message, CompletableFuture<Void> waitWhile) {
        detailsBuffer = null;

        terminal.writer().write("\n");
        terminal.flush();

        initialPosition = terminal.getCursorPosition(new NoopIntConsumer());

        try {
            var task = drawingExecutor.scheduleAtFixedRate(() -> updateAnimation(message), 0, 85, TimeUnit.MILLISECONDS);

            waitWhile.whenComplete((unused, throwable) -> task.cancel(true));

            waitWhile.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
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
    public void waitWhileWithDetails(String message, SubmissionPublisher<String> details, CompletableFuture<Void> waitWhile, int maxLines) {
        cleaner.forwardCleanup(maxLines);

        initialPosition = terminal.getCursorPosition(new NoopIntConsumer());
        initialPosition = new Cursor(initialPosition.getX(), initialPosition.getY() - maxLines);

        detailsBuffer = new CircularFifoQueue<>(maxLines);
        for (int i = 0; i < maxLines; i++) {
            detailsBuffer.add("");
        }

        var consumer = details.consume(s -> detailsBuffer.add(s));

        try {
            var task = drawingExecutor.scheduleAtFixedRate(() -> updateAnimation(message),
                    25 /* small initial delay for forward cleanup to be rendered */,
                    85,
                    TimeUnit.MILLISECONDS
            );

            waitWhile.whenComplete((unused, throwable) -> task.cancel(true));

            waitWhile.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            consumer.cancel(true);
            cleaner.cleanLines(maxLines, initialPosition);
            new TerminalWrapper(terminal).setCursorPosition(initialPosition.getY(), initialPosition.getX());
        }
    }
}


//        NonBlockingReader reader = terminal.reader();
//
//        System.out.println("Hello, waiting 5 seconds (press Ctrl+C to exit)...");
//
//        long endTime = System.currentTimeMillis() + 5000;
//        while (System.currentTimeMillis() < endTime) {
//            // Read any input (with timeout) to prevent it from appearing later
//            int read = reader.read(100); // 100ms timeout
//            terminal.writer().println(read);
//            terminal.writer().flush();
//            terminal.puts(InfoCmp.Capability.cursor_up);
//            terminal.flush();
//            if (read != NonBlockingReader.READ_EXPIRED) {
//                // Just consume the input without processing
//                continue;
//            }
//        }