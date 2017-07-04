package fortscale.ml.processes.shell;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.FeatureBucketsAggregatorInMemory;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.functions.IAggrFeatureFunctionsService;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.scored.enriched_scored.AdeScoredEnrichedRecord;

import java.util.List;

/**
 * Created by barak_schuster on 6/14/17.
 */
public class ScoreAggregationsBucketServiceImpl implements ScoreAggregationsBucketService{
    private BucketConfigurationService bucketConfigurationService;
    private RecordReaderFactoryService recordReaderFactoryService;
    private FeatureBucketAggregator featureBucketAggregator;
    FeatureBucketsAggregatorInMemory featureBucketsInMemory;

    public ScoreAggregationsBucketServiceImpl(BucketConfigurationService bucketConfigurationService, RecordReaderFactoryService recordReaderFactoryService) {
        this.bucketConfigurationService = bucketConfigurationService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        initFeatureBucketAggregator();
    }

    private void initFeatureBucketAggregator(){
        this.featureBucketsInMemory = new FeatureBucketsAggregatorInMemory();
        this.featureBucketAggregator = new FeatureBucketAggregator(featureBucketsInMemory,bucketConfigurationService,recordReaderFactoryService);
    }

    @Override
    public void updateBuckets(List<AdeScoredEnrichedRecord> adeRecordList, List<String> contextFieldNames, FeatureBucketStrategyData strategyData) {
        featureBucketAggregator.aggregate(adeRecordList, contextFieldNames, strategyData);
    }

    @Override
    public List<FeatureBucket> closeBuckets() {
        List<FeatureBucket> ret = featureBucketsInMemory.getAllFeatureBuckets();
        initFeatureBucketAggregator();

        return ret;
    }
}
