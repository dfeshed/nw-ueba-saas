package fortscale.utils.fixedduration;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.Duration;

/**
 * Determines the time context of fixed-duration-based batch processes:
 * For example, daily strategy in aggregation tasks means that daily aggregations should be built (but not hourly).
 *
 * @author Barak Schuster
 * @author Lior Govrin
 */
public enum FixedDurationStrategy {
    HOURLY(Constants.STRATEGY_NAME_FIXED_DURATION_HOURLY, Constants.DURATION_FIXED_DURATION_HOURLY),
    DAILY(Constants.STRATEGY_NAME_FIXED_DURATION_DAILY, Constants.DURATION_FIXED_DURATION_DAILY);

    private final String strategyName;
    private final Duration duration;

    FixedDurationStrategy(String strategyName, Duration duration) {
        this.strategyName = strategyName;
        this.duration = duration;
    }

    public String toStrategyName() {
        return strategyName;
    }

    public Duration toDuration() {
        return duration;
    }

    public static FixedDurationStrategy fromStrategyName(String strategyName) {
        switch (strategyName) {
            case Constants.STRATEGY_NAME_FIXED_DURATION_HOURLY:
                return HOURLY;
            case Constants.STRATEGY_NAME_FIXED_DURATION_DAILY:
                return DAILY;
            default:
                throw new IllegalArgumentException(String.format("Unsupported strategy name %s.", strategyName));
        }
    }

    public static FixedDurationStrategy fromDuration(Duration duration) {
        if (duration.equals(Constants.DURATION_FIXED_DURATION_HOURLY)) {
            return HOURLY;
        } else if (duration.equals(Constants.DURATION_FIXED_DURATION_DAILY)) {
            return DAILY;
        } else {
            throw new IllegalArgumentException(String.format("Unsupported duration %s.", duration.toString()));
        }
    }

    public static FixedDurationStrategy fromSeconds(long durationInSeconds) {
        return fromDuration(Duration.ofSeconds(durationInSeconds));
    }

    private static final class Constants {
        private static final String STRATEGY_NAME_FIXED_DURATION_HOURLY = "fixed_duration_hourly";
        private static final Duration DURATION_FIXED_DURATION_HOURLY = Duration.ofHours(1);
        private static final String STRATEGY_NAME_FIXED_DURATION_DAILY = "fixed_duration_daily";
        private static final Duration DURATION_FIXED_DURATION_DAILY = Duration.ofDays(1);
    }

    /**
     * @return ToString you know...
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
