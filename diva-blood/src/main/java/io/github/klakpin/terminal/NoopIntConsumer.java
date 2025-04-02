package io.github.klakpin.terminal;

import java.util.function.IntConsumer;

public class NoopIntConsumer implements IntConsumer {
    @Override
    public void accept(int value) {
        /* no-op */
    }
}
