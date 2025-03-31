package io.github.klakpin.components.impl;

import io.github.klakpin.components.api.Confirm;
import io.github.klakpin.terminal.NoopIntConsumer;
import io.github.klakpin.terminal.TerminalWrapper;
import io.github.klakpin.theme.ColorPalette;
import io.github.klakpin.theme.ColorPalette.ColorFeature;
import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;
import org.jline.utils.NonBlockingReader;

import static io.github.klakpin.terminal.TerminalWrapper.ARROW_LEFT;
import static io.github.klakpin.terminal.TerminalWrapper.ARROW_RIGHT;
import static io.github.klakpin.theme.ColorPalette.ColorFeature.*;
import static org.jline.utils.InfoCmp.Capability.cursor_address;
import static org.jline.utils.NonBlockingReader.READ_EXPIRED;

public class TerminalConfirm implements Confirm {

    private final TerminalWrapper terminal;
    private final ColorPalette colorPalette;

    private final int HEADER_OFFSET = 2;

    private Cursor initialPosition;
    private boolean state = true;


    public TerminalConfirm(Terminal terminal, ColorPalette colorPalette) {
        this.terminal = new TerminalWrapper(terminal);
        this.colorPalette = colorPalette;
    }

    @Override
    public Boolean confirm() {
        header("Are you sure?");
        confirmationLoop();
        cleanup();
        return state;
    }

    @Override
    public Boolean confirm(String question) {
        header(question);
        confirmationLoop();
        cleanup();
        return state;
    }

    private void confirmationLoop() {
        NonBlockingReader reader = terminal.reader();

        boolean needRedraw = true;
        while (true) {
            if (needRedraw) {
                needRedraw = false;
                drawChoice();
            }

            int input = terminal.pollInput(reader);

            if (input == TerminalWrapper.ENTER) {
                break;
            } else if (input != READ_EXPIRED) {
                updateState(input);
                needRedraw = true;
            }
        }
    }

    private void header(String text) {
        terminal.forwardCleanup(HEADER_OFFSET + 2);
        var currentPosition = terminal.getCursorPosition(new NoopIntConsumer());
        initialPosition = new Cursor(currentPosition.getX(), currentPosition.getY() - HEADER_OFFSET - 2);
        terminal.setCursorPosition(initialPosition.getY(), 0);
        terminal.writer().println("\n " + text);
        terminal.writer().println("\n");

    }

    private void cleanup() {
        terminal.cleanLines(HEADER_OFFSET + 2, initialPosition);
        terminal.puts(cursor_address, initialPosition.getY(), 0);
        terminal.flush();
    }

    private void updateState(int input) {
        if (input == ARROW_LEFT || input == ARROW_RIGHT) {
            state = !state;
        }
    }

    private void drawChoice() {
        terminal.puts(cursor_address, initialPosition.getY() + HEADER_OFFSET + 1, 0);

        String buttons = " " +
                colorPalette.apply("   Yes   ", colorStyles(state)) +
                "   " +
                colorPalette.apply("   No   ", colorStyles(!state));

        terminal.writer().println(buttons);
        terminal.flush();
    }

    private ColorFeature[] colorStyles(boolean active) {
        if (active) {
            return new ColorFeature[]{active_background, bold};
        } else {
            return new ColorFeature[]{inactive_background};
        }
    }
}