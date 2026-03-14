package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.Configuration;
import io.github.viciousxerra.tsidgenerator.api.SequenceProvider;
import io.github.viciousxerra.tsidgenerator.api.TimeSortedUniqueId;
import io.github.viciousxerra.tsidgenerator.api.TimeSortedUniqueIdGenerator;
import io.github.viciousxerra.tsidgenerator.api.TimelineProvider;
import io.github.viciousxerra.tsidgenerator.exception.UnsupportedSequenceOverflowHandleStrategyException;
import java.util.concurrent.locks.ReentrantLock;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.UNSUPPORTED_SEQUENCE_OVERFLOW_HANDLER_STRATEGY_MESSAGE;

final class TimeSortedUniqueIdGeneratorImpl implements TimeSortedUniqueIdGenerator {

    private static final ReentrantLock LOCK = new ReentrantLock();

    private final Configuration configuration;
    private final TimelineProvider timelineProvider;
    private final SequenceProvider sequenceProvider;
    private long lastMillis = -1;

    TimeSortedUniqueIdGeneratorImpl(Configuration configuration,
                                    TimelineProvider timelineProvider,
                                    SequenceProvider sequenceProvider) {
        this.configuration = configuration;
        this.timelineProvider = timelineProvider;
        this.sequenceProvider = sequenceProvider;
    }


    @Override
    public TimeSortedUniqueId nextId() {
        try {
            LOCK.lock();
            return generate();
        } finally {
            LOCK.unlock();
        }
    }

    private TimeSortedUniqueId generate() {
        long currentMillis;
        do {
            currentMillis = timelineProvider.provideCurrentMillis();
        } while (currentMillis < lastMillis);
//        long currentMillis = timelineProvider.provideCurrentMillis();
//        if (currentMillis < lastMillis) {
//            throw new ClockMoveBackwardsException(
//                    String.format(CLOCK_MOVE_BACKWARDS_MESSAGE_TEMPLATE,
//                            lastMillis - currentMillis));
//        }
        if (currentMillis > lastMillis) {
            sequenceProvider.reset();
            lastMillis = currentMillis;
            return new TimeSortedUniqueIdImpl(configuration, lastMillis, sequenceProvider.nextSequence());
        }
        if (!sequenceProvider.hasNext()) {
            handleSequenceOverflow().run();
            do {
                currentMillis = timelineProvider.provideCurrentMillis();
            } while (currentMillis <= lastMillis);
            sequenceProvider.reset();
            lastMillis = currentMillis;
        }
        return new TimeSortedUniqueIdImpl(configuration, lastMillis, sequenceProvider.nextSequence());
    }

    private Runnable handleSequenceOverflow() {
        switch (configuration.getSequenceSettings().overflowHandleStrategy()) {
            case THREAD_FIXED_SLEEP -> {
                return SequenceOverflowHandlerUtils.handleWithThreadFixedSleep(
                        SequenceOverflowHandlerUtils.DEFAULT_THREAD_SLEEP_MS);
            }
            case THREAD_SLEEP_WITH_JITTER -> {
                return SequenceOverflowHandlerUtils.handleWithThreadSleepWithJitter(
                        SequenceOverflowHandlerUtils.DEFAULT_THREAD_SLEEP_MS,
                        SequenceOverflowHandlerUtils.DEFAULT_THREAD_SLEEP_JITTER_MS);
            }
            case SPIN_ON_WAIT -> {
                return SequenceOverflowHandlerUtils.handleWithThreadSpinOnWait(
                        () -> lastMillis,
                        timelineProvider::provideCurrentMillis);
            }
            case THROW_EXCEPTION -> {
                return SequenceOverflowHandlerUtils.handleWithException();
            }
            default -> throw new UnsupportedSequenceOverflowHandleStrategyException(
                    UNSUPPORTED_SEQUENCE_OVERFLOW_HANDLER_STRATEGY_MESSAGE);
        }
    }
}
