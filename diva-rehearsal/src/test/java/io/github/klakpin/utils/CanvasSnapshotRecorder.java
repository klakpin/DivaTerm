package io.github.klakpin.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;

public class CanvasSnapshotRecorder {
    private final FlushableTestCanvas canvas;

    private final List<String> snapshots = new ArrayList<>();

    private static final Logger log = LogManager.getLogger();

    public CanvasSnapshotRecorder(FlushableTestCanvas canvas) {
        this.canvas = canvas;
    }

    public void takeSnapshot() {
        var snapshot = canvas.toTrimmedString();

        log.info("Added snapshot #" + snapshots.size() + "\n" + snapshot + "\n--- end of snapshot #" + snapshots.size());

        snapshots.add(snapshot);
    }

    public void assertSnapshots(List<String> expectedSnapshots) {
        if (expectedSnapshots.size() != snapshots.size()) {
            assertionFailure()
                    .message("Received snapshot list size is not equal to recorded snapshot list")
                    .expected(snapshots.size())
                    .actual(expectedSnapshots.size())
                    .buildAndThrow();
        }

        for (int i = 0; i < expectedSnapshots.size(); i++) {
            var expected = expectedSnapshots.get(i);
            var snapshot = snapshots.get(i);

            var expectedLines = expected.split("\n");
            var snapshotLines = snapshot.split("\n");

            Assertions.assertEquals(expectedLines.length, snapshotLines.length, "Snapshot and expected view are different in length");
            for (int e = 0; e < expectedLines.length; e++) {
                Assertions.assertEquals(expectedLines[e].stripTrailing(), snapshotLines[e].stripTrailing());
            }
        }
    }

    public void printSnapshots() {
        snapshots.forEach(string -> {
            log.info("\n---\n" + string + "\n---\n");
        });
    }
}
