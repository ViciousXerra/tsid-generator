package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.AbstractGeneratorElementsFactory;
import io.github.viciousxerra.tsidgenerator.api.AbstractTimeSortedUniqueIdGeneratorFactory;
import io.github.viciousxerra.tsidgenerator.api.TimeSortedUniqueIdGenerator;

/**
 * A factory class which produces default implementation of {@link TimeSortedUniqueIdGenerator}.
 */
public class DefaultTimeSortedUniqueIdGeneratorFactory extends AbstractTimeSortedUniqueIdGeneratorFactory {
    private final AbstractGeneratorElementsFactory generatorElementsFactory;

    /**
     * Constructs {@link DefaultGeneratorElementsFactory} factory with passed {@link GeneratorConfiguration}.
     *
     * @param configuration configuration class which holds required info
     */
    public DefaultTimeSortedUniqueIdGeneratorFactory(GeneratorConfiguration configuration) {
        super(configuration);
        this.generatorElementsFactory = new DefaultGeneratorElementsFactory(configuration);
    }

    @Override
    protected TimeSortedUniqueIdGenerator createTimeSortedUniqueIdGenerator() {
        return new TimeSortedUniqueIdGeneratorImpl(configuration,
                generatorElementsFactory.createTimelineProvider(),
                generatorElementsFactory.createSequenceProvider());
    }
}
