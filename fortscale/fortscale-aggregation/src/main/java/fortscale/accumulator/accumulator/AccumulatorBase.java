package fortscale.accumulator.accumulator;

import fortscale.utils.logging.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static fortscale.accumulator.accumulator.AccumulationParams.TimeFrame.DAILY;

/**
 * Created by barak_schuster on 10/9/16.
 */
public abstract class AccumulatorBase implements Accumulator {
    private final Logger logger;

    public AccumulatorBase(Logger logger) {
        this.logger = logger;
    }

    public void run(AccumulationParams params) {
        beforeRun(params);

        logger.info("running accumulation by params={}", params);
        AccumulationParams.TimeFrame timeFrame = params.getTimeFrame();
        if (!timeFrame.equals(DAILY)) {
            throw new UnsupportedOperationException(String.format(
                    "%s does not support accumulation of timeFrame=%s",
                    getClass().getSimpleName(), timeFrame));
        }

        Instant from = params.getFrom();
        Instant to = params.getTo();
        Instant fromCursor = Instant.from(from);
        String featureName = params.getFeatureName();


        while (fromCursor.isBefore(to)) {
            Instant toCursor;

            if (fromCursor.plus(1, ChronoUnit.DAYS).isAfter(to)) {
                toCursor = to;
            } else {
                toCursor = fromCursor.plus(1, ChronoUnit.DAYS);
            }
            accumulateEvents(featureName, fromCursor, toCursor);

            fromCursor = toCursor;
        }
    }

    protected abstract void beforeRun(AccumulationParams params);

    public abstract void accumulateEvents(String featureName, final Instant fromCursor, final Instant toCursor);


}
