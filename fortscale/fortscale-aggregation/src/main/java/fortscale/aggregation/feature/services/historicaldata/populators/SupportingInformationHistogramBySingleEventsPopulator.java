package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationException;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.domain.core.Evidence;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.services.dataentity.QueryFieldFunction;
import fortscale.services.dataqueries.querydto.*;
import fortscale.services.dataqueries.querygenerators.DataQueryRunner;
import fortscale.services.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
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
        //retrieve last day data from Impala:
//        FeatureBucket lastDayBucket = createLastDayBucket(bucketConfig, normalizedContextType, contextValue, bucketEndTime, evidenceEndTime, dataEntity, featureBuckets.get(featureBuckets.size()-1));
//        if (lastDayBucket != null) {
//            featureBuckets.add(lastDayBucket);
//        }

        logger.debug("Found {} relevant featureName buckets:", featureBuckets.size());
        logger.debug(featureBuckets.toString());

        return featureBuckets;
    }


    /*private FeatureBucket createLastDayBucket(FeatureBucketConf bucketConfig, String normalizedContextType, String contextValue, long startTime, long endTime, String dataEntity, FeatureBucket featureBucket_in) {
        //TODO: remove last day and create a new FeatureBucket from Impala
        //example of query for destination_machine in authentication_score table:
        //select normalized_username, normalized_dst_machine, count( normalized_dst_machine)  from authenticationscores where normalized_username = 'mac83a@somebigcompany.com' group by normalized_dst_machine,normalized_username;

        FeatureBucket featureBucket = new FeatureBucket();

        String QueryFieldsAsCSV = normalizedContextType.concat(",").concat(featureName);

		//add conditions
		List<Term> termsMap = new ArrayList<>();
		Term contextTerm = getTheContextTerm(normalizedContextType,contextValue);
        if (contextTerm != null) {
            termsMap.add(contextTerm);
        }
        Term dateRangeTerm = getDateRangeTerm(startTime, endTime);
        if (dateRangeTerm != null) {
            termsMap.add(dateRangeTerm);
        }

        DataQueryDTO dataQueryObject = dataQueryHelper.createDataQuery(dataEntity, QueryFieldsAsCSV, termsMap, null, -1);

        //Create the Group By clause
        List<DataQueryField > groupByFields = dataQueryHelper.createGrouByClause(QueryFieldsAsCSV,dataEntity);
        dataQueryHelper.setGroupByClause(groupByFields,dataQueryObject);

        // Create the count(*) field:
        HashMap<String, String> countParams = new HashMap<>();
        countParams.put("all", "true");
        DataQueryField countField = dataQueryHelper.createCountFunc ("countField",countParams);
        dataQueryHelper.setFuncFieldToQuery(countField,dataQueryObject);

        List<Map<String, Object>> queryList;
        try {
            DataQueryRunner dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryObject);
            // Generates query
            String query = dataQueryRunner.generateQuery(dataQueryObject);
            logger.info("Running the query: {}", query);
            // execute Query
            queryList = dataQueryRunner.executeQuery(query);
            logger.info(queryList.toString());
            return buildFeatureBucket(queryList, bucketConfig, normalizedContextType, contextValue, startTime, endTime, dataEntity, featureBucket_in);
        } catch (InvalidQueryException e) {
            logger.error(e.getMessage());
        }
        return null;
    }*/

   /* private FeatureBucket buildFeatureBucket(List<Map<String, Object>> queryList, FeatureBucketConf bucketConfig, String normalizedContextType, String contextValue, long startTime, long endTime, String dataEntity, FeatureBucket featureBucket_in) {
        FeatureBucket featureBucket = new FeatureBucket();
        for (Map<String, Object> event : queryList){

        }
        featureBucket.setFeatureBucketConfName(bucketConfig.getName());
        featureBucket.setStartTime(startTime);
        featureBucket.setEndTime(endTime);
        featureBucket.setStrategyId(bucketConfigurationService.getBucketConf(bucketConfig.getName()).getStrategyName() + "_" + TimestampUtils.convertToSeconds(startTime));
        featureBucket.setBucketId(featureBucket.getStrategyId() + "_" + normalizedContextType + "_" + contextValue);
        return featureBucket;
    }*/

    private Term getDateRangeTerm(long startTime, long endTime) {
        return dataQueryHelper.createDateRangeTerm(dataEntity, TimestampUtils.convertToSeconds(startTime), TimestampUtils.convertToSeconds(endTime));
    }

    private Term getTheContextTerm(String normalizedContextType, String contextValue)
	{
		Term term = null;
//		switch (normalizedContextType)
//		{
//			case "normalized_username" :
				term = dataQueryHelper.createUserTerm(dataEntity, contextValue);
//				break;
//			default:
//				break;
//
//		}
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
}
