package io.github.klakpin.utils;

import java.io.PrintWriter;
import java.io.Writer;

public class CanvasWriter extends Writer {

    private final FlushableTestCanvas canvas;

    public CanvasWriter(FlushableTestCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        for (int i = off; i < off + len; i++) {
            canvas.append(cbuf[i]);
        }
    }

    @Override
    public void flush() {
        // Flush called by terminal
    }

    @Override
    public void close() {
    }
}
