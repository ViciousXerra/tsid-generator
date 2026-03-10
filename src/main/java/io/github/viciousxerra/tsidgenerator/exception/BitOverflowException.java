package io.github.viciousxerra.tsidgenerator.exception;

/**
 * An unchecked exception which indicates integer number overflow for particular number of bits.
 */
public class BitOverflowException extends RuntimeException {
    public BitOverflowException(String message) {
        super(message);
    }
}
