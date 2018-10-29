package fortscale.aggregation.feature.bucket;

import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.functions.AggrFeatureFuncService;
import fortscale.aggregation.feature.functions.IAggrFeatureFunctionsService;
import fortscale.common.feature.Feature;
import fortscale.utils.logging.Logger;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReader;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FeatureBucketAggregator {
    private static final Logger logger = Logger.getLogger(FeatureBucketAggregator.class);

    private IAggrFeatureFunctionsService aggrFeatureFunctionsService;
    private RecordReaderFactoryService recordReaderFactoryService;
    private FeatureBucketsAggregatorStore featureBucketsAggregatorStore;
    private FeatureBucketAggregatorMetricsContainer metricsContainer;

    public FeatureBucketAggregator(FeatureBucketsAggregatorStore featureBucketsAggregatorStore, RecordReaderFactoryService recordReaderFactoryService, FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer) {
        this.featureBucketsAggregatorStore = featureBucketsAggregatorStore;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.aggrFeatureFunctionsService = new AggrFeatureFuncService();
        this.metricsContainer = featureBucketAggregatorMetricsContainer;
    }

    /**
     * Update feature buckets with adeRecords
     *
     * @param adeRecords            list of adeRecords
     * @param featureBucketConfs    The feature bucket configurations that need to be build / updated by the ade records
     * @param strategyData          strategy data of ade records
     */
    public void aggregate(List<? extends AdeRecord> adeRecords, List<FeatureBucketConf> featureBucketConfs, FeatureBucketStrategyData strategyData) {

        for (AdeRecord adeRecord : adeRecords) {
            aggregate(adeRecord,featureBucketConfs,strategyData);
        }
    }

    /**
     * Update feature buckets with adeRecords
     *
     * @param adeRecord             ADE Record
     * @param featureBucketConfs    The feature bucket configurations that need to be build / updated by the ade record
     * @param strategyData          strategy data of ade record
     */
    public void aggregate(AdeRecord adeRecord, List<FeatureBucketConf> featureBucketConfs, FeatureBucketStrategyData strategyData) {
        AdeRecordReader adeRecordReader = (AdeRecordReader) recordReaderFactoryService.getRecordReader(adeRecord);

        for (FeatureBucketConf featureBucketConf : featureBucketConfs) {
            try {
                String strategyId = strategyData.getStrategyId();
                String bucketId = FeatureBucketUtils.buildBucketId(adeRecordReader, featureBucketConf, strategyId);
                String featureBucketConfName = featureBucketConf.getName();
                Instant logicalStartTime = strategyData.getTimeRange().getStart();

                if (bucketId == null) {
                    metricsContainer.incNullFeatureBucketId(featureBucketConfName, logicalStartTime);
                    continue;
                }

                FeatureBucket featureBucket = this.featureBucketsAggregatorStore.getFeatureBucket(bucketId);

                if (featureBucket == null) {
                    featureBucket = createNewFeatureBucket(adeRecordReader, featureBucketConf, strategyData, bucketId);
                }

                metricsContainer.incFeatureBucketUpdates(featureBucketConfName,logicalStartTime);
                updateFeatureBucket(adeRecordReader, featureBucket, featureBucketConf);
                this.featureBucketsAggregatorStore.storeFeatureBucket(featureBucket);

            } catch (Exception e) {
                logger.error("Got an exception while updating buckets with new event", e);
            }
        }
    }

    /**
     * Update the feature bucket.
     *
     * @param adeRecordReader
     * @param featureBucket
     * @param featureBucketConf
     * @throws Exception
     */
    private void updateFeatureBucket(AdeRecordReader adeRecordReader, FeatureBucket featureBucket, FeatureBucketConf featureBucketConf) throws Exception {
        Map<String, Feature> featuresMap = adeRecordReader.getAllFeatures(featureBucketConf.getAllFeatureNames());
        Map<String, Feature> aggrFeaturesMap = aggrFeatureFunctionsService.updateAggrFeatures(adeRecordReader, featureBucketConf.getAggrFeatureConfs(), featureBucket.getAggregatedFeatures(), featuresMap);
        featureBucket.setAggregatedFeatures(aggrFeaturesMap);
    }

    /**
     * Create feature bucket
     * @param adeRecordReader
     * @param featureBucketConf
     * @param strategyData
     * @param bucketId
     * @return FeatureBucket
     */
    private FeatureBucket createNewFeatureBucket(AdeRecordReader adeRecordReader, FeatureBucketConf featureBucketConf, FeatureBucketStrategyData strategyData, String bucketId) {

        FeatureBucket ret = new FeatureBucket();
        String featureBucketConfName = featureBucketConf.getName();
        ret.setFeatureBucketConfName(featureBucketConfName);
        ret.setBucketId(bucketId);
        ret.setStrategyId(strategyData.getStrategyId());
        List<String> contextFieldNames = featureBucketConf.getContextFieldNames();
        ret.setContextFieldNames(contextFieldNames);
        TimeRange timeRange = strategyData.getTimeRange();
        Instant logicalStartTime = timeRange.getStart();
        ret.setStartTime(logicalStartTime);
        ret.setEndTime(timeRange.getEnd());
        ret.setCreatedAt(new Date());

        metricsContainer.incFeatureBuckets(featureBucketConfName,logicalStartTime);

        for (String contextFieldName : contextFieldNames) {
            String contextValue = adeRecordReader.getContext(contextFieldName);
            ret.addToContextFieldNameToValueMap(contextFieldName, contextValue);
        }

        String contextId = FeatureBucketUtils.buildContextId(ret.getContextFieldNameToValueMap());
        ret.setContextId(contextId);

        return ret;
    }
}
