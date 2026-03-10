package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.AbstractGeneratorElementsFactory;
import io.github.viciousxerra.tsidgenerator.api.Configuration;
import io.github.viciousxerra.tsidgenerator.api.SequenceProvider;
import io.github.viciousxerra.tsidgenerator.api.TimelineProvider;

final class DefaultGeneratorElementsFactory extends AbstractGeneratorElementsFactory {

    DefaultGeneratorElementsFactory(Configuration configuration) {
        super(configuration);
    }

    @Override
    public TimelineProvider createTimelineProvider() {
        return new TimelineProviderImpl(configuration);
    }

    @Override
    public SequenceProvider createSequenceProvider() {
        return new SequenceProviderImpl(configuration);
    }
}
