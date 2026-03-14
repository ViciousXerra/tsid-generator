package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.Configuration;
import io.github.viciousxerra.tsidgenerator.api.SequenceSettings;
import io.github.viciousxerra.tsidgenerator.api.ShardIdSettings;
import io.github.viciousxerra.tsidgenerator.api.TimeSortedUniqueId;
import io.github.viciousxerra.tsidgenerator.api.TimestampSettings;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class TimeSortedUniqueIdImplTest {
    private static final OffsetDateTime OFFSET_DATE_TIME_NOW = OffsetDateTime.parse("2026-01-01T00:00:00+00:00");
    private static final long DELTA_SECONDS = 5L;
    private static final long DELTA_MILLISECONDS = DELTA_SECONDS * 1000L;
    private static final OffsetDateTime VALID_START_POINT = OFFSET_DATE_TIME_NOW.minusSeconds(5L);
    private static final int TIMESTAMP_BITS = 41;
    private static final long TIMESTAMP_MAX_VALUE = (1L << TIMESTAMP_BITS) - 1;
    private static final int SHARD_ID_BITS = 10;
    private static final int SHARD_ID_MAX_VALUE = (1 << SHARD_ID_BITS) - 1;
    private static final int SHARD_ID_RANDOM = (int) ((SHARD_ID_MAX_VALUE + 1) * Math.random());
    private static final int DATA_CENTER_ID_BITS = 5;
    private static final int DATA_CENTER_ID_MAX_VALUE = (1 << DATA_CENTER_ID_BITS) - 1;
    private static final int DATA_CENTER_ID_RANDOM = (int) ((DATA_CENTER_ID_MAX_VALUE + 1) * Math.random());
    private static final int MACHINE_ID_BITS = DATA_CENTER_ID_BITS;
    private static final int MACHINE_ID_MAX_VALUE = DATA_CENTER_ID_MAX_VALUE;
    private static final int MACHINE_ID_RANDOM = (int) ((MACHINE_ID_MAX_VALUE + 1) * Math.random());
    private static final int SEQUENCE_BITS = 6;
    private static final int SEQUENCE_MAX_VALUE = (1 << SEQUENCE_BITS) - 1;
    private static final int SEQUENCE_RANDOM = (int) ((SEQUENCE_MAX_VALUE + 1) * Math.random());

    @Mock
    private Configuration configuration;

    @Test
    @DisplayName("Test equals and hashcode contract")
    void testEqualsAndHashCodeContract() {
        EqualsVerifier.forClass(TimeSortedUniqueIdImpl.class).verify();
    }

    @Test
    @DisplayName("Test correct id construction without shard id")
    void testCorrectIdConstructionWithoutShardId() {
        Mockito.when(configuration.getTimestampSettings()).thenReturn(new TimestampSettings(VALID_START_POINT, 0, 0));
        Mockito.when(configuration.getShardIdSettings()).thenReturn(new ShardIdSettings[0]);
        Mockito.when(configuration.getSequenceSettings()).thenReturn(new SequenceSettings(0, SEQUENCE_BITS, null));
        TimeSortedUniqueId id = new TimeSortedUniqueIdImpl(configuration, DELTA_MILLISECONDS, SEQUENCE_RANDOM);
        assertAll(
                () -> assertThat(extractLongWithBitMask(id.getRaw(), TIMESTAMP_MAX_VALUE, SEQUENCE_BITS)).isEqualTo(DELTA_MILLISECONDS),
                () -> assertThat(id.getTimestamp()).isEqualTo(OFFSET_DATE_TIME_NOW),
                () -> assertThat(id.getShardCoordinates()).isEmpty(),
                () -> assertThat(extractIntWithBitMask(id.getRaw(), SEQUENCE_MAX_VALUE, 0)).isEqualTo(id.getSequence()),
                () -> assertThat(id.getSequence()).isEqualTo(SEQUENCE_RANDOM)
        );
    }

    @Test
    @DisplayName("Test correct id construction with single shard id")
    void testCorrectIdConstructionWithSingleShardId() {
        Mockito.when(configuration.getTimestampSettings()).thenReturn(new TimestampSettings(VALID_START_POINT, 0, 0));
        Mockito.when(configuration.getShardIdSettings()).thenReturn(new ShardIdSettings[]{new ShardIdSettings(SHARD_ID_RANDOM, SHARD_ID_BITS)});
        Mockito.when(configuration.getSequenceSettings()).thenReturn(new SequenceSettings(0, SEQUENCE_BITS, null));
        TimeSortedUniqueId id = new TimeSortedUniqueIdImpl(configuration, DELTA_MILLISECONDS, SEQUENCE_RANDOM);
        assertAll(
                () -> assertThat(extractLongWithBitMask(id.getRaw(), TIMESTAMP_MAX_VALUE, SHARD_ID_BITS + SEQUENCE_BITS)).isEqualTo(DELTA_MILLISECONDS),
                () -> assertThat(id.getTimestamp()).isEqualTo(OFFSET_DATE_TIME_NOW),
                () -> assertThat(id.getShardCoordinates()).hasSize(1),
                () -> assertThat(extractIntWithBitMask(id.getRaw(), SHARD_ID_MAX_VALUE, SEQUENCE_BITS)).isEqualTo(id.getShardCoordinates()[0]),
                () -> assertThat(id.getShardCoordinates()[0]).isEqualTo(SHARD_ID_RANDOM),
                () -> assertThat(extractIntWithBitMask(id.getRaw(), SEQUENCE_MAX_VALUE, 0)).isEqualTo(id.getSequence()),
                () -> assertThat(id.getSequence()).isEqualTo(SEQUENCE_RANDOM)
        );
    }

    @Test
    @DisplayName("Test correct id construction with coupled data center id and machine id")
    void testCorrectIdConstructionWithCoupledDataCenterIdAndMachineId() {
        Mockito.when(configuration.getTimestampSettings()).thenReturn(new TimestampSettings(VALID_START_POINT, 0, 0));
        Mockito.when(configuration.getShardIdSettings()).thenReturn(
                new ShardIdSettings[]{
                        new ShardIdSettings(DATA_CENTER_ID_RANDOM, DATA_CENTER_ID_BITS),
                        new ShardIdSettings(MACHINE_ID_RANDOM, MACHINE_ID_BITS)});
        Mockito.when(configuration.getSequenceSettings()).thenReturn(new SequenceSettings(0, SEQUENCE_BITS, null));
        TimeSortedUniqueId id = new TimeSortedUniqueIdImpl(configuration, DELTA_MILLISECONDS, SEQUENCE_RANDOM);
        assertAll(
                () -> assertThat(extractLongWithBitMask(id.getRaw(), TIMESTAMP_MAX_VALUE, DATA_CENTER_ID_BITS + MACHINE_ID_BITS + SEQUENCE_BITS)).isEqualTo(DELTA_MILLISECONDS),
                () -> assertThat(id.getTimestamp()).isEqualTo(OFFSET_DATE_TIME_NOW),
                () -> assertThat(id.getShardCoordinates()).hasSize(2),
                () -> assertThat(extractIntWithBitMask(id.getRaw(), DATA_CENTER_ID_MAX_VALUE, MACHINE_ID_BITS + SEQUENCE_BITS)).isEqualTo(id.getShardCoordinates()[0]),
                () -> assertThat(id.getShardCoordinates()[0]).isEqualTo(DATA_CENTER_ID_RANDOM),
                () -> assertThat(extractIntWithBitMask(id.getRaw(), MACHINE_ID_MAX_VALUE, SEQUENCE_BITS)).isEqualTo(id.getShardCoordinates()[1]),
                () -> assertThat(id.getShardCoordinates()[1]).isEqualTo(MACHINE_ID_RANDOM),
                () -> assertThat(extractIntWithBitMask(id.getRaw(), SEQUENCE_MAX_VALUE, 0)).isEqualTo(id.getSequence()),
                () -> assertThat(id.getSequence()).isEqualTo(SEQUENCE_RANDOM)
        );
    }

    @Test
    @DisplayName("Test comparing ids")
    void testComparing() {
        Mockito.when(configuration.getTimestampSettings()).thenReturn(new TimestampSettings(VALID_START_POINT, 0, 0));
        Mockito.when(configuration.getShardIdSettings()).thenReturn(new ShardIdSettings[]{new ShardIdSettings(1, SHARD_ID_BITS)});
        Mockito.when(configuration.getSequenceSettings()).thenReturn(new SequenceSettings(0, SEQUENCE_BITS, null));
        TimeSortedUniqueId earlierTimestampId = new TimeSortedUniqueIdImpl(configuration, 0, 0);
        TimeSortedUniqueId id = new TimeSortedUniqueIdImpl(configuration, 1, 0);
        TimeSortedUniqueId sameTimestampNextSequence = new TimeSortedUniqueIdImpl(configuration, 1, 1);
        TimeSortedUniqueId idCopy = new TimeSortedUniqueIdImpl(configuration, 1, 0);
        assertAll(
                () -> assertThat(earlierTimestampId).isLessThan(id),
                () -> assertThat(id).isLessThan(sameTimestampNextSequence),
                () -> assertThat(id).isEqualByComparingTo(idCopy)
        );
    }

    private static long extractLongWithBitMask(long value, long maxValue, int shiftBits) {
        return (value & (maxValue << shiftBits)) >> shiftBits;
    }

    private static int extractIntWithBitMask(long value, int maxValue, int shiftBits) {
        return (int) (value & (maxValue << shiftBits)) >> shiftBits;
    }

}
