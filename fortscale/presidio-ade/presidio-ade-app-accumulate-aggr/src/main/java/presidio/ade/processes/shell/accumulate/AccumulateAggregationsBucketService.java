package presidio.ade.processes.shell.accumulate;

import fortscale.accumulator.aggregation.Accumulator;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.pagination.PageIterator;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.util.List;

/**
 * Created by maria_dorohin on 7/30/17.
 */
public interface AccumulateAggregationsBucketService {

    /**
     * Aggregate and accumulate records.
     *
     * For each page in pageIterator, create ordered map of startDate to records.
     * Aggregate each map entry.
     * If it is not last entry or if it is last page in iterator: close aggregation buckets, create aggregation records and accumulate records.
     * @param pageIterator
     * @param contextTypes
     * @param featureBucketDuration
     * @param accumulatorService
     */
    void aggregateAndAccumulate(PageIterator<EnrichedRecord> pageIterator, List<String> contextTypes, FixedDurationStrategy featureBucketDuration, Accumulator accumulatorService);

}
