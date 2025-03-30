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

        Signal.handle(new Signal("INT"), sig -> {
            System.out.println("\nExecution aborted");
            terminal.puts(InfoCmp.Capability.cursor_visible);
            terminal.flush(); // Ensure the cursor is restored
            try {
                terminal.close(); // Cleanup
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        });

        terminal.enterRawMode();
        terminal.puts(InfoCmp.Capability.cursor_invisible);
        terminal.flush();

        return terminal;
    }
}
