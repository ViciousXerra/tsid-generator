package io.github.viciousxerra.tsidgenerator.exception;

/**
 * An unchecked exception which indicates unsupported sequence overflow handling strategy for particular time sorted
 * unique id generator implementation.
 */
public class UnsupportedSequenceOverflowHandleStrategy extends RuntimeException {
    public UnsupportedSequenceOverflowHandleStrategy(String message) {
        super(message);
    }
}
