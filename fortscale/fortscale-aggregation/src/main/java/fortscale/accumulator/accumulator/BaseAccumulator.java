package fortscale.accumulator.accumulator;

import fortscale.utils.logging.Logger;

import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import static fortscale.accumulator.accumulator.AccumulationParams.TimeFrame.DAILY;
import static fortscale.accumulator.translator.BaseAccumulatedFeatureTranslator.DAILY_FEATURE_SUFFIX;
import static fortscale.accumulator.translator.BaseAccumulatedFeatureTranslator.HOURLY_FEATURE_SUFFIX;
import static fortscale.utils.time.TimeUtils.getAmountOfDaysInPeriod;

/**
 * Created by barak_schuster on 10/9/16.
 */
public abstract class BaseAccumulator implements Accumulator {
    private final Logger logger;
    private final Period defaultFromPeriodDaily;
    private final Period defaultFromPeriodHourly;

    /**
     *
     * @param logger you know...
     * @param defaultFromPeriodDaily if accumulation is ran without from params, daily accumulation start from now-this period
     * @param defaultFromPeriodHourly if accumulation is ran without from params, hourly accumulation start from now-this period
     */
    public BaseAccumulator(Logger logger, Period defaultFromPeriodDaily, Period defaultFromPeriodHourly) {
        this.logger = logger;
        this.defaultFromPeriodDaily = defaultFromPeriodDaily;
        this.defaultFromPeriodHourly = defaultFromPeriodHourly;
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

            toCursor = fromCursor.plus(1, ChronoUnit.DAYS);

            accumulateEvents(featureName, fromCursor, toCursor);

            fromCursor = toCursor;
        }
    }

    protected abstract void beforeRun(AccumulationParams params);

    public abstract void accumulateEvents(String featureName, final Instant fromCursor, final Instant toCursor);

    public Instant getDefaultFromPeriod(String feature, Instant to) {

        Instant result = to;
        if(feature.endsWith(DAILY_FEATURE_SUFFIX))
        {
            result = result.minus(getAmountOfDaysInPeriod(defaultFromPeriodDaily), ChronoUnit.DAYS);
        }
        else if(feature.endsWith(HOURLY_FEATURE_SUFFIX))
        {
            result = result.minus(getAmountOfDaysInPeriod(defaultFromPeriodHourly), ChronoUnit.DAYS);
        }
        else
        {
            throw new RuntimeException("Unsupported feature name, should end with hourly/daily");
        }
        result = result.truncatedTo(ChronoUnit.DAYS);

        logger.info("from was not given, running accumulation from={}",result);

        return result;
    }
}
