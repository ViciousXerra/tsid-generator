package io.github.viciousxerra.tsidgenerator.api;

/**
 * An abstract factory class with a methods to be overridden.
 */
public abstract class AbstractGeneratorElementsFactory implements GeneratorElementsFactory {
    protected final Configuration configuration;

    protected AbstractGeneratorElementsFactory(Configuration configuration) {
        this.configuration = configuration;
    }
}
