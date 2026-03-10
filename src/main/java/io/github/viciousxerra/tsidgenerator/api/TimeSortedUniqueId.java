package io.github.viciousxerra.tsidgenerator.api;

import java.time.OffsetDateTime;

/**
 * This type models a unique up to 63 bit time sorted identifier.
 */
public interface TimeSortedUniqueId extends Comparable<TimeSortedUniqueId> {
    /**
     * Get timestamp of this ID.
     *
     * @return creation timestamp of this ID
     */
    OffsetDateTime getTimestamp();

    /**
     * Get shard coordinates.
     *
     * @return array of integers.
     * <p>
     * If length of this array is equal to 2, then it represents data center ID and machine ID respectively.
     * <p>
     * If length of this array is equal to 1, then it represents general shard ID.
     * <p>
     * If this array is empty, it represents the abscence of this element.
     */
    int[] getShardCoordinates();

    /**
     * Get sequence number.
     *
     * @return the sequence number of the identifier generated at a specific millisecond.
     */
    int getSequence();

    /**
     * Map this object in up to 63 bit long value.
     *
     * @return long primitive with allocated bits.
     */
    long getRaw();
}
