package fortscale.aggregation.feature.bucket;

import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import presidio.ade.domain.record.AdeRecord;

import java.util.List;

/**
 * Created by YaronDL on 6/29/2017.
 */
public class InMemoryFeatureBucketAggregator {

    private FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;

    private BucketConfigurationService bucketConfigurationService;
    private RecordReaderFactoryService recordReaderFactoryService;

    public InMemoryFeatureBucketAggregator(BucketConfigurationService bucketConfigurationService, RecordReaderFactoryService recordReaderFactoryService, FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer) {
        this.bucketConfigurationService = bucketConfigurationService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.featureBucketAggregatorMetricsContainer = featureBucketAggregatorMetricsContainer;
    }


    public List<FeatureBucket> aggregate(PageIterator<? extends AdeRecord> pageIterator, String adeEventType, String contextFieldName, FeatureBucketStrategyData strategyData){
        FeatureBucketsAggregatorInMemory featureBucketsInMemory = new FeatureBucketsAggregatorInMemory();

        FeatureBucketAggregator featureBucketAggregator = new FeatureBucketAggregator(featureBucketsInMemory,recordReaderFactoryService, featureBucketAggregatorMetricsContainer);
        List<FeatureBucketConf> featureBucketConfs = bucketConfigurationService.getRelatedBucketConfs(adeEventType, strategyData.getStrategyName(), contextFieldName);
        while(pageIterator.hasNext()){
            List<? extends AdeRecord> adeRecordList = pageIterator.next();
            featureBucketAggregator.aggregate(adeRecordList, featureBucketConfs, strategyData);
        }

        return featureBucketsInMemory.getAllFeatureBuckets();
    }


}
