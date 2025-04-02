package io.github.klakpin;

import io.github.klakpin.terminal.ConsoleTerminalPresenter;
import io.github.klakpin.terminal.TerminalPresenter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;

public class ComponentsExamples {
    void runDemo() {
        try (var presenter = ConsoleTerminalPresenter.standard()) {
            var choice = presenter.stringChoice("What action you want to perform?",
                    List.of("first", "second", "third", "fourth", "fifth"));

            presenter.confirm(choice + " - are you sure?");

            presenter.successMessage("Action completed");
        }
    }

    void runExamples() {
        try (var presenter = ConsoleTerminalPresenter.standard()) {
            messages(presenter);
            prompt(presenter);
            interactiveChoice(presenter);
            confirm(presenter);
            waitWithDetails(presenter);
            waitWithoutDetails(presenter);
        }
    }

    private void confirm(TerminalPresenter presenter) {
        presenter.message(presenter.confirm().toString());
    }

    private void prompt(TerminalPresenter presenter) {
        var input = presenter.prompt("Some question");
        presenter.message(input);

        var inputWithDefaultValue = presenter.promptWithDefault("Some question with default value", "default");
        presenter.message(inputWithDefaultValue);
    }

    private void messages(TerminalPresenter presenter) {
        presenter.message("Simple message");
        presenter.successMessage("Success message");
        presenter.errorMessage("Error message");
        presenter.messageInBracket("Some\npayload\nin\nbracket");
        presenter.errorInBracket("Some\nerror\nin\nbracket");
    }

    private void interactiveChoice(TerminalPresenter presenter) {
//        var lotOfOptions = new ArrayList<ChoiceOption>();
//
//        for (int i = 0; i < 100; i++) {
//            lotOfOptions.add(new ChoiceOption(i, UUID.randomUUID().toString()));
//        }
//
//        var filteredResult = presenter.choice(cb ->
//                cb.withQuestion("What UUID you like the most?")
//                        .withOptions(lotOfOptions)
//                        .withMaxDisplayResults(5)
//                        .withFilteringEnabled(true)
//                        .withFilterSimilarityCutoff(0.8)
//        );

//        presenter.message(filteredResult.displayText());
        presenter.stringChoice("test question", List.of("first", "second", "third", "fourth", "fifth"));
        presenter.stringMultiChoice("test question", List.of("first", "second", "third", "fourth", "fifth"), 3);
    }

    private void waitWithoutDetails(TerminalPresenter presenter) {
        presenter.waitWhile("Waiting 5 seconds", CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
        presenter.message("Waited 5 seconds");
    }

    private void waitWithDetails(TerminalPresenter presenter) {
        var publisher = new SubmissionPublisher<String>();
        int maxCount = 50;

        CompletableFuture.runAsync(() -> {
            int counter = 0;
            try {
                while (counter < maxCount) {
                    publisher.submit(Integer.toString(counter));
                    counter++;
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                publisher.close();
            }
        });

        presenter.waitWhileWithDetails("Counting up to " + maxCount, publisher, 10);
        presenter.message("Counted to " + maxCount);
    }
}
