package io.github.klakpin.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Queue;

public class FlushableTestCanvas {

    private static final Logger log = LogManager.getLogger();

    private final char[][] canvas;

    private final Queue<CanvasEvent> events = new ArrayDeque<>();

    private int x = 0;
    private int y = 0;

    public FlushableTestCanvas(int height, int width) {
        canvas = new char[height][width];
    }

    /**
     * <pre>
     * ┌──────────x→
     * │
     * │
     * │
     * │
     * │
     * y
     * ↓
     * </pre>
     */
    public synchronized void append(char input) {
        if (input == '\n') {
            y++;
            x = 0;
        } else if (input == '\r') {
            x = 0;
        } else {
            events.add(new CanvasEvent(x, y, input));
            x++;
        }
    }

    public int[] getBrushPosition() {
        return new int[]{x, y};
    }

    public void setBrushPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public synchronized void flush() {
        while (!events.isEmpty()) {
            var nextInput = events.poll();

            if (nextInput.input != ' ' && nextInput.input != '\u0000') {
                log.trace(nextInput);
            }

            canvas[nextInput.y][nextInput.x] = nextInput.input;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < canvas.length; i++) {
            for (int j = 0; j < canvas[i].length; j++) {
                sb.append(canvas[i][j] == '\u0000' ? ' ' : canvas[i][j]);
            }
            if (i < canvas.length - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }


    public String toTrimmedString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < canvas.length; i++) {
            for (int j = 0; j < canvas[i].length; j++) {
                if (canvas[i][j] != '\u0000') {
                    sb.append(canvas[i][j]);
                }
            }
            if (i < canvas.length - 1) {
                sb.append('\n');
            }
        }
        return sb.toString().trim();
    }

    record CanvasEvent(int x, int y, char input) {
    }
}
