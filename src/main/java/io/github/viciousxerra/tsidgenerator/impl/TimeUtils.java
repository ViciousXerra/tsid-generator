package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.exception.BitOverflowException;
import io.github.viciousxerra.tsidgenerator.exception.TimelineBeforeStartPointException;
import io.github.viciousxerra.tsidgenerator.exception.TimestampOverflowException;
import java.time.Duration;
import java.time.OffsetDateTime;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.TIMELINE_BEFORE_START_POINT_TEMPLATE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.TIMESTAMP_OVERFLOW_TEMPLATE;

final class TimeUtils {

    private TimeUtils() {
    }

    static void checkTimelineBeforeStartPoint(OffsetDateTime now, OffsetDateTime startPoint) {
        if (now.isBefore(startPoint)) {
            throw new TimelineBeforeStartPointException(String.format(TIMELINE_BEFORE_START_POINT_TEMPLATE, now,
                    startPoint));
        }
    }

    static void checkEpochMillisTimestampOverflow(OffsetDateTime startPoint, long currentMillis, long maxMillis) {
        try {
            BitUtils.checkBitOverflow(currentMillis, maxMillis);
        } catch (BitOverflowException e) {
            throw new TimestampOverflowException(
                    String.format(
                            TIMESTAMP_OVERFLOW_TEMPLATE,
                            startPoint.plus(Duration.ofMillis(currentMillis)),
                            startPoint.plus(Duration.ofMillis(maxMillis))),
                    e);
        }
    }

}
