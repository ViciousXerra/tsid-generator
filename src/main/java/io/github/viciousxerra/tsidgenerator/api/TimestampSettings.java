package io.github.viciousxerra.tsidgenerator.api;

import java.time.OffsetDateTime;

/**
 * A value holder containing configuration information about the timestamp part of generated identifiers.
 * @param startPoint Timeline starting point.
 * @param millisMaxValue The maximum value of the difference between the timeline starting point
 *                      and the current time in milliseconds.
 * @param grantedBits Number of bits allocated for timestamp.
 */
public record TimestampSettings(OffsetDateTime startPoint, long millisMaxValue, int grantedBits) {
}
