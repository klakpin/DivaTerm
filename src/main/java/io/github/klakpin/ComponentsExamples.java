package io.github.klakpin;

import io.github.klakpin.components.TerminalComponentFactory;
import io.github.klakpin.terminal.ConsoleTerminalPresenter;
import io.github.klakpin.terminal.JlineTerminalFactory;
import io.github.klakpin.terminal.TerminalPresenter;
import io.github.klakpin.theme.TerminalColorPalette;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Stream;

public class ComponentsExamples {
    void runExamples() throws IOException, InterruptedException {
        var terminal = new JlineTerminalFactory().buildTerminal();
        var executor = Executors.newSingleThreadScheduledExecutor();
        var componentsFactory = new TerminalComponentFactory(terminal, executor, new TerminalColorPalette());
        var presenter = new ConsoleTerminalPresenter(componentsFactory);

        try {
//            prompt(presenter);
//            messages(presenter);
            interactiveChoice(presenter);
//            waitWithDetails(presenter);
//            waitWithoutDetails(presenter);
        } finally {
            terminal.puts(InfoCmp.Capability.cursor_visible);
            terminal.flush();
            terminal.close();
            executor.shutdownNow();
        }
    }

    private void prompt(TerminalPresenter presenter) {
        var input = presenter.prompt("Some question");
        presenter.message(input);

        var inputWithDefaultValue = presenter.promptWithDefault("Some question with default value", "default");
        presenter.message(inputWithDefaultValue);

        var inputBoolean = presenter.promptBoolean("Question with boolean result");
        presenter.message(inputBoolean.toString());
    }

    private void messages(TerminalPresenter presenter) {
        presenter.message("Simple message");
        presenter.successMessage("Success message");
        presenter.errorMessage("Error message");
        presenter.messageInBracket("Some\npayload\nin\nbracket");
        presenter.errorInBracket("Some\nerror\nin\nbracket");
    }

    private void interactiveChoice(TerminalPresenter presenter) {
        var result = presenter.stringChoice("test question", List.of("first", "second", "third", "fourth", "fifth"));
//        var result = presenter.stringMultiChoice("test question", List.of("first", "second", "third", "fourth", "fifth"), 3);
        
        presenter.message(result.toString());
    }

    private void waitWithoutDetails(TerminalPresenter presenter) {
        presenter.waitWhile("Waiting 5 seconds", CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private void waitWithDetails(TerminalPresenter presenter) throws InterruptedException {
        var publisher = new SubmissionPublisher<String>();

        int maxCount = 50;

        presenter.waitWhileWithDetails("Counting up to " + maxCount, publisher, CompletableFuture.runAsync(() -> {
            int counter = 0;
            try {
                while (counter < maxCount) {
                    publisher.submit(Integer.toString(counter));
                    counter++;
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }), 10);

        publisher.close();
    }
}
