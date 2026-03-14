package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.exception.BitOverflowException;
import io.github.viciousxerra.tsidgenerator.exception.TimelineBeforeStartPointException;
import io.github.viciousxerra.tsidgenerator.exception.TimestampOverflowException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;

import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.TIMELINE_BEFORE_START_POINT_TEMPLATE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.TIMESTAMP_OVERFLOW_TEMPLATE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class TimeUtilsTest {
    private static final OffsetDateTime OFFSET_DATE_TIME_NOW = OffsetDateTime.parse("2026-01-01T00:00:00+00:00");
    private static final OffsetDateTime VALID_START_POINT = OFFSET_DATE_TIME_NOW.minusSeconds(5L);
    private static final OffsetDateTime INVALID_START_POINT = OFFSET_DATE_TIME_NOW.plusSeconds(5L);
    private static final long MAX_VALUE = 31L;

    @Test
    @DisplayName("Test \"checkTimelineBeforeStartPoint\" method throws TimelineBeforeStartPointException")
    void testCheckTimelineBeforeStartPointMethodThrowsTimelineBeforeStartPointException() {
        assertThatThrownBy(() -> TimeUtils.checkTimelineBeforeStartPoint(OFFSET_DATE_TIME_NOW, INVALID_START_POINT))
                .isExactlyInstanceOf(TimelineBeforeStartPointException.class)
                .hasMessage(String.format(TIMELINE_BEFORE_START_POINT_TEMPLATE, OFFSET_DATE_TIME_NOW, INVALID_START_POINT));
    }

    @Test
    @DisplayName("Test \"checkTimelineBeforeStartPoint\" method doesn't throw any")
    void testCheckTimelineBeforeStartPointMethodDoesNotThrowAny() {
        assertAll(
                () -> assertThatCode(
                        () -> TimeUtils.checkTimelineBeforeStartPoint(OFFSET_DATE_TIME_NOW, OFFSET_DATE_TIME_NOW))
                        .doesNotThrowAnyException(),
                () -> assertThatCode(
                        () -> TimeUtils.checkTimelineBeforeStartPoint(OFFSET_DATE_TIME_NOW, VALID_START_POINT))
                        .doesNotThrowAnyException()
        );
    }

    @Test
    @DisplayName("Test \"checkEpochMillisTimestampOverflow\" method throws TimestampOverflowException")
    void testCheckEpochMillisTimestampOverflowMethodThrowsTimestampOverflowException() {
        assertThatThrownBy(
                () -> TimeUtils.checkEpochMillisTimestampOverflow(VALID_START_POINT, MAX_VALUE + 1, MAX_VALUE)
        )
                .isExactlyInstanceOf(TimestampOverflowException.class)
                .hasMessage(String.format(
                        TIMESTAMP_OVERFLOW_TEMPLATE,
                        VALID_START_POINT.plus(Duration.ofMillis(MAX_VALUE + 1)),
                        VALID_START_POINT.plus(Duration.ofMillis(MAX_VALUE))))
                .hasCauseExactlyInstanceOf(BitOverflowException.class);
    }

    @Test
    @DisplayName("Test \"checkEpochMillisTimestampOverflow\" method doesn't throw any")
    void testCheckEpochMillisTimestampOverflowMethodDoesNotThrowAny() {
        assertAll(
                () -> assertThatCode(
                        () -> TimeUtils.checkEpochMillisTimestampOverflow(VALID_START_POINT, MAX_VALUE, MAX_VALUE))
                        .doesNotThrowAnyException(),
                () -> assertThatCode(
                        () -> TimeUtils.checkEpochMillisTimestampOverflow(VALID_START_POINT, MAX_VALUE - 1, MAX_VALUE))
                        .doesNotThrowAnyException()
        );
    }
}
