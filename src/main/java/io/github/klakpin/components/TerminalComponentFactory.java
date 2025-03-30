package io.github.klakpin.components;

import io.github.klakpin.components.api.InteractiveChoice;
import io.github.klakpin.components.api.Message;
import io.github.klakpin.components.api.Prompt;
import io.github.klakpin.components.api.Wait;
import io.github.klakpin.components.helper.TerminalCleaner;
import io.github.klakpin.components.impl.TerminalMessage;
import io.github.klakpin.components.impl.TerminalPrompt;
import io.github.klakpin.components.impl.choice.TerminalInteractiveChoice;
import io.github.klakpin.components.impl.TerminalWait;
import io.github.klakpin.theme.ColorPalette;
import org.jline.terminal.Terminal;

import java.util.concurrent.ScheduledExecutorService;

public class TerminalComponentFactory implements ComponentsFactory {

    private final Terminal terminal;
    private final ScheduledExecutorService drawingExecutor;
    private final ColorPalette colorPalette;

    public TerminalComponentFactory(Terminal terminal, ScheduledExecutorService drawingExecutor, ColorPalette colorPalette) {
        this.terminal = terminal;
        this.drawingExecutor = drawingExecutor;
        this.colorPalette = colorPalette;
    }

    @Override
    public Wait buildWaitComponent() {
        return new TerminalWait(terminal, drawingExecutor, colorPalette, cleaner());
    }

    @Override
    public InteractiveChoice terminalInteractiveChoice() {
        return new TerminalInteractiveChoice(terminal, drawingExecutor, colorPalette, cleaner());
    }

    @Override
    public Message buildMessageComponent() {
        return new TerminalMessage(terminal, colorPalette);
    }

    @Override
    public Prompt buildPrompt() {
        return new TerminalPrompt(terminal);
    }

    private TerminalCleaner cleaner() {
        return new TerminalCleaner(terminal);
    }
}
