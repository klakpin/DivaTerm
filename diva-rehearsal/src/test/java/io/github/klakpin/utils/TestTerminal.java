package io.github.klakpin.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import org.jline.terminal.*;
import org.jline.utils.ColorPalette;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingPumpReader;
import org.jline.utils.NonBlockingReader;

import static org.jline.utils.InfoCmp.Capability.cursor_address;

public class TestTerminal implements Terminal {

    private final int height = 40;
    private final int width = 150;

    private final FlushableTestCanvas canvas = new FlushableTestCanvas(height, width);
    private final CanvasWriter canvasWriter = new CanvasWriter(canvas);
    private final PrintWriter writer = new PrintWriter(canvasWriter);

    private final NonBlockingPumpReader reader = new NonBlockingPumpReader();

    public NonBlockingPumpReader getReader() {
        return reader;
    }

    public FlushableTestCanvas getCanvas() {
        return canvas;
    }

    @Override
    public String getType() {
        return "test";
    }

    @Override
    public PrintWriter writer() {
        return writer;
    }

    @Override
    public void flush() {
        canvas.flush();
    }

    @Override
    public Cursor getCursorPosition(IntConsumer discarded) {
        var canvasBrush = canvas.getBrushPosition();
        return new Cursor(canvasBrush[0], canvasBrush[1]);
    }

    @Override
    public boolean puts(InfoCmp.Capability capability, Object... params) {
        if (capability == cursor_address) {
//            System.out.printf("Updating cursor address x=%s y=%s%n", params[1], params[0]);
            canvas.setBrushPosition((int) params[1], (int) params[0]);
        }

        return true;
    }

    @Override
    public NonBlockingReader reader() {
        return reader;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void close() throws IOException {
    }

    //// Unused methods

    @Override
    public int getHeight() {
        return Terminal.super.getHeight();
    }

    @Override
    public Size getBufferSize() {
        return Terminal.super.getBufferSize();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public SignalHandler handle(Signal signal, SignalHandler handler) {
        return null;
    }

    @Override
    public void raise(Signal signal) {
    }

    @Override
    public Charset encoding() {
        return null;
    }

    @Override
    public InputStream input() {
        return null;
    }

    @Override
    public OutputStream output() {
        return null;
    }

    @Override
    public boolean canPauseResume() {
        return false;
    }

    @Override
    public void pause() {
    }

    @Override
    public void pause(boolean wait) throws InterruptedException {
    }

    @Override
    public void resume() {
    }

    @Override
    public boolean paused() {
        return false;
    }

    @Override
    public Attributes enterRawMode() {
        return null;
    }

    @Override
    public boolean echo() {
        return false;
    }

    @Override
    public boolean echo(boolean echo) {
        return false;
    }

    @Override
    public Attributes getAttributes() {
        return null;
    }

    @Override
    public void setAttributes(Attributes attr) {
    }

    @Override
    public Size getSize() {
        return null;
    }

    @Override
    public void setSize(Size size) {
    }

    @Override
    public boolean getBooleanCapability(InfoCmp.Capability capability) {
        return false;
    }

    @Override
    public Integer getNumericCapability(InfoCmp.Capability capability) {
        return null;
    }

    @Override
    public String getStringCapability(InfoCmp.Capability capability) {
        return null;
    }

    @Override
    public boolean hasMouseSupport() {
        return false;
    }

    @Override
    public boolean trackMouse(MouseTracking tracking) {
        return false;
    }

    @Override
    public MouseEvent readMouseEvent() {
        return null;
    }

    @Override
    public MouseEvent readMouseEvent(IntSupplier reader) {
        return null;
    }

    @Override
    public boolean hasFocusSupport() {
        return false;
    }

    @Override
    public boolean trackFocus(boolean tracking) {
        return false;
    }

    @Override
    public ColorPalette getPalette() {
        return null;
    }
}