package presidio.ade.domain.store.accumulator;

import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;


public interface AggregationEventsAccumulationDataReader {

    /**
     *
     * @param aggregatedFeatureName feature name
     * @param startTime start time
     * @param endTime end time
     * @return accumulated context ids by TimeRange
     */
    Set<String> findDistinctAcmContextsByTimeRange(
            String aggregatedFeatureName, Date startTime, Date endTime);

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
}
