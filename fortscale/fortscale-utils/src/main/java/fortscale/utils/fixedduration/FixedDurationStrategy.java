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

    public Duration toDuration() {
        if (this.equals(DAILY)) {
            return Duration.ofDays(1);
        }
        else {
            return Duration.ofHours(1);
        }
    }
}
