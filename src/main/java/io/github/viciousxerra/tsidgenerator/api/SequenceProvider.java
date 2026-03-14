package io.github.viciousxerra.tsidgenerator.api;

/**
 * A provider of the sequence number in a given millisecond.
 */
public interface SequenceProvider {
    /**
     * Check if the following sequence exists.
     *
     * @return true - if exists, false - otherwise.
     */
    boolean hasNext();

    /**
     * Provide next sequence number.
     *
     * @return next sequence number.
     */
    int nextSequence();

    /**
     * Reset sequence number.
     */
    void reset();
}
