package fortscale.aggregation.creator;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;

import java.util.List;

/**
 * Created by barak_schuster on 6/12/17.
 */
public interface AggregationRecordsCreator {
    /**
     * executes relevant aggregation functions by using {@link IAggrFeatureEventFunctionsService} for each feature bucket
     * @param featureBuckets
     * @return aggregated records. empty list for null or empty feature buckets
     */
    List<AdeAggregationRecord> createAggregationRecords(List<FeatureBucket> featureBuckets);
}
