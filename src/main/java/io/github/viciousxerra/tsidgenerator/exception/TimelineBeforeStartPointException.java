package io.github.viciousxerra.tsidgenerator.exception;

/**
 * An unchecked exception which indicates that startPoint is ahead from current time.
 */
public class TimelineBeforeStartPointException extends RuntimeException {
    public TimelineBeforeStartPointException(String message) {
        super(message);
    }
}
