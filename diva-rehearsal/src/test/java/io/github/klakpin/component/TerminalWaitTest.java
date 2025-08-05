package io.github.klakpin.component;

import io.github.klakpin.components.impl.TerminalWait;
import io.github.klakpin.utils.CanvasSnapshotRecorder;
import io.github.klakpin.utils.NoopColorPalette;
import io.github.klakpin.utils.TestTerminal;
import org.jline.utils.NonBlockingPumpReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class TerminalWaitTest {

    TestTerminal testTerminal;
    NonBlockingPumpReader reader;
    CanvasSnapshotRecorder canvasSnapshots;

    @BeforeEach
    public void setup() {
        testTerminal = new TestTerminal();
        reader = testTerminal.getReader();
        canvasSnapshots = new CanvasSnapshotRecorder(testTerminal.getCanvas());
    }

    @Test
    public void shouldRunWait() throws ExecutionException, InterruptedException {
        var testWaitingMessage = "Test waiting message";

        var future = CompletableFuture.runAsync(() ->
                new TerminalWait(testTerminal, Executors.newSingleThreadScheduledExecutor(), new NoopColorPalette())
                .waitWhile(testWaitingMessage, CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })));

        Thread.sleep(300);
        canvasSnapshots.takeSnapshot();

        Thread.sleep(100);
        canvasSnapshots.takeSnapshot();

        future.get();
        canvasSnapshots.assertSnapshots(List.of(
                "⣯ Test waiting message",
                "⣷ Test waiting message"
        ));
    }

    @Test
    public void shouldRunWaitWithResult() throws ExecutionException, InterruptedException {
        var testWaitingMessage = "Test waiting message with result";

        String expectedResult = "Test waiting message with result";
        AtomicReference<String> waitingResult = new AtomicReference<>();


        var future = CompletableFuture.runAsync(() ->
                waitingResult.set(new TerminalWait(testTerminal, Executors.newSingleThreadScheduledExecutor(), new NoopColorPalette())
                .waitWhileWithResult(testWaitingMessage, CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                        return expectedResult;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }))));

        Thread.sleep(300);
        canvasSnapshots.takeSnapshot();

        Thread.sleep(100);
        canvasSnapshots.takeSnapshot();

        future.get();
        canvasSnapshots.assertSnapshots(List.of(
                "⣯ Test waiting message with result",
                "⣷ Test waiting message with result"
        ));

        Assertions.assertEquals(expectedResult, waitingResult.get());
    }
}
