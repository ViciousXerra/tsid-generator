package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.Configuration;
import io.github.viciousxerra.tsidgenerator.api.SequenceOverflowHandleStrategy;
import io.github.viciousxerra.tsidgenerator.api.SequenceSettings;
import io.github.viciousxerra.tsidgenerator.api.ShardIdSettings;
import io.github.viciousxerra.tsidgenerator.api.TimestampSettings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class GeneratorConfigurationTest {
    private static final OffsetDateTime OFFSET_DATE_TIME_NOW = OffsetDateTime.parse("2026-01-01T00:00:00+00:00");
    private static final OffsetDateTime VALID_START_POINT = OFFSET_DATE_TIME_NOW.minusSeconds(5L);
    private static final ZoneOffset ZONE_OFFSET = OFFSET_DATE_TIME_NOW.getOffset();
    private static final int GUARANTEED_TEN_YEARS_BIT_SIZE = 39;
    private static final long TIMESTAMP_MAX_VALUE = (1L << GUARANTEED_TEN_YEARS_BIT_SIZE) - 1;
    private static final TimestampSettings EXPECTED_TIMESTAMP_SETTINGS = new TimestampSettings(
            VALID_START_POINT,
            TIMESTAMP_MAX_VALUE,
            GUARANTEED_TEN_YEARS_BIT_SIZE
    );
    private static final int VALID_GRANTED_BITS = 2;
    private static final int VALID_GRANTED_BITS_MAX_VALUE = (1 << VALID_GRANTED_BITS) - 1;
    private static final ShardIdSettings EXPECTED_DEFAULT_SHARD_COORDINATE_SETTINGS = new ShardIdSettings(
            0,
            VALID_GRANTED_BITS
    );
    private static final ShardIdSettings EXPECTED_SHARD_COORDINATE_SETTINGS = new ShardIdSettings(
            VALID_GRANTED_BITS_MAX_VALUE,
            VALID_GRANTED_BITS
    );
    private static final SequenceSettings EXPECTED_SEQUENCE_SETTINGS = new SequenceSettings(
            VALID_GRANTED_BITS_MAX_VALUE,
            VALID_GRANTED_BITS,
            SequenceOverflowHandleStrategy.THREAD_FIXED_SLEEP
    );

    private static MockedStatic<OffsetDateTime> offsetDateTimeMockedStatic;

    @BeforeAll
    static void setUp() {
        offsetDateTimeMockedStatic = Mockito.mockStatic(OffsetDateTime.class);
        offsetDateTimeMockedStatic.when(() -> OffsetDateTime.now(ZONE_OFFSET)).thenReturn(OFFSET_DATE_TIME_NOW);
    }

    @AfterAll
    static void tearDown() {
        offsetDateTimeMockedStatic.close();
    }

    @Test
    @DisplayName("Test generator configuration settings without shard coordinates")
    void testGeneratorConfigurationSettingsWithoutShard() {
        //Given
        ShardIdSettings[] expectedShardIdSettings = new ShardIdSettings[0];
        //When
        Configuration actualConfiguration = new GeneratorConfiguration.Builder()
                .withStartPoint(VALID_START_POINT)
                .withTimestampBits(GUARANTEED_TEN_YEARS_BIT_SIZE)
                .withSequenceBits(VALID_GRANTED_BITS)
                .withSequenceOverflowStrategy(SequenceOverflowHandleStrategy.THREAD_FIXED_SLEEP)
                .build();
        //Then
        assertAll(
                () -> assertThat(actualConfiguration.getTimestampSettings()).isEqualTo(EXPECTED_TIMESTAMP_SETTINGS),
                () -> assertThat(actualConfiguration.getShardIdSettings()).isEqualTo(expectedShardIdSettings),
                () -> assertThat(actualConfiguration.getSequenceSettings()).isEqualTo(EXPECTED_SEQUENCE_SETTINGS)
        );
    }

    @Test
    @DisplayName("Test generator configuration settings with single default shard id")
    void testGeneratorConfigurationSettingsWithDefaultShardId() {
        //Given
        ShardIdSettings[] expectedShardIdSettings = new ShardIdSettings[]{
                EXPECTED_DEFAULT_SHARD_COORDINATE_SETTINGS
        };
        //When
        Configuration actualConfiguration = new GeneratorConfiguration.Builder()
                .withStartPoint(VALID_START_POINT)
                .withTimestampBits(GUARANTEED_TEN_YEARS_BIT_SIZE)
                .withShardIdBits(VALID_GRANTED_BITS)
                .withSequenceBits(VALID_GRANTED_BITS)
                .withSequenceOverflowStrategy(SequenceOverflowHandleStrategy.THREAD_FIXED_SLEEP)
                .build();
        //Then
        assertAll(
                () -> assertThat(actualConfiguration.getTimestampSettings()).isEqualTo(EXPECTED_TIMESTAMP_SETTINGS),
                () -> assertThat(actualConfiguration.getShardIdSettings()).isEqualTo(expectedShardIdSettings),
                () -> assertThat(actualConfiguration.getSequenceSettings()).isEqualTo(EXPECTED_SEQUENCE_SETTINGS)
        );
    }

    @Test
    @DisplayName("Test generator configuration settings with single custom shard id")
    void testGeneratorConfigurationSettingsWithCustomShardId() {
        //Given
        ShardIdSettings[] expectedShardIdSettings = new ShardIdSettings[]{
                EXPECTED_SHARD_COORDINATE_SETTINGS
        };
        //When
        Configuration actualConfiguration = new GeneratorConfiguration.Builder()
                .withStartPoint(VALID_START_POINT)
                .withTimestampBits(GUARANTEED_TEN_YEARS_BIT_SIZE)
                .withShardId(VALID_GRANTED_BITS_MAX_VALUE)
                .withShardIdBits(VALID_GRANTED_BITS)
                .withSequenceBits(VALID_GRANTED_BITS)
                .withSequenceOverflowStrategy(SequenceOverflowHandleStrategy.THREAD_FIXED_SLEEP)
                .build();
        //Then
        assertAll(
                () -> assertThat(actualConfiguration.getTimestampSettings()).isEqualTo(EXPECTED_TIMESTAMP_SETTINGS),
                () -> assertThat(actualConfiguration.getShardIdSettings()).isEqualTo(expectedShardIdSettings),
                () -> assertThat(actualConfiguration.getSequenceSettings()).isEqualTo(EXPECTED_SEQUENCE_SETTINGS)
        );
    }

    @Test
    @DisplayName("Test generator configuration settings with coupled default data center id and custom machine id")
    void testGeneratorConfigurationSettingsWithCoupledDefaultDataCenterIdAndCustomMachineId() {
        //Given
        ShardIdSettings[] expectedShardIdSettings = new ShardIdSettings[]{
                EXPECTED_DEFAULT_SHARD_COORDINATE_SETTINGS,
                EXPECTED_SHARD_COORDINATE_SETTINGS
        };
        //When
        Configuration actualConfiguration = new GeneratorConfiguration.Builder()
                .withStartPoint(VALID_START_POINT)
                .withTimestampBits(GUARANTEED_TEN_YEARS_BIT_SIZE)
                .withDataCenterIdBits(VALID_GRANTED_BITS)
                .withMachineId(VALID_GRANTED_BITS_MAX_VALUE)
                .withMachineIdBits(VALID_GRANTED_BITS)
                .withSequenceBits(VALID_GRANTED_BITS)
                .withSequenceOverflowStrategy(SequenceOverflowHandleStrategy.THREAD_FIXED_SLEEP)
                .build();
        //Then
        assertAll(
                () -> assertThat(actualConfiguration.getTimestampSettings()).isEqualTo(EXPECTED_TIMESTAMP_SETTINGS),
                () -> assertThat(actualConfiguration.getShardIdSettings()).isEqualTo(expectedShardIdSettings),
                () -> assertThat(actualConfiguration.getSequenceSettings()).isEqualTo(EXPECTED_SEQUENCE_SETTINGS)
        );
    }

    @Test
    @DisplayName("Test generator configuration settings with coupled custom data center id and default machine id")
    void testGeneratorConfigurationSettingsWithCoupledCustomDataCenterIdAndDefaultMachineId() {
        //Given
        ShardIdSettings[] expectedShardIdSettings = new ShardIdSettings[]{
                EXPECTED_SHARD_COORDINATE_SETTINGS,
                EXPECTED_DEFAULT_SHARD_COORDINATE_SETTINGS
        };
        //When
        Configuration actualConfiguration = new GeneratorConfiguration.Builder()
                .withStartPoint(VALID_START_POINT)
                .withTimestampBits(GUARANTEED_TEN_YEARS_BIT_SIZE)
                .withDataCenterId(VALID_GRANTED_BITS_MAX_VALUE)
                .withDataCenterIdBits(VALID_GRANTED_BITS)
                .withMachineIdBits(VALID_GRANTED_BITS)
                .withSequenceBits(VALID_GRANTED_BITS)
                .withSequenceOverflowStrategy(SequenceOverflowHandleStrategy.THREAD_FIXED_SLEEP)
                .build();
        //Then
        assertAll(
                () -> assertThat(actualConfiguration.getTimestampSettings()).isEqualTo(EXPECTED_TIMESTAMP_SETTINGS),
                () -> assertThat(actualConfiguration.getShardIdSettings()).isEqualTo(expectedShardIdSettings),
                () -> assertThat(actualConfiguration.getSequenceSettings()).isEqualTo(EXPECTED_SEQUENCE_SETTINGS)
        );
    }
}
