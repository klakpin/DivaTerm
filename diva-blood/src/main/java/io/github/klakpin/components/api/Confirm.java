package io.github.klakpin.components.api;

/**
 * A confirmation dialog presenting YES/NO options to the user.
 */
public interface Confirm {
    /**
     * Displays a confirmation dialog with default text "Are you sure?" and waits for user response.
     *
     * @return {@code true} if the user selects YES, {@code false} if the user selects NO
     */
    Boolean confirm();

    /**
     * Displays a confirmation dialog with custom text and waits for user response.
     *
     * @param question the confirmation message to display to the user
     * @return {@code true} if the user selects YES, {@code false} if the user selects NO
     */
    Boolean confirm(String question);
}