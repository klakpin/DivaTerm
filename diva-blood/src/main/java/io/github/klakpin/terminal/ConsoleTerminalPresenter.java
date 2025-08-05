package io.github.klakpin.terminal;

import io.github.klakpin.components.ComponentsFactory;
import io.github.klakpin.components.TerminalComponentFactory;
import io.github.klakpin.components.api.choice.Choice.ChoiceBuilder;
import io.github.klakpin.components.api.choice.ChoiceOption;
import io.github.klakpin.components.api.choice.comparator.FuzzyDisplayTextComparator;
import io.github.klakpin.theme.DefaultTerminalColorPalette;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

import static io.github.klakpin.theme.ColorPalette.ColorFeature.*;

public class ConsoleTerminalPresenter implements TerminalPresenter {

    private final ComponentsFactory componentsFactory;

    public static ConsoleTerminalPresenter standard() {

        try {
            var terminal = new JlineTerminalFactory().buildTerminal();
            return new ConsoleTerminalPresenter.Builder()
                    .withTerminal(terminal)
                    .withColorPalette(new DefaultTerminalColorPalette())
                    .withDrawingExecutor(Executors.newSingleThreadScheduledExecutor())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to build console terminal presenter", e);
        }
    }

    public ConsoleTerminalPresenter(ComponentsFactory componentsFactory) {
        this.componentsFactory = componentsFactory;
    }

    @Override
    public void message(String message) {
        componentsFactory.buildMessageComponent().printMessage(message);
    }

    @Override
    public void successMessage(String message) {
        componentsFactory.buildMessageComponent().printMessage(message, success, bold);
    }

    @Override
    public void errorMessage(String message) {
        componentsFactory.buildMessageComponent().printMessage(message, error, bold);
    }

    @Override
    public void messageInBracket(String message) {
        componentsFactory.buildMessageComponent().printInBracket(message, List.of(muted), List.of(muted));
    }

    @Override
    public void errorInBracket(String message) {
        componentsFactory.buildMessageComponent().printInBracket(message, List.of(error), List.of(muted));
    }

    @Override
    public void waitWhile(String message, CompletableFuture<Void> waitWhile) {
        componentsFactory.buildWaitComponent().waitWhile(message, waitWhile);
    }

    @Override
    public void waitWhileWithDetails(String message, SubmissionPublisher<String> details, int maxLines) {
        componentsFactory.buildWaitComponent().waitWhileWithDetails(message, details, maxLines);
    }

    @Override
    public String prompt(String question) {
        return componentsFactory.buildPrompt().prompt(question);
    }

    @Override
    public String promptWithDefault(String question, String defaultValue) {
        return componentsFactory.buildPrompt().prompt(question, defaultValue);
    }

    @Override
    public String stringChoice(String question, List<String> options) {
        var optionsMap = new HashMap<Integer, ChoiceOption>();
        for (int i = 0; i < options.size(); i++) {
            optionsMap.put(i, new ChoiceOption(i, options.get(i)));
        }

        var selected = componentsFactory.choiceBuilder()
                .withQuestion(question)
                .withFilteringEnabled(true)
                .withOptionsComparator(new FuzzyDisplayTextComparator())
                .withOptions(optionsMap.values().stream().toList())
                .build()
                .get();

        return optionsMap.get(selected.id()).displayText();
    }

    @Override
    public List<String> stringMultiChoice(String question, List<String> options, int limit) {
        var optionsMap = new HashMap<Integer, ChoiceOption>();
        for (int i = 0; i < options.size(); i++) {
            optionsMap.put(i, new ChoiceOption(i, options.get(i)));
        }

        var choice = componentsFactory.choiceBuilder()
                .withQuestion(question)
                .withFilteringEnabled(true)
                .withMaxSelectResults(limit)
                .withOptionsComparator(new FuzzyDisplayTextComparator())
                .withOptions(optionsMap.values().stream().toList())
                .build();


        return choice.getMulti()
                .stream()
                .map(s -> optionsMap.get(s.id()).displayText())
                .toList();
    }

    @Override
    public ChoiceOption choice(Function<ChoiceBuilder, ChoiceBuilder> builder) {
        var rawBuilder = componentsFactory
                .choiceBuilder();

        return builder.apply(rawBuilder)
                .build()
                .get();
    }

    @Override
    public List<ChoiceOption> multiChoice(Function<ChoiceBuilder, ChoiceBuilder> builder) {
        var rawBuilder = componentsFactory
                .choiceBuilder();

        return builder.apply(rawBuilder)
                .build()
                .getMulti();
    }

    @Override
    public Boolean confirm() {
        return componentsFactory.buildConfirm().confirm();
    }

    @Override
    public Boolean confirm(String confirmationText) {
        return componentsFactory.buildConfirm().confirm(confirmationText);
    }

    @Override
    public ComponentsFactory getComponentsFactory() {
        return componentsFactory;
    }

    @Override
    public void close() {
        try {
            componentsFactory.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to closed components factory", e);
        }
    }

    static class Builder {
        private Terminal terminal;
        private ScheduledExecutorService drawingExecutor;
        private DefaultTerminalColorPalette colorPalette;

        public Builder withTerminal(Terminal terminal) {
            this.terminal = terminal;
            return this;
        }

        public Builder withDrawingExecutor(ScheduledExecutorService drawingExecutor) {
            this.drawingExecutor = drawingExecutor;
            return this;

        }

        public Builder withColorPalette(DefaultTerminalColorPalette colorPalette) {
            this.colorPalette = colorPalette;
            return this;
        }

        public ConsoleTerminalPresenter build() {
            Objects.requireNonNull(terminal, "terminal instance should be set for the console terminal presenter");
            Objects.requireNonNull(drawingExecutor, "drawing executor instance should be set for the console terminal presenter");
            Objects.requireNonNull(colorPalette, "color palette instance should be set for the console terminal presenter");
            return new ConsoleTerminalPresenter(new TerminalComponentFactory(terminal, drawingExecutor, colorPalette));
        }
    }
}
