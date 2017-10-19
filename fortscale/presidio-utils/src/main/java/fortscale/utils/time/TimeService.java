package fortscale.utils.time;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by maria_dorohin on 7/31/17.
 */
public class TimeService {

    public static Instant floorTime(Instant date, Duration timeDelta){
        long roundDown = date.getEpochSecond() / timeDelta.getSeconds() * timeDelta.getSeconds();
        return Instant.ofEpochSecond(roundDown);
    }

    public static Instant floorTime(Instant date, long timeDeltaInSeconds){
        return floorTime(date, Duration.ofSeconds(timeDeltaInSeconds));
    }
}
