package fortscale.utils.time;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by maria_dorohin on 7/31/17.
 */
public class TimeService {

    public static Instant floorTime(Instant date, Duration timeDelta){
        return floorTime(date, timeDelta.getSeconds());
    }

    public static Instant floorTime(Instant date, long timeDeltaInSeconds){
        long roundDown = date.getEpochSecond() / timeDeltaInSeconds * timeDeltaInSeconds;
        return Instant.ofEpochSecond(roundDown);
    }
}
