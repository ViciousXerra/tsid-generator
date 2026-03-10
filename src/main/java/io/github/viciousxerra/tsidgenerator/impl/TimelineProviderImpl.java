package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.Configuration;
import io.github.viciousxerra.tsidgenerator.api.TimelineProvider;
import io.github.viciousxerra.tsidgenerator.exception.ClockMoveBackwardsException;
import java.time.Duration;
import java.time.OffsetDateTime;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.CLOCK_MOVE_BACKWARDS_MESSAGE;

final class TimelineProviderImpl implements TimelineProvider {
    private final OffsetDateTime startPoint;
    private final long millisMaxValue;

    TimelineProviderImpl(Configuration configuration) {
        this.startPoint = configuration.getTimestampSettings().startPoint();
        this.millisMaxValue = configuration.getTimestampSettings().millisMaxValue();
    }

    @Override
    public long provideCurrentMillis() {
        var now = Duration.between(startPoint.toInstant(), OffsetDateTime.now(startPoint.getOffset()).toInstant())
                .toMillis();
        if (now < 0) {
            throw new ClockMoveBackwardsException(CLOCK_MOVE_BACKWARDS_MESSAGE);
        }
        TimeUtils.checkEpochMillisTimestampOverflow(startPoint, now, millisMaxValue);
        return now;
    }
}
