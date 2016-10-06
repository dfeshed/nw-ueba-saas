package fortscale.acumulator;


import fortscale.acumulator.entity.event.AccumulatedEntityEvent;

/**
 * Accumulators duty is to read data from a specific source of data,
 * and to accumulate the source entities into aggregated instances of those entities.
 *
 * should be used when detailed source-data's granularity is not needed (only part of the source fields are needed)
 * and/or when only an aggregated view is needed.
 *
 * i.e. {@link fortscale.acumulator.entity.event.EntityEventAccumulator}:
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

}
