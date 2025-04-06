package io.github.klakpin;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            new ComponentsExamples().runExamples();

        } else {
            switch (args[0]) {
                case "demo" -> new ComponentsExamples().basicDemo();
                case "messages" -> new ComponentsExamples().messages();
                case "confirm" -> new ComponentsExamples().confirm();
                case "prompt" -> new ComponentsExamples().prompt();
                case "interactiveChoice" -> new ComponentsExamples().interactiveChoice();
                case "multiChoice" -> new ComponentsExamples().multiChoice();
                case "waitWithoutDetails" -> new ComponentsExamples().waitWithoutDetails();
                case "waitWithDetails" -> new ComponentsExamples().waitWithDetails();
            }
        }
    }
}