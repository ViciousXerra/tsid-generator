package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.Configuration;
import io.github.viciousxerra.tsidgenerator.api.SequenceProvider;

final class SequenceProviderImpl implements SequenceProvider {

    private final int sequenceMaxValue;
    private int sequence;

    SequenceProviderImpl(Configuration configuration) {
        this.sequenceMaxValue = configuration.getSequenceSettings().sequenceMaxValue();
        sequence = 0;
    }

    @Override
    public boolean hasNext() {
        return sequence <= sequenceMaxValue;
    }

    @Override
    public int nextSequence() {
        return sequence++;
    }

    @Override
    public void reset() {
        sequence = 0;
    }
}
