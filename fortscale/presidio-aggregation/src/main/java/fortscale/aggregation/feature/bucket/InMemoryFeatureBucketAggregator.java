package fortscale.aggregation.feature.bucket;

import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import presidio.ade.domain.record.AdeRecord;

import java.util.Collections;
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

    private List<FeatureBucket> aggregate(PageIterator<? extends AdeRecord> pageIterator, List<FeatureBucketConf> featureBucketConfs, FeatureBucketStrategyData strategyData){
        FeatureBucketsAggregatorInMemory featureBucketsInMemory = new FeatureBucketsAggregatorInMemory();

        FeatureBucketAggregator featureBucketAggregator = new FeatureBucketAggregator(featureBucketsInMemory,recordReaderFactoryService, featureBucketAggregatorMetricsContainer);
        while(pageIterator.hasNext()){
            List<? extends AdeRecord> adeRecordList = pageIterator.next();
            featureBucketAggregator.aggregate(adeRecordList, featureBucketConfs, strategyData);
        }

        return featureBucketsInMemory.getAllFeatureBuckets();
    }

    public List<FeatureBucket> aggregate(PageIterator<? extends AdeRecord> pageIterator, String adeEventType, String contextFieldName,
                                         List<String> excludeContextFieldNames, FeatureBucketStrategyData strategyData){
        List<FeatureBucketConf> featureBucketConfs =
                bucketConfigurationService.getRelatedBucketConfs(adeEventType, strategyData.getStrategyName(), contextFieldName, excludeContextFieldNames);

        return aggregate(pageIterator, featureBucketConfs, strategyData);
    }

    public List<FeatureBucket> aggregate(PageIterator<? extends AdeRecord> pageIterator, String featureBucketConfName, FeatureBucketStrategyData strategyData){
        FeatureBucketConf featureBucketConf =                 bucketConfigurationService.getBucketConf(featureBucketConfName);

        if(featureBucketConf != null) {
            return aggregate(pageIterator, Collections.singletonList(featureBucketConf), strategyData);
        } else{
            return Collections.emptyList();
        }
    }
}
