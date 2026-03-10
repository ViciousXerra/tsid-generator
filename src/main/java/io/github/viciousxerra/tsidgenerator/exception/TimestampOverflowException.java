package io.github.viciousxerra.tsidgenerator.exception;

/**
 * An unchecked exception which indicates timestamp in milliseconds integer number overflow for particular number of bits.
 */
public class TimestampOverflowException extends RuntimeException {
    public TimestampOverflowException(String message, Throwable cause) {
        super(message, cause);
    }
}
