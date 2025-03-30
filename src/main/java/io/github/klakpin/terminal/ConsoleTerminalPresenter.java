package io.github.klakpin.terminal;

import io.github.klakpin.components.ComponentsFactory;
import io.github.klakpin.components.impl.choice.OptionsComparator;
import io.github.klakpin.components.impl.choice.OptionsProvider;

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
    public void interactiveChoice(String question,
                                  int maxResults,
                                  OptionsProvider optionsProvider,
                                  OptionsComparator comparator,
                                  Double filterValuesCutoff) {
        componentsFactory
                .terminalInteractiveChoice()
                .interactiveChoice(question, maxResults, optionsProvider, comparator, filterValuesCutoff);
    }


}
