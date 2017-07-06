package fortscale.ml.processes.shell.scoring.aggregation;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.scored.enriched_scored.AdeScoredEnrichedRecord;

import java.util.List;

/**
 * Created by barak_schuster on 6/12/17.
 */
public interface ScoreAggregationsBucketService {
    public void updateBuckets(List<AdeScoredEnrichedRecord> adeRecordList, List<String> contextFieldNames, FeatureBucketStrategyData strategyData);

    List<FeatureBucket> closeBuckets();
}
