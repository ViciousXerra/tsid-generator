package io.github.viciousxerra.tsidgenerator.api;

/**
 * An abstract factory class with a method to be overridden.
 */
public abstract class AbstractTimeSortedUniqueIdGeneratorFactory implements TimeSortedUniqueIdGeneratorFactory {
    protected final Configuration configuration;

    protected AbstractTimeSortedUniqueIdGeneratorFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public final TimeSortedUniqueIdGenerator create() {
        return createTimeSortedUniqueIdGenerator();
    }

    protected abstract TimeSortedUniqueIdGenerator createTimeSortedUniqueIdGenerator();
}
