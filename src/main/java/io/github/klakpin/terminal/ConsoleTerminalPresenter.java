package io.github.klakpin.terminal;

import io.github.klakpin.components.ComponentsFactory;
import io.github.klakpin.components.api.choice.ChoiceOption;
import io.github.klakpin.components.api.choice.comparator.FuzzyDisplayTextComparator;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;

import static io.github.klakpin.theme.ColorPalette.ColorFeature.*;

public class ConsoleTerminalPresenter implements TerminalPresenter {

    private final ComponentsFactory componentsFactory;

    public ConsoleTerminalPresenter(ComponentsFactory componentsFactory) {
        this.componentsFactory = componentsFactory;
    }

    @Override
    public void message(String message) {
        componentsFactory.buildMessageComponent().printMessage(message);
    }

    @Override
    public void successMessage(String message) {
        componentsFactory.buildMessageComponent().printMessage(message, success);
    }

    @Override
    public void errorMessage(String message) {
        componentsFactory.buildMessageComponent().printMessage(message, error);
    }

    @Override
    public void messageInBracket(String message) {
        componentsFactory.buildMessageComponent().printInBracket(message, List.of(secondary_info_bracket), List.of(secondary_info));
    }

    @Override
    public void errorInBracket(String message) {
        componentsFactory.buildMessageComponent().printInBracket(message, List.of(error_bracket), List.of(secondary_info));
    }

    @Override
    public void waitWhile(String message, CompletableFuture<Void> waitWhile) {
        componentsFactory.buildWaitComponent().waitWhile(message, waitWhile);
    }

    @Override
    public void waitWhileWithDetails(String message, SubmissionPublisher<String> details, CompletableFuture<Void> waitWhile, int maxLines) {
        componentsFactory.buildWaitComponent().waitWhileWithDetails(message, details, waitWhile, maxLines);
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
    public Boolean promptBoolean(String question) {
        return componentsFactory.buildPrompt().promptBoolean(question);
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

        return componentsFactory.choiceBuilder()
                .withQuestion(question)
                .withFilteringEnabled(true)
                .withMultiSelect(true)
                .withOptionsComparator(new FuzzyDisplayTextComparator())
                .withOptions(optionsMap.values().stream().toList())
                .build()
                .getMulti()
                .stream()
                .map(s -> optionsMap.get(s.id()).displayText())
                .toList();
    }
}
