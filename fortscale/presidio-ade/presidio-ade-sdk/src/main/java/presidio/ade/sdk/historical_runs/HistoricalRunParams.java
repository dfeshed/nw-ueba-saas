package presidio.ade.sdk.historical_runs;

import presidio.ade.sdk.common.AdeRunParams;

import java.time.Duration;
import java.time.Instant;

/**
 * When a request to digest historical data is made, it should be accompanied by these parameters.
 * The time fields are event time (logical time) and not wall clock time (system time).
 *
 * @author Barak Schuster
 */
public class HistoricalRunParams extends AdeRunParams {
    private final Instant endInstant;

    /**
     * C'tor.
     * If startInstant = 1970-01-01T00:00:00Z, endInstant = 1970-01-02T00:00:00Z and timeDelta = 1 day,
     * then the data of this day will be processed in one daily batch.
     *
     * @param startInstant the starting point of the batch data processing
     * @param endInstant   the ending point of the batch data processing
     * @param timeDelta    the time range is split into segments that are "timeDelta" long
     */
    public HistoricalRunParams(Instant startInstant, Instant endInstant, Duration timeDelta) {
        super(startInstant, timeDelta);
        this.endInstant = endInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }
}
