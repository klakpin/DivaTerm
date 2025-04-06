package io.github.klakpin.component;

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

public class TerminalPromptTest {

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
    public void shouldRunPromptWithDefault() throws ExecutionException, InterruptedException, IOException {
        var expectedPromptResult = "default value";

        var promptResult = CompletableFuture.supplyAsync(() -> {
            var prompt = new TerminalPrompt(testTerminal, NoopColorPalette.INSTANCE);
            return prompt.prompt("Test prompt", expectedPromptResult);
        });


        Thread.sleep(FLUSH_TIMEOUT);
        canvasSnapshots.takeSnapshot();

        reader.getWriter().write(new char[]{ENTER});

        var result = promptResult.get();

        Thread.sleep(FLUSH_TIMEOUT);
        canvasSnapshots.takeSnapshot();


        canvasSnapshots.assertSnapshots(List.of(
                "? Test prompt (default value):",
                "✔ Test prompt: default value"
        ));

        Assertions.assertEquals(expectedPromptResult, result);
    }
}
