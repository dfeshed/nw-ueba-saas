package fortscale.accumulator.accumulator;


import fortscale.accumulator.entityEvent.EntityEventAccumulator;
import fortscale.accumulator.entityEvent.event.AccumulatedEntityEvent;

import java.time.Instant;

/**
 * Accumulators duty is to read data from a specific source of data,
 * and to accumulate the source entities into aggregated instances of those entities.
 *
 * should be used when detailed source-data's granularity is not needed (only part of the source fields are needed)
 * and/or when only an aggregated view is needed.
 *
 * i.e. {@link EntityEventAccumulator}:
 * accumulates a group of {@link fortscale.domain.core.EntityEvent} into one {@link AccumulatedEntityEvent} by {@link AccumulationParams}
 *
 * Created by barak_schuster on 10/6/16.
 */
public interface Accumulator {

    /**
     * aggregates source collection into new accumulated-collection by params
     * @param params contains filtering data, source collection name etc...
     */
    void run(AccumulationParams params);

    /**
     * searches for the last accumulated event start time
     * used to determine what was the last accumulation execution time range
     * @param featureName
     * @return returns null if no events found, last start time otherwise
     */
    Instant getLastAccumulatedEventStartTime(String featureName);

    /**
     * searches for the last source event start time
     * used to determine till when to accumulate
     * @param featureName
     * @return returns null if no events found, last start time otherwise
     */
    Instant getLastSourceEventStartTime(String featureName);
}
