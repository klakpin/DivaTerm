package io.github.klakpin.terminal;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import sun.misc.Signal;

import java.io.IOException;

public class JlineTerminalFactory {
    public Terminal buildTerminal() throws IOException {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        // Handle CTRL+C signal separately
        Signal.handle(new Signal("INT"), sig -> {
            terminal.puts(InfoCmp.Capability.cursor_visible);
            terminal.flush();
            try {
                terminal.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        });

        // Handle other exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            terminal.puts(InfoCmp.Capability.cursor_visible);
            terminal.flush();
            try {
                terminal.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        terminal.enterRawMode();
        terminal.puts(InfoCmp.Capability.cursor_invisible);
        terminal.flush();

        return terminal;
    }
}
