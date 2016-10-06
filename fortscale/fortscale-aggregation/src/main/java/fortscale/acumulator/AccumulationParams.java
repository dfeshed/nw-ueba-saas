package fortscale.acumulator;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.Instant;

/**
 * params the {@link Accumulator} should execute by
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulationParams {

    public enum TimeFrame
    {
        // 24 hour documents will be accumulated into 1 daily document
        DAILY
    }
    private TimeFrame timeFrame;
    // source data should be accumulated from that date
    private Instant from;
    // source data should be accumulated till that date
    private Instant to;
    // source feature name to be accumulated
    private String featureName;

    /**
     * C'tor
     *
     * accumulation of {@param featureName} will be preformed between the dates: {@param from},{@param to}
     * into buckets in size of a {@param timeFrame} and written into accumulated collection
     */
    public AccumulationParams(String featureName,
                              TimeFrame timeFrame, Instant from, Instant to) {
        this.featureName = featureName;
        this.from = from;
        this.timeFrame = timeFrame;
        this.to = to;
    }

    /**
     * @return To String. you know...
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    // --- Getters/setters ---

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(TimeFrame timeFrame) {
        this.timeFrame = timeFrame;
    }

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public Instant getTo() {
        return to;
    }

    public void setTo(Instant to) {
        this.to = to;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

}
