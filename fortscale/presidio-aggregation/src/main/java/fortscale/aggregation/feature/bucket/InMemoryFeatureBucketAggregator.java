package fortscale.aggregation.feature.bucket;

import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.functions.IAggrFeatureFunctionsService;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import presidio.ade.domain.record.AdeRecord;

import java.util.List;

/**
 * Created by YaronDL on 6/29/2017.
 */
public class InMemoryFeatureBucketAggregator {
//    private static final Logger logger = Logger.getLogger(InMemoryFeatureBucketAggregator.class);


    private BucketConfigurationService bucketConfigurationService;
    private RecordReaderFactoryService recordReaderFactoryService;

    public InMemoryFeatureBucketAggregator(BucketConfigurationService bucketConfigurationService, RecordReaderFactoryService recordReaderFactoryService) {
        this.bucketConfigurationService = bucketConfigurationService;
        this.recordReaderFactoryService = recordReaderFactoryService;
    }


    public List<FeatureBucket> aggregate(PageIterator<AdeRecord> pageIterator, List<String> contextFieldNames, FeatureBucketStrategyData strategyData){
        FeatureBucketsAggregatorInMemory featureBucketsInMemory = new FeatureBucketsAggregatorInMemory();

        FeatureBucketAggregator featureBucketAggregator = new FeatureBucketAggregator(featureBucketsInMemory,bucketConfigurationService,recordReaderFactoryService);
        while(pageIterator.hasNext()){
            List<AdeRecord> adeRecordList = pageIterator.next();
            featureBucketAggregator.aggregate(adeRecordList, contextFieldNames, strategyData);
        }

        return featureBucketsInMemory.getAllFeatureBuckets();
    }


}
