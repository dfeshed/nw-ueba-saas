package presidio.ade.sdk.executions.online;

import com.cronutils.model.Cron;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by barak_schuster on 5/18/17.
 */
public class PrepareOnlineRunTimeParams {
    private final Instant startInstant;
    private final Cron scheduleInterval;
    private final Duration dataProcessingInterval;

    /**
     * example:
     * for given params: startInstant=1970-01-01T00:00:00Z,dataProcessingDuration=2hours
     * the gapDuration=1Day
     * means that the data of this day should be processed in 12 chunks (2 Hour each)
     *
     * @param startInstant               the batch data processing will be executed from that date
     * @param scheduleInterval           a cron expression signifying the schedule interval (once in when to run) of the mission
     * @param dataProcessingDuration     the data should be processed in partitions of this interval unit
     */
    public PrepareOnlineRunTimeParams(Instant startInstant, Cron scheduleInterval, Duration dataProcessingDuration ) {
        this.startInstant = startInstant;
        this.scheduleInterval = scheduleInterval;
        this.dataProcessingInterval = dataProcessingDuration;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public Cron getScheduleInterval() {
        return scheduleInterval;
    }

}
