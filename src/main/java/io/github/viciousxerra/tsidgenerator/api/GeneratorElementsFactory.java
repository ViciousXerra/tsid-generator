package io.github.viciousxerra.tsidgenerator.api;

/**
 * A factory which produces elements for time sorted unique id generator.
 */
public interface GeneratorElementsFactory {
    /**
     * Produce timeline provider.
     * @return created timeline provider.
     */
    TimelineProvider createTimelineProvider();

    /**
     * Produce sequence provider.
     * @return created sequence provider.
     */
    SequenceProvider createSequenceProvider();
}
