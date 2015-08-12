package fortscale.aggregation.feature.services.historicaldata;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
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

    static final String BUCKET_CONF_DAILY_STRATEGY_SUFFIX = "daily";

    private static Logger logger = Logger.getLogger(SupportingInformationBasePopulator.class);

    protected String contextType;
    protected String dataEntity;
    protected String featureName;

    @Autowired
    protected BucketConfigurationService bucketConfigurationService;

    @Autowired
    protected FeatureBucketsStore featureBucketsStore;

    public SupportingInformationBasePopulator(String contextType, String dataEntity, String featureName) {
        this.contextType = contextType;
        this.dataEntity = dataEntity;
        this.featureName = featureName;
    }

    abstract Map<HistogramKey, Double> createSupportingInformationHistogram(List<FeatureBucket> featureBuckets);

    abstract HistogramKey createAnomalyHistogramKey(String anomalyValue);

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

        List<FeatureBucket> featureBuckets = featureBucketsStore.getFeatureBucketsByContextAndTimeRange(bucketConfig, contextType, contextValue, supportingInformationStartTime, evidenceEndTime, true);

        logger.info("Found {} relevant featureName buckets", featureBuckets.size());

        return featureBuckets;
    }

    protected void validateHistogramDataConsistency(Map<HistogramKey, Double> histogramMap, HistogramKey anomalyHistogramKey) {
        if (!histogramMap.containsKey(anomalyHistogramKey)) {
            throw new IllegalStateException("Could not find anomaly histogram key in histogram map. Anomaly key = " + anomalyHistogramKey + " # Histogram map = " + histogramMap);
        }
    }

    protected String getBucketConfigurationName(String contextType, String dataEntity) {
        return String.format("%s_%s_%s", contextType, dataEntity, BUCKET_CONF_DAILY_STRATEGY_SUFFIX);
    }

    abstract String getNormalizedFeatureName(String featureName);
}
