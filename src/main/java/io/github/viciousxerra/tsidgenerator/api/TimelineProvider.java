package io.github.viciousxerra.tsidgenerator.api;

/**
 * A provider of the current millisecond relative to the starting point.
 */
public interface TimelineProvider {
    /**
     * Provide current millisecond relative to the starting point.
     * @return current millisecond relative to the starting point.
     */
    long provideCurrentMillis();
}
