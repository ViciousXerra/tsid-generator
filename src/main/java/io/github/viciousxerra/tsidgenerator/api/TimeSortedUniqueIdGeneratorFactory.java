package io.github.viciousxerra.tsidgenerator.api;

/**
 * A factory which produces time sorted unique id generator.
 */
public interface TimeSortedUniqueIdGeneratorFactory {
    /**
     * Produce time sorted unique id generator.
     *
     * @return time sorted unique id generator.
     */
    TimeSortedUniqueIdGenerator create();
}
