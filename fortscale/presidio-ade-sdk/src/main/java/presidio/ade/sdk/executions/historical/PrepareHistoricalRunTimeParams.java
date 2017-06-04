package presidio.ade.sdk.executions.historical;

import java.time.Duration;
import java.time.Instant;

/**
 * When a mission of digestion historical data is needed, it should be accompanied by those time params.
 * the time fields are event-time (logical time) and not wall-clock time (system-time)
 * Created by barak_schuster on 5/17/17.
 */
public class PrepareHistoricalRunTimeParams {
    private final Instant startInstant;
    private final Instant endInstant;
    private final Duration timeDelta;

    /**
     * C'tor
     * example:
     * for given params: startInstant=1970-01-01T00:00:00Z,endInstant=1970-01-02T00:00:00Z, timeDelta="days=1"
     *
     * means that the data of this day should be processed in once a day
     * @param startInstant               the batch data processing will be executed from that date
     * @param endInstant                 the batch data processing will be executed till that date
     * @param timeDelta
     */
    public PrepareHistoricalRunTimeParams(Instant startInstant, Instant endInstant, Duration timeDelta) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.timeDelta = timeDelta;
    }

    public Duration getTimeDelta() {
        return timeDelta;
    }


    public Instant getStartInstant() {
        return startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }


}
