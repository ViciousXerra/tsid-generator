package io.github.viciousxerra.tsidgenerator.exception;

/**
 * An unchecked exception which indicates unsupported sequence overflow handling strategy for particular time sorted
 * unique id generator implementation.
 */
public class UnsupportedSequenceOverflowHandleStrategyException extends RuntimeException {
    public UnsupportedSequenceOverflowHandleStrategyException(String message) {
        super(message);
    }
}
