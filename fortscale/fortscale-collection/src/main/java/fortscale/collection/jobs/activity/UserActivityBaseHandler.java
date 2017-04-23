package fortscale.collection.jobs.activity;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.state.FeatureBucketStateService;
import fortscale.aggregation.feature.functions.AggGenericNAFeatureValues;
import fortscale.collection.services.UserActivityConfiguration;
import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.activities.UserActivityDocument;
import fortscale.domain.core.activities.UserActivityJobState;
import fortscale.domain.core.dao.UserActivityFeaturesExtractionsRepositoryUtil;
import fortscale.services.UserService;
import fortscale.services.impl.UsernameService;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Abstract class to provide basic functionality of user activity handlers
 */
@Configurable(preConstruction = true)
@Component
public abstract class UserActivityBaseHandler implements UserActivityHandler {
    protected final static String CONTEXT_ID_USERNAME_PREFIX = "normalized_username###";

    public static final String AGGREGATED_FEATURES_PREFIX = "aggregatedFeatures";

    protected final Logger logger = Logger.getLogger(this.getClass());


    @Autowired
    protected UserService userService;
    @Autowired
    protected UsernameService usernameService;

    @Autowired
    FeatureBucketStateService featureBucketStateService;

    @Autowired
    protected UserActivityFeaturesExtractionsRepositoryUtil userActivityFeaturesExtractionsRepositoryUtil;


    @Value("${user.activity.mongo.batch.size:10000}")
    private int mongoBatchSize;


    /**
     * Calculate activity for all users, per time bucket and data source
     *
     * @param numOfLastDaysToCalculate the num of last days to calculate
     */
    public void calculate(int numOfLastDaysToCalculate) {
        // Getting the last day the aggregation process finished processing
        Instant lastClosedDailyBucketDate = featureBucketStateService.getFeatureBucketState().getLastSyncedEventDate();
        logger.debug("Starting user activity calculation, the last event date is {}", lastClosedDailyBucketDate);

        if (lastClosedDailyBucketDate != null) {
            // Get the date of last closed daily bucket
            Long endTime = lastClosedDailyBucketDate.toEpochMilli();

            long startingTime = TimestampUtils.toStartOfDay(TimeUtils.calculateStartingTime(endTime, numOfLastDaysToCalculate));

            logger.info("Going to handle {} Activity..", getActivityName());
            try {
                logger.debug("Start Time = {}  ### End time = {}", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(startingTime)), TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(endTime)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            long fullExecutionStartTime = System.nanoTime();

            UserActivityJobState userActivityJobState = userActivityFeaturesExtractionsRepositoryUtil.
                    loadAndUpdateJobState(getActivityName(), numOfLastDaysToCalculate, getRelevantDocumentClasses());
            List<String> dataSources = getDataSources();
            logger.debug("Relevant data sources for activity {} : {}", getActivityName(), dataSources);

            DateTime dateStartTime = new DateTime(TimestampUtils.convertToMilliSeconds(startingTime), DateTimeZone.UTC);
            long firstBucketStartTime = TimestampUtils.convertToSeconds(dateStartTime.withTimeAtStartOfDay().getMillis());
            long firstBucketEndTime = TimestampUtils.convertToSeconds(dateStartTime.withTimeAtStartOfDay().plusDays(1).minusSeconds(1).getMillis());

            DateTime dateEndTime = new DateTime(TimestampUtils.convertToMilliSeconds(endTime), DateTimeZone.UTC);
            long lastBucketEndTime = TimestampUtils.convertToSeconds(dateEndTime.withTimeAtStartOfDay().minusSeconds(1).getMillis());

            final Map<String, String> dataSourceToCollection = getDataSourceToCollection();
            Map<String, List<String>> dataSourceToUserIds = fetchUserIdPerDatasource(dataSources, firstBucketStartTime, lastBucketEndTime, dataSourceToCollection);

            int totalNumberOfUsers = 0;
            for (List<String> userIds : dataSourceToUserIds.values()) {
                if (!userIds.isEmpty()) {
                    totalNumberOfUsers += userIds.size();
                }
            }
            if (totalNumberOfUsers > 0) {
                logger.debug("Found {} active users for {} activity", getActivityName(), totalNumberOfUsers);
            } else {
                logger.warn("Could not find any users. Abort job");
                return;
            }

            long currBucketStartTime = firstBucketStartTime;
            long currBucketEndTime = firstBucketEndTime;

            while (currBucketEndTime <= lastBucketEndTime) {

                if (userActivityJobState.getCompletedExecutionDays().contains(currBucketStartTime)) {
                    logger.debug("Skipping job process for bucket start time {} (already calculated)", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime)));
                } else {
                    calculateTimeBucket(dataSourceToUserIds, currBucketStartTime, currBucketEndTime);
                }

                logger.debug("Updating job's state..");
                userActivityFeaturesExtractionsRepositoryUtil.updateJobState(userActivityJobState, currBucketStartTime);
                logger.debug("Job state was updated successfully");

                DateTime currDateTime = new DateTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime), DateTimeZone.UTC);
                currBucketStartTime = TimestampUtils.convertToSeconds(currDateTime.plusDays(1).getMillis());
                currBucketEndTime = TimestampUtils.convertToSeconds(currDateTime.plusDays(2).minus(1).getMillis());
            }
            long fullExecutionElapsedTime = System.nanoTime() - fullExecutionStartTime;
            logger.info("Full execution of Activity {} ({} active users) took {} seconds", getActivityName(), totalNumberOfUsers,
                    durationInSecondsWithPrecision(fullExecutionElapsedTime));
        } else {
            logger.warn("No aggregation data to process");
        }

        postCalculation();
    }

    /**
     * @param dataSourceToUserIds
     * @param currBucketStartTime
     * @param currBucketEndTime
     */
    private void calculateTimeBucket(
            Map<String, List<String>> dataSourceToUserIds,
            long currBucketStartTime,
            long currBucketEndTime) {
        logger.debug("Going to fetch from Bucket Start Time = {}  till Bucket End time = {}", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime)), TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketEndTime)));
        Map<String, Double> additionalActivityHistogram = new HashMap<>();

        List<String> dataSources = getDataSources();

        for (String dataSource : dataSources) {
            calculateDataSourceInTimeBucket(dataSourceToUserIds.get(dataSource), currBucketStartTime, currBucketEndTime, additionalActivityHistogram, dataSource);
        }
        updateAdditionalActivitySpecificDocumentInDatabase(dataSources, currBucketStartTime, currBucketEndTime,
                additionalActivityHistogram);
    }

    private void calculateDataSourceInTimeBucket(List<String> userIds,
                                                 long currBucketStartTime,
                                                 long currBucketEndTime,
                                                 Map<String, Double> additionalActivityHistogram,
                                                 String dataSource) {


        int numberOfUsers = userIds.size();
        String collectionName = getDataSourceToCollection().get(dataSource);
        int actualUserChunkSize = Math.min(mongoBatchSize, numberOfUsers);
        int numOfHandledUsers = 0;

        //Iterate bulk of users and calculate the the activities of those users per single data source in between currBucketStartTime end currBucketEndTime
        while (numOfHandledUsers < numberOfUsers) {

            int currentUsersChunkStartIndex = numOfHandledUsers;
            int currentUsersChunkEndIndex = (numOfHandledUsers + mongoBatchSize <= numberOfUsers) ? numOfHandledUsers + mongoBatchSize : numberOfUsers;

            List<String> currentUsersChunk = userIds.subList(currentUsersChunkStartIndex, currentUsersChunkEndIndex);

            Map<String, UserActivityDocument> userActivityMap = new HashMap<>(currentUsersChunk.size());

            logger.debug("Handling chunk of {} users ({} to {})", actualUserChunkSize, currentUsersChunkStartIndex, currentUsersChunkEndIndex);

            List<FeatureBucket> bucketsForDataSource = retrieveBuckets(currBucketStartTime, currBucketEndTime, currentUsersChunk, dataSource, collectionName);

            if (!bucketsForDataSource.isEmpty()) {
                long updateUsersHistogramInMemoryStartTime = System.nanoTime();
                updateUsersHistogram(userActivityMap, bucketsForDataSource, currBucketStartTime, currBucketEndTime, dataSource);
                long updateUsersHistogramInMemoryElapsedTime = System.nanoTime() - updateUsersHistogramInMemoryStartTime;
                logger.debug("Update users histogram in memory for {} users took {} seconds", currentUsersChunk.size(), durationInSecondsWithPrecision(updateUsersHistogramInMemoryElapsedTime));
            }

            //If this activity have single unique histogram (I.E. global countries for all the system in advance to countries per user)
            //We need to process the additional computation, and store it on additionalActivityHistogram,
            //So the next loop could also read and update additionalActivityHistogram.
            Map<String, Double> histograms = updateAdditionalActivitySpecificHistograms(userActivityMap);
            histograms.forEach((k, v) -> additionalActivityHistogram.merge(k, v, (v1, v2) -> v1 + v2));

            Collection<UserActivityDocument> userActivityToInsertDocument = userActivityMap.values();

            userActivityFeaturesExtractionsRepositoryUtil.insertUsersActivityToDB(userActivityToInsertDocument, getCollectionName());

            numOfHandledUsers += mongoBatchSize;
        }
    }

    private List<String> getDataSources() {
        final UserActivityConfigurationService userActivityConfigurationService = getUserActivityConfigurationService();
        UserActivityConfiguration userActivityConfiguration = userActivityConfigurationService.getUserActivityConfiguration();
        return userActivityConfiguration.getDataSources();
    }

    private Map<String, String> getDataSourceToCollection() {
        final UserActivityConfigurationService userActivityConfigurationService = getUserActivityConfigurationService();
        UserActivityConfiguration userActivityConfiguration = userActivityConfigurationService.getUserActivityConfiguration();
        return userActivityConfiguration.getDataSourceToCollection();
    }


    protected Map<String, Double> updateAdditionalActivitySpecificHistograms(Map<String, UserActivityDocument> userActivityMap) {
        return Collections.emptyMap();
    }


    //This fetches all the active users from a certain point in time.
    //There is an underlying assumption that we will always search for usernames with the CONTEXT_ID_USERNAME_PREFIX
    protected Map<String, List<String>> fetchUserIdPerDatasource(List<String> dataSources, long startTime, long endTime, Map<String, String> dataSourceToCollection) {
        Map<String, List<String>> userIds = new HashMap<>();

        for (String dataSource : dataSources) {
            String collectionName = dataSourceToCollection.get(dataSource);
            List<String> contextIdList = userActivityFeaturesExtractionsRepositoryUtil.
                    getContextIdList(startTime, endTime, FeatureBucket.START_TIME_FIELD, FeatureBucket.END_TIME_FIELD, collectionName);


            userIds.put(dataSource, contextIdList);
        }

        return userIds;
    }


    protected List<FeatureBucket> retrieveBuckets(
            long startTime, long endTime, List<String> usersChunk, String dataSource, String collectionName) {

        List<String> relevantFields = null;
        try {
            relevantFields = getRelevantFields(dataSource);

        } catch (IllegalArgumentException e) {
            logger.error("{}. Skipping query data source {}", e.getLocalizedMessage(), dataSource);
            return Collections.emptyList();
        }
        long queryStartTime = System.nanoTime();

        List<FeatureBucket> featureBuckets = userActivityFeaturesExtractionsRepositoryUtil.getFeatureBuckets(startTime, endTime, usersChunk, collectionName, relevantFields,
                FeatureBucket.CONTEXT_ID_FIELD, FeatureBucket.START_TIME_FIELD, FeatureBucket.class);
        long queryElapsedTime = System.nanoTime() - queryStartTime;
        logger.debug("Query {} aggregation collection for {} users took {} seconds",
                dataSource, usersChunk.size(), durationInSecondsWithPrecision(queryElapsedTime));
        return featureBuckets;
    }


    protected double durationInSecondsWithPrecision(long updateUsersHistogramInMemoryElapsedTime) {
        return (double) TimeUnit.MILLISECONDS.convert(updateUsersHistogramInMemoryElapsedTime, TimeUnit.NANOSECONDS) / 1000;
    }


    protected void updateUsersHistogram(Map<String, UserActivityDocument> userActivityMap, List<FeatureBucket> featureBucketsForDataSource,
                                        Long startTime, Long endTime, String dataSource) {
        for (FeatureBucket featureBucket : featureBucketsForDataSource) {
            String contextId = featureBucket.getContextId().substring(CONTEXT_ID_USERNAME_PREFIX.length());
            contextId = usernameService.getUserId(contextId, null);
            if (contextId == null) {
                logger.error("Cannot create instance of {} - userid not found", getActivityName());
                continue;
            }
            if (!userActivityMap.containsKey(contextId)) {
                try {
                    Class<? extends UserActivityDocument> activityDocumentClass = UserActivityType.valueOf(getActivityName()).getDocumentClass();

                    UserActivityDocument userActivityDocument = activityDocumentClass.newInstance();

                    userActivityDocument.setEntityId(contextId);
                    userActivityDocument.setStartTime(startTime);
                    userActivityDocument.setEndTime(endTime);

                    userActivityMap.put(contextId, userActivityDocument);
                } catch (Exception e) {
                    logger.error("Cannot create instance of {}", getActivityName());
                }
            }

            updateActivitySpecificHistogram(userActivityMap, featureBucket, contextId, dataSource);
        }
    }

    private void updateActivitySpecificHistogram(Map<String, UserActivityDocument> userActivityMap, FeatureBucket featureBucket, String contextId, String dataSource) {
        UserActivityDocument userActivityDocument = userActivityMap.get(contextId);
        userActivityDocument.addDataSourceIfAbsent(dataSource);
        Map<String, Double> histogramOfUser = userActivityDocument.getHistogram();

        final Map<String, Feature> aggregatedFeatures = featureBucket.getAggregatedFeatures();
        final List<String> histogramFeatureNames = getRelevantAggregatedFeaturesFieldsNames();
        for (String histogramFeatureName : histogramFeatureNames) {
            Feature featureValue = aggregatedFeatures.get(histogramFeatureName);
            if (featureValue == null) {
                continue;
            }
            final GenericHistogram featureAsHistogram = convertFeatureToHistogram(featureValue, histogramFeatureName);
            Map<String, Double> bucketHistogram = featureAsHistogram.getHistogramMap();
            for (Map.Entry<String, Double> entry : bucketHistogram.entrySet()) {
                if (entry.getKey().equals(AggGenericNAFeatureValues.NOT_AVAILABLE) && !countNAValues()) {
                    continue;
                }
                double oldValue = histogramOfUser.get(entry.getKey()) != null ? histogramOfUser.get(entry.getKey()) : 0;
                double newValue = entry.getValue();
                histogramOfUser.put(entry.getKey(), oldValue + valueReducer().apply(newValue));
            }
        }
    }

    /**
     * Function that change the value of the bucket. The default implementation is not changing it.
     * You need to override to change it.
     *
     * @return
     */
    Function<Double, Double> valueReducer() {
        return (newValue) -> newValue;
    }

    public void postCalculation() {
        // Runs all needs to be done after the calculation finished
    }

    protected GenericHistogram convertFeatureToHistogram(Object objectToConvert, String histogramFeatureName) {
        if (objectToConvert instanceof Feature && ((Feature) objectToConvert).getValue() instanceof GenericHistogram) {
            return (GenericHistogram) ((Feature) objectToConvert).getValue();
        } else {
            final String errorMessage = String.format("Can't convert %s object of class %s", objectToConvert, objectToConvert.getClass());
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    protected abstract String getCollectionName();

    protected boolean countNAValues() {
        return true;
    }

    /**
     * returns the relevant fields from the aggregated features map
     *
     * @return the list of the fields from which the job should get its information (from the aggregated features map)
     */
    protected abstract List<String> getRelevantAggregatedFeaturesFieldsNames();


    protected String getActivityName() {
        return getActivity().name().toUpperCase();
    }

    public abstract UserActivityType getActivity();

    protected abstract UserActivityConfigurationService getUserActivityConfigurationService();

    protected abstract List<String> getRelevantFields(String dataSource) throws IllegalArgumentException;

    protected abstract List<Class> getRelevantDocumentClasses();

    /**
     * Most classes need to do nothing here. Use this if your class has an additional document (like locations-activity has the additional organization-document)
     *
     * @param dataSources                 the data sources of the document
     * @param currBucketStartTime         the start time of the document
     * @param currBucketEndTime           the end time of the document
     * @param additionalActivityHistogram the histogram of the document
     */
    protected abstract void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram);

    protected Double convertAggregatedFeatureValueToDouble(Object featureValue) {
        if (featureValue instanceof Double) {
            return (Double) featureValue;
        } else if (featureValue instanceof Float) {
            logger.debug("Expected featureValue Double got Float");
            return ((Float) featureValue).doubleValue();
        } else if (featureValue instanceof Long) {
            logger.debug("Expected featureValue Double got Long");
            return ((Long) featureValue).doubleValue();
        } else if (featureValue instanceof Integer) {
            logger.debug("Expected featureValue Double got Integer");
            return ((Integer) featureValue).doubleValue();
        }
        throw new RuntimeException("Cannot convert featureValue to Double");
    }

}
