package io.github.viciousxerra.tsidgenerator.api;

/**
 * A source of unique up to 63 bit time sorted identifier (64 bit -1 top zero bit).
 */
public interface TimeSortedUniqueIdGenerator {
    /**
     * Generates a unique up to 63 bit ID based on the current epoch milliseconds
     * timestamp, shard ID (single ID or data center ID coupled with machine ID) (if present) and the current sequence.
     *
     * @return unique identifier.
     */
    TimeSortedUniqueId nextId();
}
