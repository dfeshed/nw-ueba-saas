package presidio.ade.sdk.online_run;

import presidio.ade.sdk.common.AdeRunParams;

import java.time.Duration;
import java.time.Instant;

/**
 * When a request to digest online data is made, it should be accompanied by these parameters.
 * The time fields are event time (logical time) and not wall clock time (system time).
 *
 * @author Barak Schuster
 */
public class OnlineRunParams extends AdeRunParams {
    /**
     * C'tor.
     * If startInstant = 1970-01-01T00:00:00Z and timeDelta = 1 day,
     * then the data from this day onwards will be processed in daily batches.
     *
     * @param startInstant the starting point of the online run
     * @param timeDelta    online data is processed in batches that are "timeDelta" long
     */
    public OnlineRunParams(Instant startInstant, Duration timeDelta) {
        super(startInstant, timeDelta);
    }
}
