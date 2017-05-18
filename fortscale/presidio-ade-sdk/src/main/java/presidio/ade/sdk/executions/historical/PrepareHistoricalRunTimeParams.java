package presidio.ade.sdk.executions.historical;

import com.cronutils.model.Cron;

import java.time.Duration;
import java.time.Instant;

/**
 * When a mission of digestion historical data is needed, it should be accompanied by those time params.
 * the time fields are event-time (logical time) and not wall-clock time (system-time)
 * Created by barak_schuster on 5/17/17.
 */
public class PrepareHistoricalRunTimeParams {
    private final Duration dataProcessingDuration;
    private final Duration gapDuration;
    private final Instant startInstant;
    private final Instant endInstant;
    private final Cron scheduleInterval;

    /**
     * C'tor
     * example:
     * for given params: startInstant=1970-01-01T00:00:00Z,endInstant=1970-01-02T00:00:00Z,dataProcessingIntervalUnit=2Hours, scheduleInterval="0 0 0 1/1 * ? *"
     * the gapDuration=1Day
     * means that the data of this day should be processed in 12 chunks (2 Hours each) once a day in midnight
     *
     * @param startInstant               the batch data processing will be executed from that date
     * @param endInstant                 the batch data processing will be executed till that date
     * @param dataProcessingDuration    the data should be processed in partitions of this interval duration
     * @param scheduleInterval           a cron expression signifying the schedule interval (once in when to run) of the mission
     */
    public PrepareHistoricalRunTimeParams(Instant startInstant, Instant endInstant, Duration dataProcessingDuration, Cron scheduleInterval) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.dataProcessingDuration = dataProcessingDuration;
        this.scheduleInterval = scheduleInterval;
        this.gapDuration = Duration.between(startInstant, endInstant);
    }

    public Cron getScheduleInterval() {
        return scheduleInterval;
    }

    public Duration getGapDuration() {
        return gapDuration;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }

    public Duration getDataProcessingDuration() {
        return dataProcessingDuration;
    }

}
