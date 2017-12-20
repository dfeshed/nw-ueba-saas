package presidio.ade.processes.shell.scoring.aggregation;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.FeatureBucketsAggregatorInMemory;
import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;

import java.util.List;

/**
 * Created by barak_schuster on 6/14/17.
 */
public class ScoreAggregationsBucketServiceImpl implements ScoreAggregationsBucketService{
    private FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;
    private BucketConfigurationService bucketConfigurationService;
    private RecordReaderFactoryService recordReaderFactoryService;
    private FeatureBucketAggregator featureBucketAggregator;
    private FeatureBucketsAggregatorInMemory featureBucketsInMemory;

    public ScoreAggregationsBucketServiceImpl(BucketConfigurationService bucketConfigurationService, RecordReaderFactoryService recordReaderFactoryService, FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer) {
        this.bucketConfigurationService = bucketConfigurationService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.featureBucketAggregatorMetricsContainer = featureBucketAggregatorMetricsContainer;
        initFeatureBucketAggregator();
    }

    private void initFeatureBucketAggregator(){
        this.featureBucketsInMemory = new FeatureBucketsAggregatorInMemory();
        this.featureBucketAggregator = new FeatureBucketAggregator(featureBucketsInMemory,bucketConfigurationService,recordReaderFactoryService, featureBucketAggregatorMetricsContainer);
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
