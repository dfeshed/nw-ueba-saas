package fortscale.aggregation.feature.bucket;

import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.functions.AggrFeatureFuncService;
import fortscale.aggregation.feature.functions.IAggrFeatureFunctionsService;
import fortscale.common.feature.Feature;
import fortscale.utils.logging.Logger;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class FeatureBucketAggregator {
    private static final Logger logger = Logger.getLogger(FeatureBucketAggregator.class);

    private BucketConfigurationService bucketConfigurationService;
    private IAggrFeatureFunctionsService aggrFeatureFunctionsService;
    private RecordReaderFactoryService recordReaderFactoryService;
    private FeatureBucketsAggregatorStore featureBucketsAggregatorStore;


    public FeatureBucketAggregator(FeatureBucketsAggregatorStore featureBucketsAggregatorStore, BucketConfigurationService bucketConfigurationService, RecordReaderFactoryService recordReaderFactoryService) {
        this.featureBucketsAggregatorStore = featureBucketsAggregatorStore;
        this.bucketConfigurationService = bucketConfigurationService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.aggrFeatureFunctionsService = new AggrFeatureFuncService();
    }

    /**
     * Update feature buckets with adeRecords
     *
     * @param adeRecords        list of adeRecords
     * @param contextFieldNames names of context field(e.g: normalized_user_name)
     * @param strategyData      strategy data of ade records
     */
    public void aggregate(List<? extends AdeRecord> adeRecords, List<String> contextFieldNames, FeatureBucketStrategyData strategyData) {

        for (AdeRecord adeRecord : adeRecords) {
            aggregate(adeRecord,contextFieldNames,strategyData);
        }
    }

    /**
     * Update feature buckets with adeRecords
     *
     * @param adeRecord        ADE Record
     * @param contextFieldNames names of context field(e.g: normalized_user_name)
     * @param strategyData      strategy data of ade records
     */
    public void aggregate(AdeRecord adeRecord, List<String> contextFieldNames, FeatureBucketStrategyData strategyData) {
        AdeRecordReader adeRecordReader = (AdeRecordReader) recordReaderFactoryService.getRecordReader(adeRecord);
        List<FeatureBucketConf> featureBucketConfs = bucketConfigurationService.getRelatedBucketConfs(adeRecordReader, strategyData.getStrategyName(), contextFieldNames);

        for (FeatureBucketConf featureBucketConf : featureBucketConfs) {
            try {
                String strategyId = strategyData.getStrategyId();
                String bucketId = FeatureBucketUtils.buildBucketId(adeRecordReader, featureBucketConf, strategyId);
                if (bucketId == null) {
                    //todo: metrics.nullBucketIds++;
                    continue;
                }

                FeatureBucket featureBucket = this.featureBucketsAggregatorStore.getFeatureBucket(bucketId);

                if (featureBucket == null) {
                    featureBucket = createNewFeatureBucket(adeRecordReader, featureBucketConf, strategyData, bucketId);
                }

                //todo: metrics.featureBucketUpdates++;
                updateFeatureBucket(adeRecordReader, featureBucket, featureBucketConf);
                this.featureBucketsAggregatorStore.storeFeatureBucket(featureBucket);

            } catch (Exception e) {
                logger.error("Got an exception while updating buckets with new event", e);
                //todo: metrics.exceptionsUpdatingWithNewEvents++;
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
        ret.setFeatureBucketConfName(featureBucketConf.getName());
        ret.setBucketId(bucketId);
        ret.setStrategyId(strategyData.getStrategyId());
        ret.setContextFieldNames(featureBucketConf.getContextFieldNames());
        TimeRange timeRange = strategyData.getTimeRange();
        ret.setStartTime(timeRange.getStart());
        ret.setEndTime(timeRange.getEnd());
        ret.setCreatedAt(new Date());

        for (String contextFieldName : featureBucketConf.getContextFieldNames()) {
            String contextValue = adeRecordReader.getContext(contextFieldName);
            ret.addToContextFieldNameToValueMap(contextFieldName, contextValue);
        }

        String contextId = FeatureBucketUtils.buildContextId(ret.getContextFieldNameToValueMap());
        ret.setContextId(contextId);

        //todo: metrics.buckets++;

        return ret;
    }
}
