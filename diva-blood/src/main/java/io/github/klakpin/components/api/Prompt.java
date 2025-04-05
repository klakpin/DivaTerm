package io.github.klakpin.components.api;

/**
 * Interface for prompting user for text input.
 */
public interface Prompt {
    /**
     * Displays a prompt to the user and returns their input.
     * Returns an empty string if the user provides no input.
     *
     * @param text The prompt message to display to the user
     * @return The user's input, or empty string if no input provided
     */
    String prompt(String text);

    /**
     * Displays a prompt to the user and returns their input.
     * Returns the default value if the user provides no input.
     *
     * @param text         The prompt message to display to the user
     * @param defaultValue The value to return if user provides no input
     * @return The user's input, or the defaultValue if no input provided
     */
    String prompt(String text, String defaultValue);
}