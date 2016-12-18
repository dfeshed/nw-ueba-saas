package fortscale.collection.jobs.activity;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.repository.state.FeatureBucketState;
import fortscale.aggregation.feature.bucket.repository.state.FeatureBucketStateService;
import fortscale.aggregation.feature.functions.AggGenericNAFeatureValues;
import fortscale.collection.services.UserActivityConfiguration;
import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.User;
import fortscale.domain.core.activities.UserActivityDocument;
import fortscale.domain.core.activities.UserActivityJobState;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fortscale.utils.time.TimestampUtils.convertToSeconds;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Abstract class to provide basic functionality of user activity handlers
 *
 * @author gils
 * 31/05/2016
 */
@Configurable(preConstruction = true)
@Component
public abstract class UserActivityBaseHandler implements UserActivityHandler {
    protected final static String CONTEXT_ID_USERNAME_PREFIX = "normalized_username###";

    protected final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    protected MongoTemplate mongoTemplate;
	@Autowired
	protected UserService userService;
	@Autowired
	protected UsernameService usernameService;

    @Autowired
    FeatureBucketStateService featureBucketStateService;


	@Value("${user.activity.mongo.batch.size:10000}")
	private int mongoBatchSize;

    public void calculate(int numOfLastDaysToCalculate) {
        // Getting the last day the aggregation process finished processing
        FeatureBucketState featureBucketState = featureBucketStateService.getFeatureBucketState();
        if (featureBucketState != null  && featureBucketState.getLastSyncedDate() != null){
            Long endTime = featureBucketState.getLastSyncedDate().toEpochMilli();
            long startingTime = TimestampUtils.toStartOfDay(TimeUtils.calculateStartingTime(endTime, numOfLastDaysToCalculate));

            logger.info("Going to handle {} Activity..", getActivityName());
            try {
                logger.info("Start Time = {}  ### End time = {}", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(startingTime)), TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(endTime)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            long fullExecutionStartTime = System.nanoTime();

            UserActivityJobState userActivityJobState = loadAndUpdateJobState(getActivityName(), numOfLastDaysToCalculate);
            final UserActivityConfigurationService userActivityConfigurationService = getUserActivityConfigurationService();
            UserActivityConfiguration userActivityConfiguration = userActivityConfigurationService.getUserActivityConfiguration();
            List<String> dataSources = userActivityConfiguration.getDataSources();
            logger.info("Relevant data sources for activity {} : {}", getActivityName(), dataSources);

            DateTime dateStartTime = new DateTime(TimestampUtils.convertToMilliSeconds(startingTime), DateTimeZone.UTC);
            long firstBucketStartTime = TimestampUtils.convertToSeconds(dateStartTime.withTimeAtStartOfDay().getMillis());
            long firstBucketEndTime = TimestampUtils.convertToSeconds(dateStartTime.withTimeAtStartOfDay().plusDays(1).minusSeconds(1).getMillis());

            DateTime dateEndTime = new DateTime(TimestampUtils.convertToMilliSeconds(endTime), DateTimeZone.UTC);
            long lastBucketEndTime = TimestampUtils.convertToSeconds(dateEndTime.withTimeAtStartOfDay().minusSeconds(1).getMillis());

            Map<String, List<String>> dataSourceToUserIds = fetchAllActiveUserIds(dataSources, firstBucketStartTime);

            int totalNumberOfUsers = 0;
            for (List<String> userIds : dataSourceToUserIds.values()) {
                if (!userIds.isEmpty()) {
                    totalNumberOfUsers += userIds.size();
                }
            }
            if (totalNumberOfUsers > 0) {
                logger.info("Found {} active users for {} activity", getActivityName(), totalNumberOfUsers);
            } else {
                logger.warn("Could not find any users. Abort job");
                return;
            }

            long currBucketStartTime = firstBucketStartTime;
            long currBucketEndTime = firstBucketEndTime;

            while (currBucketEndTime <= lastBucketEndTime) {

                if (userActivityJobState.getCompletedExecutionDays().contains(new Long(currBucketStartTime))) {
                    logger.info("Skipping job process for bucket start time {} (already calculated)", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime)));
                } else {
                    logger.info("Going to fetch from Bucket Start Time = {}  till Bucket End time = {}", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime)), TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketEndTime)));
                    Map<String, Double> additionalActivityHistogram = new HashMap<>();
                    for (String dataSource : dataSources) {

                        List<String> userIds = dataSourceToUserIds.get(dataSource);
                        int numberOfUsers = userIds.size();

                        int actualUserChunkSize = Math.min(mongoBatchSize, numberOfUsers);
                        int numOfHandledUsers = 0;

                        while (numOfHandledUsers < numberOfUsers) {

                            int currentUsersChunkStartIndex = numOfHandledUsers;
                            int currentUsersChunkEndIndex = (numOfHandledUsers + mongoBatchSize <= numberOfUsers) ? numOfHandledUsers + mongoBatchSize : numberOfUsers;

                            List<String> currentUsersChunk = userIds.subList(currentUsersChunkStartIndex, currentUsersChunkEndIndex);

                            Map<String, UserActivityDocument> userActivityMap = new HashMap<>(currentUsersChunk.size());

                            logger.info("Handling chunk of {} users ({} to {})", actualUserChunkSize, currentUsersChunkStartIndex, currentUsersChunkEndIndex);

                            String collectionName = userActivityConfiguration.getCollection(dataSource);
                            List<FeatureBucket> bucketsForDataSource = retrieveBuckets(currBucketStartTime, currBucketEndTime, currentUsersChunk, dataSource, collectionName);

                            if (!bucketsForDataSource.isEmpty()) {
                                long updateUsersHistogramInMemoryStartTime = System.nanoTime();
                                updateUsersHistogram(userActivityMap, bucketsForDataSource, currBucketStartTime, currBucketEndTime, dataSources);
                                long updateUsersHistogramInMemoryElapsedTime = System.nanoTime() - updateUsersHistogramInMemoryStartTime;
                                logger.info("Update users histogram in memory for {} users took {} seconds", currentUsersChunk.size(), durationInSecondsWithPrecision(updateUsersHistogramInMemoryElapsedTime));
                            }

                            Map<String, Double> histograms = updateAdditionalActivitySpecificHistograms(userActivityMap);
                            histograms.forEach((k, v) -> additionalActivityHistogram.merge(k, v, (v1, v2) -> v1 + v2));

                            Collection<UserActivityDocument> userActivityToInsertDocument = userActivityMap.values();

                            insertUsersActivityToDB(userActivityToInsertDocument);

                            numOfHandledUsers += mongoBatchSize;
                        }
                    }
                    updateAdditionalActivitySpecificDocumentInDatabase(dataSources, currBucketStartTime, currBucketEndTime,
                            additionalActivityHistogram);
                }

                logger.info("Updating job's state..");
                updateJobState(userActivityJobState, currBucketStartTime);
                logger.info("Job state was updated successfully");

                DateTime currDateTime = new DateTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime), DateTimeZone.UTC);
                currBucketStartTime = TimestampUtils.convertToSeconds(currDateTime.plusDays(1).getMillis());
                currBucketEndTime = TimestampUtils.convertToSeconds(currDateTime.plusDays(2).minus(1).getMillis());
            }
            long fullExecutionElapsedTime = System.nanoTime() - fullExecutionStartTime;
            logger.info("Full execution of Location Activity ({} active users) took {} seconds", totalNumberOfUsers,
                    durationInSecondsWithPrecision(fullExecutionElapsedTime));
        }else{
            logger.warn("No aggregation data to process");
        }

        postCalculation();
    }

    protected Map<String, Double> updateAdditionalActivitySpecificHistograms(Map<String, UserActivityDocument> userActivityMap) {
        return Collections.emptyMap();
    }

    protected UserActivityJobState loadAndUpdateJobState(String activityName, int numOfLastDaysToCalculate) {
        Criteria criteria = Criteria.where(UserActivityJobState.ACTIVITY_NAME_FIELD).is(activityName);

        Query query = new Query(criteria);
        UserActivityJobState userActivityJobState = mongoTemplate.findOne(query, UserActivityJobState.class);

        if (userActivityJobState == null) {
            userActivityJobState = new UserActivityJobState();
            userActivityJobState.setActivityName(activityName);
            userActivityJobState.setLastRun(System.currentTimeMillis());

            mongoTemplate.save(userActivityJobState, UserActivityJobState.COLLECTION_NAME);
        }
        else {
            Update update = new Update();
            update.set(UserActivityJobState.LAST_RUN_FIELD, System.currentTimeMillis());

            mongoTemplate.upsert(query, update, UserActivityJobState.class);

            TreeSet<Long> completedExecutionDays = userActivityJobState.getCompletedExecutionDays();

            long endTime = System.currentTimeMillis();
            long startingTime = TimestampUtils.convertToSeconds(TimestampUtils.toStartOfDay(TimeUtils.
					calculateStartingTime(endTime, numOfLastDaysToCalculate)));

            completedExecutionDays.removeIf(a -> (a < startingTime));

            removeRelevantDocuments(startingTime);
        }

        return userActivityJobState;
    }

	//This fetches all the active users from a certain point in time.
	//There is an underlying assumption that we will always search for usernames with the CONTEXT_ID_USERNAME_PREFIX
    protected Map<String, List<String>> fetchAllActiveUserIds(List<String> dataSources, long startTime) {
        Map<String, List<String>> dataSourceToUserIds = new HashMap<>();
		DateTime startDate = new DateTime(TimestampUtils.convertToMilliSeconds(startTime));
		List<User> users = userService.getUsersActiveSinceIncludingUsernameAndLogLastActivity(startDate);

        for (String dataSource : dataSources) {
			//we don't have vpn_session in the log last activity
			final String logDataSource = dataSource.toLowerCase().equals("vpn_session") ? "vpn" : dataSource;

			List<String> userIds = users.stream().filter(user -> user.getLogLastActivity(logDataSource) != null &&
					user.getLogLastActivity(logDataSource).isAfter(startDate)).map(user -> CONTEXT_ID_USERNAME_PREFIX +
					user.getUsername()).collect(Collectors.toList());
			dataSourceToUserIds.put(dataSource, userIds);
        }
        return dataSourceToUserIds;
    }

	protected List<FeatureBucket> retrieveBuckets(
			long startTime, long endTime, List<String> usersChunk, String dataSource, String collectionName) {

		Query query = new Query();
		query.addCriteria(where(FeatureBucket.CONTEXT_ID_FIELD).in(usersChunk));
		query.addCriteria(where(FeatureBucket.START_TIME_FIELD)
				.gte(convertToSeconds(startTime))
				.lt(convertToSeconds(endTime)));
		query.fields().include(FeatureBucket.CONTEXT_ID_FIELD);

		try {
			final List<String> relevantFields = getRelevantFields(dataSource);
			relevantFields.forEach(field -> query.fields().include(field));
		} catch (IllegalArgumentException e) {
			logger.error("{}. Skipping query data source {}", e.getLocalizedMessage(), dataSource);
			return Collections.emptyList();
		}

		long queryStartTime = System.nanoTime();
		List<FeatureBucket> featureBuckets = mongoTemplate.find(query, FeatureBucket.class, collectionName);
		long queryElapsedTime = System.nanoTime() - queryStartTime;
		logger.info("Query {} aggregation collection for {} users took {} seconds",
				dataSource, usersChunk.size(), durationInSecondsWithPrecision(queryElapsedTime));
		return featureBuckets;
	}



    protected double durationInSecondsWithPrecision(long updateUsersHistogramInMemoryElapsedTime) {
        return (double) TimeUnit.MILLISECONDS.convert(updateUsersHistogramInMemoryElapsedTime, TimeUnit.NANOSECONDS) / 1000;
    }

    protected void removeRelevantDocuments(Object startingTime) {
        final List<Class> relatedDocuments = getRelevantDocumentClasses();
        for (Class relatedDocumentClass : relatedDocuments) {
            Query query = new Query();
			query.addCriteria(Criteria.where(UserActivityDocument.START_TIME_FIELD_NAME).lt(startingTime));
            mongoTemplate.remove(query, relatedDocumentClass);
        }
    }

    protected void insertUsersActivityToDB(Collection<UserActivityDocument> userActivityToInsert) {
        long insertStartTime = System.nanoTime();
        mongoTemplate.insert(userActivityToInsert, getCollectionName());
        long elapsedInsertTime = System.nanoTime() - insertStartTime;
        logger.info("Insertion of {} users to Mongo took {} seconds", userActivityToInsert.size(), durationInSecondsWithPrecision(elapsedInsertTime));
    }

    protected void updateJobState(UserActivityJobState userActivityJobState, Long startOfDay) {
        Query query = new Query();
        query.addCriteria(Criteria.where(UserActivityJobState.ID_FIELD).is(userActivityJobState.getId()));

        userActivityJobState.getCompletedExecutionDays().add(startOfDay);

        Update update = new Update();
        update.set(UserActivityJobState.COMPLETED_EXECUTION_DAYS_FIELD, userActivityJobState.getCompletedExecutionDays());

        mongoTemplate.upsert(query, update, UserActivityJobState.class);
    }

    protected void updateUsersHistogram(Map<String, UserActivityDocument> userActivityMap, List<FeatureBucket> featureBucketsForDataSource,
                                        Long startTime, Long endTime, List<String> dataSources) {
        for (FeatureBucket featureBucket : featureBucketsForDataSource) {
            String contextId = featureBucket.getContextId().substring(CONTEXT_ID_USERNAME_PREFIX.length());
			contextId = usernameService.getUserId(contextId, null);
			if (contextId == null) {
				logger.error("Cannot create instance of {} - userid not found",getActivityName());
				continue;
			}
            if (!userActivityMap.containsKey(contextId)) {
                try {
                    Class<? extends UserActivityDocument> activityDocumentClass = UserActivityType.valueOf(getActivityName()).getDocumentClass();

                    UserActivityDocument userActivityDocument =  activityDocumentClass.newInstance();

                    userActivityDocument.setEntityId(contextId);
                    userActivityDocument.setStartTime(startTime);
                    userActivityDocument.setEndTime(endTime);
                    userActivityDocument.setDataSources(dataSources);

                    userActivityMap.put(contextId, userActivityDocument);
                } catch (Exception e){
                    logger.error("Cannot create instance of {}",getActivityName());
                }
            }

            updateActivitySpecificHistogram(userActivityMap, featureBucket, contextId);
        }
    }

    private void updateActivitySpecificHistogram(Map<String, UserActivityDocument> userActivityMap, FeatureBucket featureBucket, String contextId) {
        UserActivityDocument userActivityDocument = userActivityMap.get(contextId);
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
     * @return
     */
    Function<Double, Double> valueReducer() {
        return (newValue) -> newValue;
    };

    public void postCalculation(){
        // Runs all needs to be done after the calculation finished
    }

    protected abstract GenericHistogram convertFeatureToHistogram(Object objectToConvert, String histogramFeatureName);

    protected abstract String getCollectionName();

	protected boolean countNAValues() {
		return true;
	}

    /**
     * returns the relevant fields from the aggregated features map
     * @return the list of the fields from which the job should get its information (from the aggregated features map)
     */
    protected abstract List<String> getRelevantAggregatedFeaturesFieldsNames();


    protected String getActivityName(){
        return getActivity().name().toUpperCase();
    }

    public abstract UserActivityType getActivity();

    protected abstract UserActivityConfigurationService getUserActivityConfigurationService();

    protected abstract List<String> getRelevantFields(String dataSource) throws IllegalArgumentException;

    protected abstract List<Class> getRelevantDocumentClasses();

    /**
     * Most classes need to do nothing here. Use this if your class has an additional document (like locations-activity has the additional organization-document)
     * @param dataSources the data sources of the document
     * @param currBucketStartTime the start time of the document
     * @param currBucketEndTime the end time of the document
     * @param additionalActivityHistogram the histogram of the document
     */
    protected abstract void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram);

}
