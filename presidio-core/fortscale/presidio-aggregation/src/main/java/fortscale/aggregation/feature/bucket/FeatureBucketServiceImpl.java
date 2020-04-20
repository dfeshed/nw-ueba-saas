package fortscale.aggregation.feature.bucket;

import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import presidio.ade.domain.record.AdeRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 */
public class FeatureBucketServiceImpl<T extends AdeRecord> implements FeatureBucketService<T> {
    private BucketConfigurationService bucketConfigurationService;
    private RecordReaderFactoryService recordReaderFactoryService;
    private FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;
    private FeatureBucketsAggregatorInMemory featureBucketsAggregatorInMemory;
    private FeatureBucketAggregator featureBucketAggregator;

    public FeatureBucketServiceImpl(
            BucketConfigurationService bucketConfigurationService,
            RecordReaderFactoryService recordReaderFactoryService,
            FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer) {

        this.bucketConfigurationService = bucketConfigurationService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.featureBucketAggregatorMetricsContainer = featureBucketAggregatorMetricsContainer;
        initFeatureBucketAggregator();
    }

    private void initFeatureBucketAggregator() {
        featureBucketsAggregatorInMemory = new FeatureBucketsAggregatorInMemory();
        featureBucketAggregator = new FeatureBucketAggregator(
                featureBucketsAggregatorInMemory,
                recordReaderFactoryService,
                featureBucketAggregatorMetricsContainer);
    }

    @Override
    public void updateFeatureBuckets(
            List<T> adeRecords,
            String contextFieldName,
            List<String> contextFieldNamesToExclude,
            FeatureBucketStrategyData featureBucketStrategyData) {

        if (adeRecords.isEmpty()) return;
        Map<String, List<FeatureBucketConf>> adeEventTypeToFeatureBucketConfs = new HashMap<>();
        String featureBucketStrategyName = featureBucketStrategyData.getStrategyName();

        for (T adeRecord : adeRecords) {
            String adeEventType = adeRecord.getAdeEventType();
            List<FeatureBucketConf> featureBucketConfs = adeEventTypeToFeatureBucketConfs.computeIfAbsent(adeEventType,
                    key -> bucketConfigurationService.getRelatedBucketConfs(adeEventType, featureBucketStrategyName, contextFieldName, contextFieldNamesToExclude)
            );
            featureBucketAggregator.aggregate(adeRecord, featureBucketConfs, featureBucketStrategyData);
        }
    }

    @Override
    public List<FeatureBucket> closeFeatureBuckets() {
        List<FeatureBucket> closedFeatureBuckets = featureBucketsAggregatorInMemory.getAllFeatureBuckets();
        initFeatureBucketAggregator();
        return closedFeatureBuckets;
    }
}
