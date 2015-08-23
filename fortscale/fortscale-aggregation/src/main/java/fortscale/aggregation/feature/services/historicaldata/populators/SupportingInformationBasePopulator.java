package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationData;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationException;
import fortscale.domain.core.Evidence;
import fortscale.domain.histogram.HistogramKey;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Basic implementation for supporting information populator class
 *
 * @author gils
 * Date: 05/08/2015
 */
public abstract class SupportingInformationBasePopulator implements SupportingInformationDataPopulator{

    private static Logger logger = Logger.getLogger(SupportingInformationBasePopulator.class);

    protected String contextType;
    protected String dataEntity;
    protected String featureName;

    static final String BUCKET_CONF_DAILY_STRATEGY_SUFFIX = "daily";

    @Autowired
    protected BucketConfigurationService bucketConfigurationService;

    @Autowired
    protected FeatureBucketsStore featureBucketsStore;

    public SupportingInformationBasePopulator(String contextType, String dataEntity, String featureName) {
        this.contextType = contextType;
        this.dataEntity = dataEntity;
        this.featureName = featureName;
    }

    /*
     * Basic flow of the populator:
     * 1. Fetch relevant buckets
     * 2. Create the histogram
     * 3. Create the anomaly histogram key
     * 4. Validate data consistency (histogram + anomaly)
     */
    @Override
    public SupportingInformationData createSupportingInformationData(Evidence evidence, String contextValue, long evidenceEndTime, int timePeriodInDays) {

        List<FeatureBucket> featureBuckets = fetchRelevantFeatureBuckets(contextValue, evidenceEndTime, timePeriodInDays);

        if (featureBuckets.isEmpty()) {
            throw new SupportingInformationException("Could not find any relevant bucket for histogram creation");
        }

        Map<HistogramKey, Double> histogramMap = createSupportingInformationHistogram(featureBuckets);

        if (isAnomalyIndicationRequired(evidence)) {
            HistogramKey anomalyHistogramKey = createAnomalyHistogramKey(evidence, featureName);

            validateHistogramDataConsistency(histogramMap, anomalyHistogramKey);

            return new SupportingInformationData(histogramMap, anomalyHistogramKey);
        }
        else {
            return new SupportingInformationData(histogramMap);
        }
    }

    /**
     * Abstract method to the histogram creation functionality
     */
    abstract Map<HistogramKey, Double> createSupportingInformationHistogram(List<FeatureBucket> featureBuckets);

    abstract HistogramKey createAnomalyHistogramKey(Evidence evidence, String featureName);

    /*
     * Fetch the relevant feature buckets based on the context value and time values.
     */
    protected List<FeatureBucket> fetchRelevantFeatureBuckets(String contextValue, long evidenceEndTime, int timePeriodInDays) {
        String bucketConfigName = getBucketConfigurationName(contextType, dataEntity);

        FeatureBucketConf bucketConfig = bucketConfigurationService.getBucketConf(bucketConfigName);

        if (bucketConfig != null) {
            logger.info("Using bucket configuration {} with strategy {}", bucketConfig.getName(), bucketConfig.getStrategyName());
        }
        else {
            throw new SupportingInformationException("Could not find Bucket configuration with name " + bucketConfigName);
        }

        Long supportingInformationStartTime = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);

        List<FeatureBucket> featureBuckets = featureBucketsStore.getFeatureBucketsByContextAndTimeRange(bucketConfig, getNormalizedContextType(contextType), contextValue, supportingInformationStartTime, evidenceEndTime, true);

        logger.info("Found {} relevant featureName buckets", featureBuckets.size());

        return featureBuckets;
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

    protected void validateHistogramDataConsistency(Map<HistogramKey, Double> histogramMap, HistogramKey anomalyHistogramKey) {
        if (!histogramMap.containsKey(anomalyHistogramKey)) {
            throw new IllegalStateException("Could not find anomaly histogram key in histogram map. Anomaly key = " + anomalyHistogramKey + " # Histogram map = " + histogramMap);
        }
    }

    protected String getBucketConfigurationName(String contextType, String dataEntity) {
        return String.format("%s_%s_%s", contextType, dataEntity, BUCKET_CONF_DAILY_STRATEGY_SUFFIX);
    }

    protected abstract boolean isAnomalyIndicationRequired(Evidence evidence);

    abstract String getNormalizedContextType(String contextType);

    abstract String getNormalizedFeatureName(String featureName);
}
