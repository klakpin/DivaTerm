package io.github.klakpin.component;

import io.github.klakpin.components.impl.TerminalConfirm;
import io.github.klakpin.components.impl.TerminalPrompt;
import io.github.klakpin.utils.CanvasSnapshotRecorder;
import io.github.klakpin.utils.NoopColorPalette;
import io.github.klakpin.utils.TestTerminal;
import org.jline.utils.NonBlockingPumpReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.github.klakpin.component.TestConstants.FLUSH_TIMEOUT;
import static io.github.klakpin.terminal.TerminalWrapper.ENTER;

public class TerminalConfirmTest {
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
    public void shouldRunConfirm() throws ExecutionException, InterruptedException, IOException {
        var promptResult = CompletableFuture.supplyAsync(() -> {
            var confirm = new TerminalConfirm(testTerminal, NoopColorPalette.INSTANCE);
            return confirm.confirm();
        });

        Thread.sleep(FLUSH_TIMEOUT);
        canvasSnapshots.takeSnapshot();

        reader.getWriter().write(new char[]{27, 91, 67}); // arrow right
        Thread.sleep(FLUSH_TIMEOUT);
        canvasSnapshots.takeSnapshot();


        reader.getWriter().write(new char[]{ENTER});
        Thread.sleep(FLUSH_TIMEOUT);
        canvasSnapshots.takeSnapshot();

        var result = promptResult.get();

        canvasSnapshots.assertSnapshots(List.of(
                """
                        Are you sure?                                                                                                                                       \s
                                                                                                                                                                             \s
                            Yes         No
                        """,
                """
                        Are you sure?                                                                                                                                       \s
                                                                                                                                                                             \s
                            Yes         No
                        """,
                ""
        ));

        Assertions.assertEquals(false, result);
    }
}
