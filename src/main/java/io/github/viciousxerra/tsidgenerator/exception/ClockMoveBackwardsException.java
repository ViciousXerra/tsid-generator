package io.github.viciousxerra.tsidgenerator.exception;

/**
 * An unchecked exception which indicates that time has been turned back through start point.
 */
public class ClockMoveBackwardsException extends RuntimeException {
    public ClockMoveBackwardsException(String message) {
        super(message);
    }
}
