package io.github.viciousxerra.tsidgenerator.exception;

/**
 * An unchecked exception which indicates sequence overflow for particular number of bits in a given millisecond.
 */
public class SequenceOverflowException extends RuntimeException {
    public SequenceOverflowException(String message) {
        super(message);
    }
}
