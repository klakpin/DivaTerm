package io.github.klakpin.components.api.choice;

/**
 * Represents a single option for a choice element.
 *
 * @param id          internal ID of the element
 * @param displayText text that will be displayed in the terminal. Filter will be applied to this text.
 */
public record ChoiceOption(int id, String displayText) {
}
