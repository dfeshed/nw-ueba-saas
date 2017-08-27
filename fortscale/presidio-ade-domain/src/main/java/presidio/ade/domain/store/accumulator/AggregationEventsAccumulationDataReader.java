package presidio.ade.domain.store.accumulator;

import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;


public interface AggregationEventsAccumulationDataReader {

    /**
     *
     * @param aggregatedFeatureName feature name
     * @param timeRange
     * @return accumulated context ids by TimeRange
     */
    Set<String> findDistinctAcmContextsByTimeRange(
            String aggregatedFeatureName, TimeRange timeRange);

    /**
     *
     * @param aggregatedFeatureName feature name
     * @param contextId context id
     * @param startTime start date
     * @param endTime end date
     * @return accumulated records by contextId and start timeRange
     */
    List<AccumulatedAggregationFeatureRecord> findAccumulatedEventsByContextIdAndStartTimeRange(
            String aggregatedFeatureName,
            String contextId,
            Instant startTime,
            Instant endTime);

    /**
     * @see this#findAccumulatedEventsByContextIdAndStartTimeRange(String, String, Instant, Instant) - this method is just syntactic sugar
     */
    default List<AccumulatedAggregationFeatureRecord> findAccumulatedEventsByContextIdAndStartTimeRange(
            String aggregatedFeatureName,
            String contextId,
            TimeRange timeRange)
    {
        Instant start = timeRange.getStart();
        Instant end = timeRange.getEnd();
        return findAccumulatedEventsByContextIdAndStartTimeRange(aggregatedFeatureName, contextId, start, end);
    }
}
