package presidio.ade.sdk.executions.online;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by barak_schuster on 5/18/17.
 */
public class PrepareOnlineRunTimeParams {
    private final Instant startInstant;
    private final Duration timeDelta;

    /**
     * example:
     * for given params: startInstant=1970-01-01T00:00:00Z,timeDelta=2hours
     *
     * means that the data of this day should be from this point each 2 Hours
     *  @param startInstant               the batch data processing will be executed from that date
     * @param timeDelta           a duration expression signifying the schedule interval (once in when to run) of the mission
     */
    public PrepareOnlineRunTimeParams(Instant startInstant, Duration timeDelta) {
        this.startInstant = startInstant;
        this.timeDelta = timeDelta;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public Duration getTimeDelta() {
        return timeDelta;
    }


}
