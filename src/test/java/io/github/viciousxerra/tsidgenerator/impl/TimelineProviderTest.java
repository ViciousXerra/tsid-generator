package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.Configuration;
import io.github.viciousxerra.tsidgenerator.api.TimelineProvider;
import io.github.viciousxerra.tsidgenerator.api.TimestampSettings;
import io.github.viciousxerra.tsidgenerator.exception.ClockMoveBackwardsException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.CLOCK_MOVE_BACKWARDS_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TimelineProviderTest {
    private static final OffsetDateTime OFFSET_DATE_TIME_NOW = OffsetDateTime.parse("2026-01-01T00:00:00+00:00");
    private static final long SECONDS_DELTA = 5L;
    private static final long MILLISECONDS_DELTA = SECONDS_DELTA * 1000L;
    private static final OffsetDateTime VALID_START_POINT = OFFSET_DATE_TIME_NOW.minusSeconds(SECONDS_DELTA);
    private static final OffsetDateTime INVALID_START_POINT = OFFSET_DATE_TIME_NOW.plusSeconds(SECONDS_DELTA);
    private static final ZoneOffset ZONE_OFFSET = OFFSET_DATE_TIME_NOW.getOffset();
    private static final int GUARANTEED_TEN_YEARS_BIT_SIZE = 39;

    @Mock
    private Configuration configuration;
    private static MockedStatic<OffsetDateTime> offsetDateTimeMockedStatic;

    @BeforeAll
    static void setUp() {
        offsetDateTimeMockedStatic = Mockito.mockStatic(OffsetDateTime.class);
        offsetDateTimeMockedStatic.when(() -> OffsetDateTime.now(ZONE_OFFSET)).thenReturn(OFFSET_DATE_TIME_NOW.withOffsetSameInstant(ZONE_OFFSET));
    }

    @AfterAll
    static void tearDown() {
        offsetDateTimeMockedStatic.close();
    }

    @Test
    @DisplayName("Test provided millis on mocked OffsetDateTime.now()")
    void testProvidedMillisWithMock() {
        //Given
        Mockito.when(configuration.getTimestampSettings()).thenReturn(
                new TimestampSettings(VALID_START_POINT, MILLISECONDS_DELTA, GUARANTEED_TEN_YEARS_BIT_SIZE));
        TimelineProvider timelineProvider = new TimelineProviderImpl(configuration);
        //When
        long actualMillis = timelineProvider.provideCurrentMillis();
        //Then
        assertThat(actualMillis).isEqualTo(MILLISECONDS_DELTA);
    }

    @Test
    @DisplayName("Test provided millis throws ClockMoveBackwardsException")
    void testProvidedMillisThrowsClockMoveBackwardsException() {
        //Given
        Mockito.when(configuration.getTimestampSettings()).thenReturn(
                new TimestampSettings(INVALID_START_POINT, MILLISECONDS_DELTA, GUARANTEED_TEN_YEARS_BIT_SIZE));
        //When
        TimelineProvider timelineProvider = new TimelineProviderImpl(configuration);
        //Then
        assertThatThrownBy(timelineProvider::provideCurrentMillis)
                .isExactlyInstanceOf(ClockMoveBackwardsException.class)
                .hasMessage(CLOCK_MOVE_BACKWARDS_MESSAGE);
    }

}
