package io.github.klakpin.component;

import io.github.klakpin.components.api.choice.ChoiceOption;
import io.github.klakpin.components.api.choice.comparator.FuzzyDisplayTextComparator;
import io.github.klakpin.components.impl.choice.TerminalChoiceBuilder;
import io.github.klakpin.utils.CanvasSnapshotRecorder;
import io.github.klakpin.utils.NoopColorPalette;
import io.github.klakpin.utils.TestTerminal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

public class TerminalChoiceTest {

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
    public void shouldRunChoice() throws IOException, ExecutionException, InterruptedException {
        var future = CompletableFuture.supplyAsync(() -> {
            var options = List.of(new ChoiceOption(1, "1"), new ChoiceOption(2, "2"));
            var choice = new TerminalChoiceBuilder()
                    .withTerminal(testTerminal)
                    .withColorPalette(new NoopColorPalette())
                    .withQuestion("Test question")
                    .withFilteringEnabled(true)
                    .withOptionsComparator(new FuzzyDisplayTextComparator())
                    .withOptions(options)
                    .build();

            var result = choice.get();

            return result.displayText();
        });

        Thread.sleep(FLUSH_TIMEOUT);
        canvasSnapshots.takeSnapshot();

        reader.getWriter().write(new char[]{27, 91, 66}); // arrow down

        Thread.sleep(FLUSH_TIMEOUT);
        canvasSnapshots.takeSnapshot();

        reader.getWriter().write(new char[]{ENTER});

        var result = future.get();
        canvasSnapshots.takeSnapshot();

        Assertions.assertEquals("1", result);

        canvasSnapshots.assertSnapshots(List.of(
                """
                        ? Test question:                                                                                                                                     \s
                        > 2                                                                                                                                                  \s
                          1                                                                                                                                                  \s
                                                                                                                                                                             \s
                         arrows - move, enter - confirm, type to filter""".stripIndent(),
                """
                        ? Test question:                                                                                                                                     \s
                          2                                                                                                                                                  \s
                        > 1                                                                                                                                                  \s
                                                                                                                                                                             \s
                         arrows - move, enter - confirm, type to filter""".stripIndent(),
                """
                        ✔ Test question: 1""".stripIndent()
        ));
    }
}
