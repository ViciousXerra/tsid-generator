package io.github.viciousxerra.tsidgenerator.api;

/**
 * Sequence overflow strategy related enumeration.
 */
public enum SequenceOverflowHandleStrategy {
    /**
     * Handle with fixed thread sleep.
     */
    THREAD_FIXED_SLEEP,
    /**
     * Handle with fixed thread sleep plus random milliseconds.
     */
    THREAD_SLEEP_WITH_JITTER,
    /**
     * Handle with thread spinning on wait of valid condition.
     */
    SPIN_ON_WAIT,
    /**
     * Handle with threw exception.
     */
    THROW_EXCEPTION
}
