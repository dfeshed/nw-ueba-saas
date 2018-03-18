package fortscale.aggregation.feature.services.historicaldata.populators;

import com.fasterxml.jackson.databind.ObjectMapper;
//import fortscale.aggregation.feature.bucket.BucketConfigurationService;
//import fortscale.aggregation.feature.bucket.FeatureBucket;
//import fortscale.aggregation.feature.bucket.FeatureBucketConf;
//import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationException;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
//import fortscale.common.dataqueries.querydto.*;
//import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
//import fortscale.common.dataqueries.querygenerators.DataQueryRunnerFactory;
//import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.domain.core.Evidence;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.utils.CustomedFilter;
import fortscale.utils.FilteringPropertiesConfigurationHandler;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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

//    @Autowired
//    protected BucketConfigurationService bucketConfigurationService;
//
//    @Autowired
//    protected FeatureBucketsStore featureBucketsStore;
//
//	@Autowired
//	DataQueryHelper dataQueryHelper;
//
//    @Autowired
//    protected DataQueryRunnerFactory dataQueryRunnerFactory;

    @Autowired
    private FilteringPropertiesConfigurationHandler eventsFilter;

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

        Map<SupportingInformationKey, Double> histogramMap = createSupportingInformationHistogram(contextValue, evidenceEndTime, timePeriodInDays,evidence);

        if (isAnomalyIndicationRequired(evidence)) {
            SupportingInformationKey anomalySupportingInformationKey = createAnomalyHistogramKey(evidence, featureName);

            validateHistogramDataConsistency(histogramMap, anomalySupportingInformationKey);

            return new SupportingInformationGenericData<>(histogramMap, anomalySupportingInformationKey);
        }
        else {
            return new SupportingInformationGenericData<>(histogramMap);
        }
    }

    public SupportingInformationGenericData<Double> createSupportingInformationData(String contextValue, long endTime, Integer timePeriodInDays, Evidence evidence) {

        Map<SupportingInformationKey, Double> histogramMap = createSupportingInformationHistogram(contextValue, endTime, timePeriodInDays,evidence);
        return new SupportingInformationGenericData<>(histogramMap);
    }

    /*
     * Fetch the relevant feature buckets based on the context value and time values.
     */
    protected List fetchRelevantFeatureBuckets(String contextValue, long evidenceEndTime, int timePeriodInDays) {
//        String bucketConfigName = getBucketConfigurationName(contextType, dataEntity);
//
//        FeatureBucketConf bucketConfig = bucketConfigurationService.getBucketConf(bucketConfigName);
//
//        if (bucketConfig != null) {
//            logger.debug("Using bucket configuration {}", bucketConfig.getName());
//        }
//        else {
//            throw new SupportingInformationException("Could not find Bucket configuration with name " + bucketConfigName);
//        }
//
//        String bucketStrategyName = bucketConfig.getStrategyName();
//
//        logger.debug("Bucket strategy name = {}", bucketStrategyName);
//
//        Long bucketStartTime = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);
//        //remove one day from bucket end. replace it with data from Impala
//        Long bucketEndTime = TimestampUtils.toStartOfDay(evidenceEndTime);
//        String normalizedContextType = getNormalizedContextType(contextType);
//        List<FeatureBucket> featureBuckets = featureBucketsStore.getFeatureBucketsByContextAndTimeRange(bucketConfig, normalizedContextType, contextValue, bucketStartTime, bucketEndTime);
//
//        logger.debug("Found {} relevant featureName buckets:", featureBuckets.size());
//        logger.debug("{}", featureBuckets);
//
//        return featureBuckets;

        return null;
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

    abstract protected Map<SupportingInformationKey, Double> buildLastDayMap(List<Map<String, Object>> queryList);
}