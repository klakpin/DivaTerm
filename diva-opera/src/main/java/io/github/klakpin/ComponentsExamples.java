package io.github.klakpin;

import io.github.klakpin.components.api.choice.ChoiceOption;
import io.github.klakpin.terminal.ConsoleTerminalPresenter;
import io.github.klakpin.terminal.TerminalPresenter;
import net.datafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ComponentsExamples {
    void runExamples() {
        try (var presenter = ConsoleTerminalPresenter.standard()) {
            messages(presenter);
            prompt(presenter);
            interactiveChoice(presenter);
            simpleInteractiveChoice(presenter);
            confirm(presenter);
            waitWithDetails(presenter);
            waitWithoutDetails(presenter);
        }
    }

    void basicDemo() {
        try (var presenter = ConsoleTerminalPresenter.standard()) {
            var choice = presenter.stringChoice("What action you want to perform?",
                    List.of("first", "second", "third", "fourth", "fifth"));

            presenter.confirm(choice + " - are you sure?");

            presenter.successMessage("Action completed");
        }
    }

    void messages() {
        try (var presenter = ConsoleTerminalPresenter.standard()) {
            messages(presenter);
        }
    }

    void confirm() {
        try (var presenter = ConsoleTerminalPresenter.standard()) {
            confirm(presenter);
        }
    }

    void prompt() {
        try (var presenter = ConsoleTerminalPresenter.standard()) {
            prompt(presenter);
        }
    }

    void interactiveChoice() {
        try (var presenter = ConsoleTerminalPresenter.standard()) {
            interactiveChoice(presenter);
        }
    }

    void multiChoice() {
        try (var presenter = ConsoleTerminalPresenter.standard()) {
            interactiveChoiceMulti(presenter);
        }
    }

    void waitWithoutDetails() {
        try (var presenter = ConsoleTerminalPresenter.standard()) {
            waitWithoutDetails(presenter);
        }
    }

    void waitWithDetails() {
        try (var presenter = ConsoleTerminalPresenter.standard()) {
            waitWithDetails(presenter);
        }
    }

    private void confirm(TerminalPresenter presenter) {
        var result = presenter.confirm();
        if (result) {
            presenter.message("Confirmed");
        } else {
            presenter.message("Not confirmed");
        }
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

    private void simpleInteractiveChoice(TerminalPresenter presenter) {
        presenter.stringChoice("test question", List.of("first", "second", "third", "fourth", "fifth"));
        presenter.stringMultiChoice("test question", List.of("first", "second", "third", "fourth", "fifth"), 3);
    }

    private void interactiveChoice(TerminalPresenter presenter) {
        var dessertsFaker = new Faker().dessert();
        var desserts = IntStream.range(1, 25).mapToObj(o -> dessertsFaker.variety()).distinct().toList();

        var dessertsChoices = new ArrayList<ChoiceOption>();

        for (int i = 0; i < desserts.size(); i++) {
            dessertsChoices.add(new ChoiceOption(i, desserts.get(i)));
        }

        var selectedChoice = presenter.choice(cb ->
                cb.withQuestion("What dessert you want to have today?")
                        .withOptions(new ArrayList<>(dessertsChoices))
                        .withMaxDisplayResults(5)
                        .withFilteringEnabled(true)
                        .withFilterSimilarityCutoff(0.7)
        );

        presenter.message("You selected " + selectedChoice.displayText());
    }

    private void interactiveChoiceMulti(TerminalPresenter presenter) {
        var dessertsFaker = new Faker().dessert();
        var desserts = IntStream.range(1, 25).mapToObj(o -> dessertsFaker.variety()).distinct().toList();

        var dessertsChoices = new ArrayList<ChoiceOption>();

        for (int i = 0; i < desserts.size(); i++) {
            dessertsChoices.add(new ChoiceOption(i, desserts.get(i)));
        }

        var filteredResult = presenter.multiChoice(cb ->
                cb.withQuestion("What dessert you want to have today?")
                        .withOptions(new ArrayList<>(dessertsChoices))
                        .withMaxDisplayResults(5)
                        .withMaxSelectResults(3)
                        .withFilteringEnabled(true)
                        .withFilterSimilarityCutoff(0.0)
        );

        presenter.message("You selected: " + filteredResult.stream().map(ChoiceOption::displayText).collect(Collectors.joining(", ")));
    }

    private void waitWithoutDetails(TerminalPresenter presenter) {
        var waitingFuture = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        presenter.waitWhile("Waiting 5 seconds", waitingFuture);
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

        presenter.waitWhileWithDetails("Counting up to " + maxCount, publisher, 5);
        presenter.message("Counted to " + maxCount);
    }
}
