package io.github.klakpin.components.helper;

import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

public class TerminalCleaner {

    private final Terminal terminal;

    public TerminalCleaner(Terminal terminal) {
        this.terminal = terminal;
    }

    public void cleanLines(int size, Cursor sourcePosition) {
        var terminalWidth = terminal.getWidth();
        var emptyLine = " ".repeat(terminalWidth);

        terminal.puts(InfoCmp.Capability.cursor_address, sourcePosition.getY(), 0);

        for (int i = 0; i < size; i++) {
            terminal.writer().println(emptyLine);

        }
        terminal.flush();
    }

    public void forwardCleanup(int size) {
        for (int i = 0; i < size; i++) {
            terminal.writer().println(" ".repeat(terminal.getWidth()));
        }
        terminal.flush();
    }
}