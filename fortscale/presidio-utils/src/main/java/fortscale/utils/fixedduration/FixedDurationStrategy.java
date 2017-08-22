package fortscale.utils.fixedduration;

import java.time.Duration;

/**
 * determines the time context of fixed-duration based batch processes
 * i.e. Daily strategy in aggregation task means that daily aggregation should be built (hourly -> should not)
 * Created by barak_schuster on 6/11/17.
 */
public enum FixedDurationStrategy {
    DAILY,
    HOURLY;

    public static final String STRATEGY_NAME_FIXED_DURATION_HOURLY = "fixed_duration_hourly";
    public static final String STRATEGY_NAME_FIXED_DURATION_DAILY = "fixed_duration_daily";

    public Duration toDuration() {
        if (this.equals(DAILY)) {
            return Duration.ofDays(1);
        }
        else {
            return Duration.ofHours(1);
        }
    }

    public String toStrategyName()
    {
        switch (this)
        {
            case DAILY:
                return STRATEGY_NAME_FIXED_DURATION_DAILY;
            case HOURLY:
                return STRATEGY_NAME_FIXED_DURATION_HOURLY;
            default:
                throw new RuntimeException("cannot convert fixedDurationStrategy into strategyName");
        }
    }
    public static FixedDurationStrategy fromDuration(Duration duration)
    {
        if(duration.equals(Duration.ofDays(1)))
        {
            return DAILY;
        }
        else
        {
            return HOURLY;
        }
    }

    public static FixedDurationStrategy fromSeconds(long durationInSeconds)
    {
        return fromDuration(Duration.ofSeconds(durationInSeconds));
    }

    public static FixedDurationStrategy fromStartegyName(String strategyName)
    {
        if (strategyName.equals(STRATEGY_NAME_FIXED_DURATION_HOURLY))
        {
            return FixedDurationStrategy.HOURLY;
        }
        else if (strategyName.equals(STRATEGY_NAME_FIXED_DURATION_DAILY))
        {
            return FixedDurationStrategy.DAILY;
        }
        else
        {
            throw new IllegalArgumentException("Unsupported fixed duration strategy name");
        }
    }
}
