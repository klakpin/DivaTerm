package io.github.klakpin.terminal;

import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

import java.io.*;
import java.util.List;

public class TerminalWrapper implements Closeable, Flushable {

    public final static int ARROW_UP = -228;
    public final static int ARROW_DOWN = -1337;
    public final static int SPACE = 32;
    public final static int BACKSPACE = 127;
    public final static int ENTER = 13;

    // Control sequence that is not defined in the code yet
    public final static int UNKNOWN_CONTROL = -999;


    private final Terminal terminal;

    public TerminalWrapper(Terminal terminal) {
        this.terminal = terminal;
    }

    public void setCursorPosition(int row, int column) {
        terminal.puts(InfoCmp.Capability.cursor_address, row, column);
    }

    @Override
    public void close() throws IOException {
        terminal.close();
    }

    @Override
    public void flush() {
        terminal.flush();
    }

    public void puts(InfoCmp.Capability capability, Object... params) {
        terminal.puts(capability, params);
    }

    public Cursor getCursorPosition(NoopIntConsumer noopIntConsumer) {
        return terminal.getCursorPosition(noopIntConsumer);
    }

    public NonBlockingReader reader() {
        return terminal.reader();
    }

    public void printlnFull(String line, int offset) {
        var toFill = terminal.getWidth() - line.length();
        terminal.writer().println(line + " ".repeat(toFill - offset));
    }

    public void printFull(String line, int offset) {
        var toFill = terminal.getWidth() - line.length();
        terminal.writer().print(line + " ".repeat(toFill - offset));
    }

    public void printlnFull(String line) {
        printlnFull(line, 0);
    }

    public void printFull(String text) {
        printFull(text, 0);
    }

    public void emptyLine() {
        terminal.writer().println(" ".repeat(terminal.getWidth()));
    }

    public int getWidth() {
        return terminal.getWidth();
    }

    public PrintWriter writer() {
        return terminal.writer();
    }

    public int pollInput(NonBlockingReader reader) {
        try {
            int initial = reader.read(100);
            if (initial == 27) { // escape char
                var a = reader.read(1);
                var b = reader.read(2);

                if (a == 91 && b == 65) {
                    return ARROW_UP;
                } else if (a == 91 && b == 66) {
                    return ARROW_DOWN;
                }

                return UNKNOWN_CONTROL;
            }
            return initial;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void printPollInfo(Boolean escapeChar, int lastInput, int esc1, int esc2, int esc3) {
        var debugLines = List.of(
                String.format("escape char: '%s'", escapeChar),
                String.format("last esc: [%d|%d|%d]", esc1, esc2, esc3),
                String.format("last input: '%d'", lastInput)
        );

        printDebugInfo("last input", debugLines, 0);
    }

    public void printDebugInfo(String debugName, List<String> debugLines, int row) {
        terminal.puts(InfoCmp.Capability.save_cursor);

        int longestTextLine = debugLines.stream()
                .map(s -> s.length() - nullChars(s))
                .max(Integer::compareTo)
                .orElseThrow();

        int debugNameSize = debugName.length();

        if (debugNameSize + "┌─ ".length() + " ─┐".length() > longestTextLine) {
            longestTextLine = debugNameSize + "┌─ ".length() + " ─┐".length();
        }

        var column = terminal.getWidth() - longestTextLine - 5;

        setCursorPosition(row, column);
        terminal.writer().println("┌─ " + debugName + " " + "─".repeat(longestTextLine - debugNameSize - 2) + "─┐");

        for (int i = 0; i < debugLines.size(); i++) {
            terminal.puts(InfoCmp.Capability.cursor_address, row + i + 1, column);
            terminal.writer().println("│ " + debugLines.get(i) + " ".repeat(longestTextLine - debugLines.get(i).length() + nullChars(debugLines.get(i))) + " │");
        }
        setCursorPosition(row + debugLines.size() + 1, column);
        terminal.writer().println("└" + "─".repeat(longestTextLine + 2) + "┘");

        terminal.puts(InfoCmp.Capability.restore_cursor);
        terminal.flush();
    }

    private int nullChars(String string) {
        var res = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '\u0000') {
                res++;
            }
        }

        return res;
    }
}
