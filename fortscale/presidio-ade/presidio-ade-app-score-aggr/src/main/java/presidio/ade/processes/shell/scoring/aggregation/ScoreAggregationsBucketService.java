package presidio.ade.processes.shell.scoring.aggregation;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;

import java.util.List;

/**
 * Created by barak_schuster on 6/12/17.
 */
public interface ScoreAggregationsBucketService {
    /**
     * update bucket with ade records
     * @param adeRecordList
     * @param contextFieldNames
     * @param strategyData
     */
    void updateBuckets(List<AdeScoredEnrichedRecord> adeRecordList, List<String> contextFieldNames, FeatureBucketStrategyData strategyData);

    /**
     * once buckets are closed they should not be updated for that specific session
     * @return
     */
    List<FeatureBucket> closeBuckets();
}
