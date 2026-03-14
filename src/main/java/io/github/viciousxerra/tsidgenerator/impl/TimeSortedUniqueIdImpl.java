package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.Configuration;
import io.github.viciousxerra.tsidgenerator.api.ShardIdSettings;
import io.github.viciousxerra.tsidgenerator.api.TimeSortedUniqueId;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

final class TimeSortedUniqueIdImpl implements TimeSortedUniqueId {
    private final OffsetDateTime startPoint;
    private final long millis;
    private final int[] shardCoordinates;
    private final int sequence;
    private final long rawValue;

    TimeSortedUniqueIdImpl(Configuration configuration, long millis, int sequence) {
        this.millis = millis;
        this.sequence = sequence;
        this.startPoint = configuration.getTimestampSettings().startPoint();
        this.shardCoordinates = getShardCoordinates(configuration.getShardIdSettings());
        this.rawValue = fillBits(configuration, millis, sequence);
    }

    @Override
    public OffsetDateTime getTimestamp() {
        return startPoint.plus(Duration.ofMillis(millis));
    }

    @Override
    public int[] getShardCoordinates() {
        return shardCoordinates;
    }

    @Override
    public int getSequence() {
        return sequence;
    }

    @Override
    public long getRaw() {
        return rawValue;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TimeSortedUniqueIdImpl that)) {
            return false;
        }
        return startPoint == that.startPoint
                && millis == that.millis
                && sequence == that.sequence
                && rawValue == that.rawValue
                && Objects.deepEquals(shardCoordinates, that.shardCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPoint, millis, Arrays.hashCode(shardCoordinates), sequence, rawValue);
    }

    @Override
    public String toString() {
        return "TimeSortedUniqueId{"
                + "timestamp=" + getTimestamp()
                + ", millis=" + millis
                + ", shardCoordinates=" + Arrays.toString(shardCoordinates)
                + ", sequence=" + sequence
                + ", rawValue=" + rawValue
                + '}';
    }

    @Override
    public int compareTo(TimeSortedUniqueId timeSortedUniqueId) {
        if (timeSortedUniqueId == null) {
            throw new NullPointerException();
        }
        return Comparator.comparing(TimeSortedUniqueId::getTimestamp)
                .thenComparing(TimeSortedUniqueId::getShardCoordinates, Arrays::compare)
                .thenComparingInt(TimeSortedUniqueId::getSequence)
                .thenComparingLong(TimeSortedUniqueId::getRaw)
                .compare(this, timeSortedUniqueId);
    }

    private static int[] getShardCoordinates(ShardIdSettings[] shardIdSettings) {
        var result = new int[shardIdSettings.length];
        for (int i = 0; i < shardIdSettings.length; i++) {
            result[i] = shardIdSettings[i].id();
        }
        return result;
    }

    private static long fillBits(Configuration configuration, long millis, int sequence) {
        var shardIdShifts = new int[configuration.getShardIdSettings().length];
        if (configuration.getShardIdSettings().length != 0) {
            shardIdShifts[configuration.getShardIdSettings().length - 1] =
                    configuration.getSequenceSettings().grantedBits();
            for (int i = configuration.getShardIdSettings().length - 2; i >= 0; i--) {
                shardIdShifts[i] = shardIdShifts[i + 1] + configuration.getShardIdSettings()[i + 1].grantedBits();
            }
        }
        var timestampShift = configuration.getShardIdSettings().length != 0
                ? shardIdShifts[0] + configuration.getShardIdSettings()[0].grantedBits()
                : configuration.getSequenceSettings().grantedBits();
        var res = BitUtils.shiftBits(millis, timestampShift);
        for (int i = 0; i < configuration.getShardIdSettings().length; i++) {
            res |= BitUtils.shiftBits(configuration.getShardIdSettings()[i].id(), shardIdShifts[i]);
        }
        res |= sequence;
        return res;
    }
}
