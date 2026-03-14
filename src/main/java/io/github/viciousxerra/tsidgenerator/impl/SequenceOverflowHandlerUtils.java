package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.exception.SequenceOverflowException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.SEQUENCE_OVERFLOW_MESSAGE;

final class SequenceOverflowHandlerUtils {
    static final long DEFAULT_THREAD_SLEEP_MS = 100L;
    static final long DEFAULT_THREAD_SLEEP_JITTER_MS = 50L;

    private SequenceOverflowHandlerUtils() {
    }

    static Runnable handleWithThreadFixedSleep(long sleepMillis) {
        return () -> {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException ignored) {
            }
        };
    }

    static Runnable handleWithThreadSleepWithJitter(long sleepMillis, long jitterMillis) {
        return () -> {
            try {
                Thread.sleep(sleepMillis + ThreadLocalRandom.current().nextLong(jitterMillis));
            } catch (InterruptedException ignored) {
            }
        };
    }

    static Runnable handleWithThreadSpinOnWait(Supplier<Long> lastMillisSupplier, Supplier<Long> actualMillisSupplier) {
        return () -> {
            do {
                Thread.onSpinWait();
            } while (Objects.equals(lastMillisSupplier.get(), actualMillisSupplier.get()));
        };
    }

    static Runnable handleWithException() {
        return () -> {
            throw new SequenceOverflowException(SEQUENCE_OVERFLOW_MESSAGE);
        };
    }
}
