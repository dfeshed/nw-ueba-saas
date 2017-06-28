package fortscale.aggregation.feature.bucket;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.functions.IAggrFeatureFunctionsService;
import fortscale.common.feature.Feature;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReader;
import net.minidev.json.JSONObject;

import java.util.*;

public class FeatureBucketsService {
    private static final Logger logger = Logger.getLogger(FeatureBucketsService.class);
    private static final String BUCKET_ID_BUILDER_SEPARATOR = "###";

    private BucketConfigurationService bucketConfigurationService;
    private IAggrFeatureFunctionsService aggrFeatureFunctionsService;
    private RecordReaderFactoryService recordReaderFactoryService;
    private FeatureBucketsStore featureBucketsStore;
    private Map<String, FeatureBucketsServiceMetrics> dataSourceToMetrics = new HashMap<>();

    @Autowired
    private StatsService statsService;

    private FeatureBucketsServiceMetrics getMetrics(String dataSource) {
        if (!dataSourceToMetrics.containsKey(dataSource)) {
            dataSourceToMetrics.put(dataSource, new FeatureBucketsServiceMetrics(statsService, dataSource));
        }
        return dataSourceToMetrics.get(dataSource);
    }


    public FeatureBucketsService(FeatureBucketsStore featureBucketsStore, BucketConfigurationService bucketConfigurationService, IAggrFeatureFunctionsService aggrFeatureFunctionsService, RecordReaderFactoryService recordReaderFactoryService) {
        this.featureBucketsStore = featureBucketsStore;
        this.bucketConfigurationService = bucketConfigurationService;
        this.aggrFeatureFunctionsService = aggrFeatureFunctionsService;
        this.recordReaderFactoryService = recordReaderFactoryService;
    }

    /**
     * Update feature buckets with adeRecords
     *
     * @param adeRecords        list of adeRecords
     * @param contextFieldNames names of context field(e.g: normalized_user_name)
     * @param strategyData      strategy data of ade records
     */
    public void updateFeatureBucketsWithAdeRecords(List<AdeRecord> adeRecords, List<String> contextFieldNames, FeatureBucketStrategyData strategyData) {

        for (AdeRecord adeRecord : adeRecords) {

            FeatureBucketsServiceMetrics metrics = getMetrics(adeRecord.getDataSource());

            AdeRecordReader adeRecordReader = (AdeRecordReader) recordReaderFactoryService.getRecordReader(adeRecord);
            List<FeatureBucketConf> featureBucketConfs = bucketConfigurationService.getRelatedBucketConfs(adeRecordReader, strategyData.getStrategyName(), contextFieldNames);

            for (FeatureBucketConf featureBucketConf : featureBucketConfs) {
                try {
                    String bucketId = getBucketId(adeRecordReader, featureBucketConf, strategyData.getStrategyId());
                    if (bucketId == null) {
                        metrics.nullBucketIds++;
                        continue;
                    }

                    FeatureBucket featureBucket = this.featureBucketsStore.getFeatureBucket(featureBucketConf, bucketId);

                    if (featureBucket == null) {
                        featureBucket = createNewFeatureBucket(adeRecordReader, featureBucketConf, strategyData, bucketId);
                    }

                    metrics.featureBucketUpdates++;
                    updateFeatureBucket(adeRecordReader, featureBucket, featureBucketConf);
                    storeFeatureBucket(featureBucketConf, featureBucket);

                } catch (Exception e) {
                    logger.error("Got an exception while updating buckets with new event", e);
                    metrics.exceptionsUpdatingWithNewEvents++;
                }
            }

        }
    }

    /**
     * Get and then clear all the feature buckets.
     */
    public List<FeatureBucket> popAllFeatureBuckets() {
        List<FeatureBucket> featureBuckets = this.featureBucketsStore.getAllFeatureBuckets();
        this.featureBucketsStore.clearAll();
        return featureBuckets;
    }

    /**
     * Generate bucket id.
     * The bucket id consist: strategyId , contextFieldNames, contextFieldName value.
     *
     * @param adeRecordReader
     * @param featureBucketConf e.g: normalized_user_name
     * @param strategyId        e.g: fixed_duration_hourly
     * @return bucket id
     */
    private String getBucketId(AdeRecordReader adeRecordReader, FeatureBucketConf featureBucketConf, String strategyId) {
        List<String> sorted = new ArrayList<>(featureBucketConf.getContextFieldNames());
        Collections.sort(sorted);
        StringBuilder builder = new StringBuilder();
        builder.append(strategyId);

        for (String contextFieldName : sorted) {
            builder.append(BUCKET_ID_BUILDER_SEPARATOR);
            String contextValue = adeRecordReader.get(contextFieldName, String.class);

            // Return null as the bucket ID if one of the contexts is missing
            if (StringUtils.isBlank(contextValue)) {
                logger.debug("The {} value is missing.", contextFieldName);
                return null;
            }
            builder.append(contextFieldName).append(BUCKET_ID_BUILDER_SEPARATOR).append(contextValue);
        }

        return builder.toString();
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
        JSONObject jSONObject = getJsonObject(adeRecordReader);
        Map<String, Feature> aggrFeaturesMap = aggrFeatureFunctionsService.updateAggrFeatures(jSONObject, featureBucketConf.getAggrFeatureConfs(), featureBucket.getAggregatedFeatures(), featuresMap);
    }

    /**
     * Create json object of ade record
     *
     * @param adeRecordReader ade record reader
     * @return json object
     */
    private JSONObject getJsonObject(AdeRecordReader adeRecordReader) {
        AdeRecord adeRecord = adeRecordReader.getAdeRecord();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> adeRecordMap = mapper.convertValue(adeRecord, Map.class);
        return new JSONObject(adeRecordMap);
    }

    /**
     * Store featureBucket
     * @param featureBucketConf
     * @param featureBucket
     * @throws Exception
     */
    private void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) throws Exception {
        this.featureBucketsStore.storeFeatureBucket(featureBucketConf, featureBucket);
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
        FeatureBucketsServiceMetrics metrics = getMetrics(adeRecordReader.getDataSource());

        FeatureBucket ret = new FeatureBucket();
        ret.setFeatureBucketConfName(featureBucketConf.getName());
        ret.setBucketId(bucketId);
        ret.setStrategyId(strategyData.getStrategyId());
        ret.setContextFieldNames(featureBucketConf.getContextFieldNames());
        ret.setDataSources(featureBucketConf.getDataSources());
        ret.setStartTime(strategyData.getStartTime());
        ret.setEndTime(strategyData.getEndTime());
        ret.setCreatedAt(new Date());

        for (String contextFieldName : featureBucketConf.getContextFieldNames()) {
            String contextValue = adeRecordReader.get(contextFieldName, String.class);
            ret.addToContextFieldNameToValueMap(contextFieldName, contextValue);
        }

        String contextId = FeatureBucketUtils.buildContextId(ret.getContextFieldNameToValueMap());
        ret.setContextId(contextId);

        metrics.buckets++;

        return ret;
    }


    public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf, String bucketId) {
        return this.featureBucketsStore.getFeatureBucket(featureBucketConf, bucketId);
    }
}
