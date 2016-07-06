package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationException;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.common.dataqueries.querydto.*;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
import fortscale.common.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.domain.core.Evidence;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.utils.CustomedFilter;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Autowired
	DataQueryHelper dataQueryHelper;

    @Autowired
    protected DataQueryRunnerFactory dataQueryRunnerFactory;

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

    public SupportingInformationGenericData<Double> createSupportingInformationData(String contextValue, long endTime, Integer timePeriodInDays) {

        Map<SupportingInformationKey, Double> histogramMap = createSupportingInformationHistogram(contextValue, endTime, timePeriodInDays);
        return new SupportingInformationGenericData<>(histogramMap);
    }

    /*
     * Fetch the relevant feature buckets based on the context value and time values.
     */
    protected List<FeatureBucket> fetchRelevantFeatureBuckets(String contextValue, long evidenceEndTime, int timePeriodInDays) {
        String bucketConfigName = getBucketConfigurationName(contextType, dataEntity);

        FeatureBucketConf bucketConfig = bucketConfigurationService.getBucketConf(bucketConfigName);

        if (bucketConfig != null) {
            logger.debug("Using bucket configuration {}", bucketConfig.getName());
        }
        else {
            throw new SupportingInformationException("Could not find Bucket configuration with name " + bucketConfigName);
        }

        String bucketStrategyName = bucketConfig.getStrategyName();

        logger.debug("Bucket strategy name = {}", bucketStrategyName);

        Long bucketStartTime = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);
        //remove one day from bucket end. replace it with data from Impala
        Long bucketEndTime = TimestampUtils.toStartOfDay(evidenceEndTime);
        String normalizedContextType = getNormalizedContextType(contextType);
        List<FeatureBucket> featureBuckets = featureBucketsStore.getFeatureBucketsByContextAndTimeRange(bucketConfig, normalizedContextType, contextValue, bucketStartTime, bucketEndTime);

        logger.debug("Found {} relevant featureName buckets:", featureBuckets.size());
        logger.debug(featureBuckets.toString());

        return featureBuckets;
    }

    /**
     *
     * @param normalizedContextType - The entity normalized field - i.e normalized_username
     * @param contextValue - The value of the normalized context field - i.e test@somebigcomapny.com
     * @param endTime - The time of the anomaly
     * @param dataEntity - The data entity - i.e kerberos_logins
     * @return
     */
    protected Map<SupportingInformationKey, Double> createLastDayBucket(String normalizedContextType, String contextValue, long endTime, String dataEntity) {

        String QueryFieldsAsCSV = normalizedContextType.concat(",").concat(featureName);

        //add conditions
        List<Term> termsMap = new ArrayList<>();
        Term contextTerm = getTheContextTerm(normalizedContextType,contextValue);
        if (contextTerm != null) {
            termsMap.add(contextTerm);
        }
        Term dateRangeTerm = getDateRangeTerm(TimestampUtils.toStartOfDay(endTime), endTime);
        if (dateRangeTerm != null) {
            termsMap.add(dateRangeTerm);
        }


        DataQueryDTO dataQueryObject = dataQueryHelper.createDataQuery(dataEntity, QueryFieldsAsCSV, termsMap, null, -1, DataQueryDTOImpl.class);

        //Remove the alias from the query fields so the context types and values will match the query result (match to field id and not field display name )
        dataQueryHelper.removeAlias(dataQueryObject);

        //Create the Group By clause without alias
        List<DataQueryField> groupByFields = dataQueryHelper.createGrouByClause(QueryFieldsAsCSV, dataEntity, false);
        dataQueryHelper.setGroupByClause(groupByFields,dataQueryObject);

        // Create the count(*) field:
        HashMap<String, String> countParams = new HashMap<>();
        countParams.put("all", "true");
        DataQueryField countField = dataQueryHelper.createCountFunc("countField", countParams);
        dataQueryHelper.setFuncFieldToQuery(countField, dataQueryObject);

        List<Map<String, Object>> queryList;
        try {
            DataQueryRunner dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryObject);
            // Generates query
            String query = dataQueryRunner.generateQuery(dataQueryObject);
            logger.debug("Running the query: {}", query);
            // execute Query
            queryList = dataQueryRunner.executeQuery(query);
            logger.debug(queryList.toString());
            return buildLastDayMap(queryList);
        } catch (InvalidQueryException e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    private Term getDateRangeTerm(long startTime, long endTime) {
        return dataQueryHelper.createDateRangeTerm(dataEntity, TimestampUtils.convertToSeconds(startTime), TimestampUtils.convertToSeconds(endTime));
    }

    private Term getTheContextTerm(String normalizedContextType, String contextValue)
    {
        Term term = null;
        switch (normalizedContextType)
        {
        case "normalized_username" :
            term = dataQueryHelper.createUserTerm(dataEntity, contextValue);
            break;
        default:
            CustomedFilter filter = new CustomedFilter(normalizedContextType,"equals",contextValue);
            term = dataQueryHelper.createCustomTerm(dataEntity, filter);
            break;

        }
        return term;

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
