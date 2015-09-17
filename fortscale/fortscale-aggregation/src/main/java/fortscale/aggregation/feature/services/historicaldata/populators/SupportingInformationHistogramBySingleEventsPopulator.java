package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationException;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.domain.core.Evidence;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Abstract class with generic implementation for supporting information histogram populator of single events
 *
 * @author gils
 * Date: 05/08/2015
 */
public abstract class SupportingInformationHistogramBySingleEventsPopulator extends SupportingInformationBaseHistogramPopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationHistogramBySingleEventsPopulator.class);

    private static final String FIXED_DURATION_DAILY_STRATEGY = "fixed_duration_daily";
    private static final String FIXED_DURATION_HOURLY_STRATEGY = "fixed_duration_hourly";

    private static final String BUCKET_CONF_DAILY_STRATEGY_SUFFIX = "daily";

    @Autowired
    protected BucketConfigurationService bucketConfigurationService;

    @Autowired
    protected FeatureBucketsStore featureBucketsStore;

    public SupportingInformationHistogramBySingleEventsPopulator(String contextType, String dataEntity, String featureName) {
        super(contextType, dataEntity, featureName);
    }

    /*
     * Basic flow of the populator:
     * 1. Fetch relevant buckets
     * 2. Create the histogram
     * 3. Create the anomaly histogram key
     * 4. Validate data consistency (histogram + anomaly)
     */
    @Override
    public SupportingInformationGenericData<Double> createSupportingInformationData(Evidence evidence, String contextValue, long evidenceEndTime, Integer timePeriodInDays) {

        Map<SupportingInformationKey, Double> histogramMap = createSupportingInformationHistogram(contextValue, evidenceEndTime, timePeriodInDays);

        if (isAnomalyIndicationRequired(evidence)) {
            SupportingInformationKey anomalySupportingInformationKey = createAnomalyHistogramKey(evidence, featureName);

            validateHistogramDataConsistency(histogramMap, anomalySupportingInformationKey);

            return new SupportingInformationGenericData<>(histogramMap, anomalySupportingInformationKey);
        }
        else {
            return new SupportingInformationGenericData<>(histogramMap);
        }
    }

    /*
     * Fetch the relevant feature buckets based on the context value and time values.
     */
    protected List<FeatureBucket> fetchRelevantFeatureBuckets(String contextValue, long evidenceEndTime, int timePeriodInDays) {
        String bucketConfigName = getBucketConfigurationName(contextType, dataEntity);

        FeatureBucketConf bucketConfig = bucketConfigurationService.getBucketConf(bucketConfigName);

        if (bucketConfig != null) {
            logger.info("Using bucket configuration {}", bucketConfig.getName());
        }
        else {
            throw new SupportingInformationException("Could not find Bucket configuration with name " + bucketConfigName);
        }

        String bucketStrategyName = bucketConfig.getStrategyName();

        logger.info("Bucket strategy name = {}", bucketStrategyName);

        long bucketEndTime = calculateBucketEndTime(evidenceEndTime, bucketStrategyName);

        Long bucketStartTime = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);

        List<FeatureBucket> featureBuckets = featureBucketsStore.getFeatureBucketsByContextAndTimeRange(bucketConfig, getNormalizedContextType(contextType), contextValue, bucketStartTime, bucketEndTime);

        logger.info("Found {} relevant featureName buckets:", featureBuckets.size());
        logger.info(featureBuckets.toString());

        return featureBuckets;
    }

    /*
     * Bucket end time should be the evidence end time + 1 timeframe (e.g. 1 day / 1 hour) so the last bucket that the anomaly is within it will be contained in the results.
     * This logic should exist as long as we don't take the "last bucket" events directly from Impala (bug FV-8306)
     */
    private long calculateBucketEndTime(long evidenceEndTime, String strategyName) {
        long bucketEndTime;

        // TODO need to use the feature bucket strategy service, currently it's in the streaming project
        if (FIXED_DURATION_DAILY_STRATEGY.equals(strategyName)) {
            bucketEndTime = evidenceEndTime + TimeUnit.DAYS.toMillis(1);
        }
        else if (FIXED_DURATION_HOURLY_STRATEGY.equals(strategyName)) {
            bucketEndTime = evidenceEndTime + TimeUnit.HOURS.toMillis(1);
        }
        else {
            throw new SupportingInformationException("Could not find bucket strategy with name " + strategyName);
        }

        return bucketEndTime;
    }

    protected String extractAnomalyValue(Evidence evidence, String featureName) {

        boolean contextAndFeatureMatch = isContextAndFeatureMatch(evidence, featureName);

        if (contextAndFeatureMatch) { // in this case we want the inverse chart
            return evidence.getEntityName();
        }
        else {
            return evidence.getAnomalyValue();
        }
    }

    private boolean isContextAndFeatureMatch(Evidence evidence, String feature) {
        return feature.equalsIgnoreCase(evidence.getEntityTypeFieldName());
    }

    protected void validateHistogramDataConsistency(Map<SupportingInformationKey, Double> histogramMap, SupportingInformationKey anomalySupportingInformationKey) {
        if (!histogramMap.containsKey(anomalySupportingInformationKey)) {
            throw new SupportingInformationException("Could not find anomaly histogram key in histogram map. Anomaly key = " + anomalySupportingInformationKey + " # Histogram map = " + histogramMap);
        }
    }

    protected String getBucketConfigurationName(String contextType, String dataEntity) {
        return String.format("%s_%s_%s", contextType, dataEntity, BUCKET_CONF_DAILY_STRATEGY_SUFFIX);
    }
}
