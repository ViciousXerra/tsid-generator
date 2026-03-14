package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.SequenceOverflowHandleStrategy;
import io.github.viciousxerra.tsidgenerator.exception.TimelineBeforeStartPointException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.GENERAL_BIT_SIZE_EXCEEDED_MESSAGE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.MUST_NOT_BE_GREATER_THAN_TEMPLATE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.NUMBER_OF_SEQUENCE_GRANTED_BITS_MUST_BE_POSITIVE_MESSAGE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.SEQUENCE_OVERFLOW_HANDLER_STRATEGY_NULL_MESSAGE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.SHARD_ID_MUST_NOT_BE_GREATER_THAN_MESSAGE_TEMPLATE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.START_POINT_NULL_MESSAGE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.TIMELINE_BEFORE_START_POINT_TEMPLATE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.TIMESTAMP_BITS_TEN_YEARS_WARRANTY_MESSAGE_TEMPLATE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.UNSUPPORTED_CONFIGURATION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneratorConfigurationInvalidInputsTest {
    private static final OffsetDateTime OFFSET_DATE_TIME_NOW = OffsetDateTime.parse("2026-01-01T00:00:00+00:00");
    private static final OffsetDateTime VALID_START_POINT = OFFSET_DATE_TIME_NOW.minusSeconds(5L);
    private static final OffsetDateTime INVALID_START_POINT = OFFSET_DATE_TIME_NOW.plusSeconds(5L);
    private static final ZoneOffset ZONE_OFFSET = OFFSET_DATE_TIME_NOW.getOffset();
    private static final int GUARANTEED_TEN_YEARS_BIT_SIZE = 39;
    private static final int VALID_GRANTED_BITS = 2;
    private static final int VALID_GRANTED_BITS_MAX_VALUE = (1 << VALID_GRANTED_BITS) - 1;
    private static final int MAX_BITS_LIMIT = 63;
    private static final SequenceOverflowHandleStrategy VALID_STRATEGY = SequenceOverflowHandleStrategy.THREAD_FIXED_SLEEP;

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

    @ParameterizedTest
    @MethodSource("getInvalidStartPointAndExpectedExceptionWithMessage")
    @DisplayName("Test invalid start point inputs")
    void testInvalidStartInputs(Class<? extends Throwable> exceptionClass, OffsetDateTime startPoint, String message) {
        assertThatThrownBy(() -> {
            new GeneratorConfiguration.Builder()
                    .withStartPoint(startPoint)
                    .build();
        })
                .isExactlyInstanceOf(exceptionClass)
                .hasMessage(message);
    }

    @ParameterizedTest
    @MethodSource("getInvalidTimestampNumberOfGrantedBits")
    @DisplayName("Test invalid timestamp number of granted bits")
    void testInvalidTimestampNumberOfGrantedBits(int timestampBits) {
        assertThatThrownBy(() -> {
            new GeneratorConfiguration.Builder()
                    .withStartPoint(VALID_START_POINT)
                    .withTimestampBits(timestampBits)
                    .build();
        })
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(TIMESTAMP_BITS_TEN_YEARS_WARRANTY_MESSAGE_TEMPLATE, GUARANTEED_TEN_YEARS_BIT_SIZE));

    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0})
    @DisplayName("Test invalid sequence number of granted bits")
    void testInvalidSequenceNumberOfGrantedBits(int sequenceBits) {
        assertThatThrownBy(() -> {
            new GeneratorConfiguration.Builder()
                    .withStartPoint(VALID_START_POINT)
                    .withTimestampBits(GUARANTEED_TEN_YEARS_BIT_SIZE)
                    .withSequenceBits(sequenceBits)
                    .build();
        })
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage(NUMBER_OF_SEQUENCE_GRANTED_BITS_MUST_BE_POSITIVE_MESSAGE);
    }

    @Test
    @DisplayName("Test null sequence overflow strategy enum")
    void testNullSequenceOverflowStrategy() {
        assertThatThrownBy(() -> {
            new GeneratorConfiguration.Builder()
                    .withStartPoint(VALID_START_POINT)
                    .withTimestampBits(GUARANTEED_TEN_YEARS_BIT_SIZE)
                    .withSequenceBits(VALID_GRANTED_BITS)
                    .withSequenceOverflowStrategy(null)
                    .build();
        })
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage(SEQUENCE_OVERFLOW_HANDLER_STRATEGY_NULL_MESSAGE);
    }

    @ParameterizedTest
    @MethodSource("getInvalidShardCoordinatesInputs")
    @DisplayName("Test invalid shard coordinates inputs")
    void testInvalidShardCoordinatesInputs(int shardId, int shardIdBits,
                                           int dataCenterId, int dataCenterIdBits,
                                           int machineId, int machineIdBits) {
        assertThatThrownBy(() -> {
            new GeneratorConfiguration.Builder()
                    .withStartPoint(VALID_START_POINT)
                    .withTimestampBits(GUARANTEED_TEN_YEARS_BIT_SIZE)
                    .withSequenceBits(VALID_GRANTED_BITS)
                    .withSequenceOverflowStrategy(VALID_STRATEGY)
                    .withShardId(shardId)
                    .withShardIdBits(shardIdBits)
                    .withDataCenterId(dataCenterId)
                    .withDataCenterIdBits(dataCenterIdBits)
                    .withMachineId(machineId)
                    .withMachineIdBits(machineIdBits)
                    .build();
        })
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage(UNSUPPORTED_CONFIGURATION_MESSAGE);
    }

    @ParameterizedTest
    @ValueSource(ints = {VALID_GRANTED_BITS_MAX_VALUE + 1, Integer.MAX_VALUE})
    @DisplayName("Test invalid shard ID")
    void testInvalidShardIdInputs(int invalidShardId) {
        assertThatThrownBy(() -> {
            new GeneratorConfiguration.Builder()
                    .withStartPoint(VALID_START_POINT)
                    .withTimestampBits(GUARANTEED_TEN_YEARS_BIT_SIZE)
                    .withSequenceBits(VALID_GRANTED_BITS)
                    .withSequenceOverflowStrategy(VALID_STRATEGY)
                    .withShardId(invalidShardId)
                    .withShardIdBits(VALID_GRANTED_BITS)
                    .build();
        })
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(SHARD_ID_MUST_NOT_BE_GREATER_THAN_MESSAGE_TEMPLATE, VALID_GRANTED_BITS_MAX_VALUE));
    }

    @ParameterizedTest
    @MethodSource("getDataCenterIdAndMachineIdInvalidInputs")
    @DisplayName("Test invalid data center ID and machine ID inputs")
    void testInvalidDataCenterIdAndMachineIdInputs(int dataCenterId, int machineId, String problemPart) {
        assertThatThrownBy(() -> {
            new GeneratorConfiguration.Builder()
                    .withStartPoint(VALID_START_POINT)
                    .withTimestampBits(GUARANTEED_TEN_YEARS_BIT_SIZE)
                    .withSequenceBits(VALID_GRANTED_BITS)
                    .withSequenceOverflowStrategy(VALID_STRATEGY)
                    .withDataCenterId(dataCenterId)
                    .withDataCenterIdBits(VALID_GRANTED_BITS)
                    .withMachineId(machineId)
                    .withMachineIdBits(VALID_GRANTED_BITS)
                    .build();
        })
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(problemPart + MUST_NOT_BE_GREATER_THAN_TEMPLATE, VALID_GRANTED_BITS_MAX_VALUE));
    }

    @ParameterizedTest
    @MethodSource("getExceedingMaxBitLimitNumbersOfGrantedBits")
    @DisplayName("Test exceeding max bit limit numbers of granted bits")
    void testExceedingMaxBitsLimit(int shardIdBits, int dataCenterIdBits, int machineIdBits) {
        assertThatThrownBy(() -> {
            new GeneratorConfiguration.Builder()
                    .withStartPoint(VALID_START_POINT)
                    .withTimestampBits(GUARANTEED_TEN_YEARS_BIT_SIZE)
                    .withSequenceBits(VALID_GRANTED_BITS)
                    .withSequenceOverflowStrategy(VALID_STRATEGY)
                    .withShardIdBits(shardIdBits)
                    .withDataCenterIdBits(dataCenterIdBits)
                    .withMachineIdBits(machineIdBits)
                    .build();
        })
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage(GENERAL_BIT_SIZE_EXCEEDED_MESSAGE);
    }

    private static Stream<Arguments> getInvalidStartPointAndExpectedExceptionWithMessage() {
        return Stream.of(
                Arguments.of(NullPointerException.class, null,
                        START_POINT_NULL_MESSAGE),
                Arguments.of(TimelineBeforeStartPointException.class, INVALID_START_POINT,
                        String.format(TIMELINE_BEFORE_START_POINT_TEMPLATE, OFFSET_DATE_TIME_NOW, INVALID_START_POINT))
        );
    }

    private static Stream<Integer> getInvalidTimestampNumberOfGrantedBits() {
        return IntStream.range(0, GUARANTEED_TEN_YEARS_BIT_SIZE).boxed();
    }

    private static Stream<Arguments> getInvalidShardCoordinatesInputs() {
        return Stream.of(
                Arguments.of(
                        VALID_GRANTED_BITS_MAX_VALUE, VALID_GRANTED_BITS,
                        VALID_GRANTED_BITS_MAX_VALUE, VALID_GRANTED_BITS,
                        VALID_GRANTED_BITS_MAX_VALUE, VALID_GRANTED_BITS),
                Arguments.of(
                        VALID_GRANTED_BITS_MAX_VALUE, VALID_GRANTED_BITS,
                        0, 0,
                        VALID_GRANTED_BITS_MAX_VALUE, VALID_GRANTED_BITS),
                Arguments.of(
                        0, VALID_GRANTED_BITS,
                        0, 0,
                        0, VALID_GRANTED_BITS),
                Arguments.of(
                        VALID_GRANTED_BITS_MAX_VALUE, VALID_GRANTED_BITS,
                        VALID_GRANTED_BITS_MAX_VALUE, VALID_GRANTED_BITS,
                        0, 0),
                Arguments.of(
                        0, VALID_GRANTED_BITS,
                        0, VALID_GRANTED_BITS,
                        0, 0),
                Arguments.of(
                        VALID_GRANTED_BITS_MAX_VALUE, 0,
                        0, 0,
                        0, 0),
                Arguments.of(
                        0, 0,
                        VALID_GRANTED_BITS_MAX_VALUE, 0,
                        0, 0),
                Arguments.of(
                        0, 0,
                        VALID_GRANTED_BITS_MAX_VALUE, VALID_GRANTED_BITS,
                        0, 0),
                Arguments.of(
                        0, 0,
                        0, 0,
                        VALID_GRANTED_BITS_MAX_VALUE, 0),
                Arguments.of(
                        0, 0,
                        0, 0,
                        VALID_GRANTED_BITS_MAX_VALUE, VALID_GRANTED_BITS),
                Arguments.of(
                        0, 0,
                        VALID_GRANTED_BITS_MAX_VALUE, 0,
                        VALID_GRANTED_BITS_MAX_VALUE, 0),
                Arguments.of(
                        0, 0,
                        VALID_GRANTED_BITS_MAX_VALUE, VALID_GRANTED_BITS,
                        VALID_GRANTED_BITS_MAX_VALUE, 0),
                Arguments.of(
                        0, 0,
                        VALID_GRANTED_BITS_MAX_VALUE, 0,
                        VALID_GRANTED_BITS_MAX_VALUE, VALID_GRANTED_BITS)
        );
    }

    private static Stream<Arguments> getDataCenterIdAndMachineIdInvalidInputs() {
        return Stream.of(
                Arguments.of(VALID_GRANTED_BITS_MAX_VALUE + 1, 0, "Data center ID"),
                Arguments.of(Integer.MAX_VALUE, 0, "Data center ID"),
                Arguments.of(VALID_GRANTED_BITS_MAX_VALUE, VALID_GRANTED_BITS_MAX_VALUE + 1, "Machine ID"),
                Arguments.of(VALID_GRANTED_BITS_MAX_VALUE, Integer.MAX_VALUE, "Machine ID")
        );
    }

    private static Stream<Arguments> getExceedingMaxBitLimitNumbersOfGrantedBits() {
        int remainingBits = MAX_BITS_LIMIT - GUARANTEED_TEN_YEARS_BIT_SIZE - VALID_GRANTED_BITS;
        return Stream.of(
                Arguments.of(remainingBits + 1, 0, 0),
                Arguments.of(0, remainingBits - 1, 2),
                Arguments.of(0, 2, remainingBits - 1)
        );
    }
}
