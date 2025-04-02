package io.github.klakpin;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("demo")) {
            new ComponentsExamples().runDemo();
        } else {
            new ComponentsExamples().runExamples();
        }
    }
}