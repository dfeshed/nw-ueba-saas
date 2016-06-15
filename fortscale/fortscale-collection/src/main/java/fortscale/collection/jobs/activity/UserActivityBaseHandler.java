package fortscale.collection.jobs.activity;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.collection.services.UserActivityConfiguration;
import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.activities.UserActivityDocument;
import fortscale.domain.core.activities.UserActivityJobState;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Abstract class to provide basic functionality of user activity handlers
 *
 * @author gils
 * 31/05/2016
 */
@Configurable(preConstruction = true)
@Component
public abstract class UserActivityBaseHandler implements UserActivityHandler {
    protected static final String CONTEXT_ID_FIELD_NAME = "contextId";
    protected final static String CONTEXT_ID_USERNAME_PREFIX = "normalized_username###";
    protected final static int MONGO_READ_WRITE_BULK_SIZE = 10000;

    protected final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    protected MongoTemplate mongoTemplate;

    public void calculate(int numOfLastDaysToCalculate) {
        long endTime = System.currentTimeMillis();
        long startingTime = TimestampUtils.toStartOfDay(TimeUtils.calculateStartingTime(endTime, numOfLastDaysToCalculate));

        logger.info("Going to handle {} Activity..", getActivityName());
        try {
            logger.info("Start Time = {}  ### End time = {}", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(startingTime)), TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(endTime)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        long fullExecutionStartTime = System.nanoTime();

        UserActivityJobState userActivityJobState = loadAndUpdateJobState(numOfLastDaysToCalculate);
        final UserActivityConfigurationService userActivityConfigurationService = getUserActivityConfigurationService();
        UserActivityConfiguration userActivityConfiguration = userActivityConfigurationService.getUserActivityConfiguration();
        List<String> dataSources = userActivityConfiguration.getDataSources();
        logger.info("Relevant data sources for activity: {}", getActivityName(), dataSources);

        DateTime dateStartTime = new DateTime(TimestampUtils.convertToMilliSeconds(startingTime), DateTimeZone.UTC);
        long firstBucketStartTime = TimestampUtils.convertToSeconds(dateStartTime.withTimeAtStartOfDay().getMillis());
        long firstBucketEndTime = TimestampUtils.convertToSeconds(dateStartTime.withTimeAtStartOfDay().plusDays(1).minusSeconds(1).getMillis());

        DateTime dateEndTime = new DateTime(TimestampUtils.convertToMilliSeconds(endTime), DateTimeZone.UTC);
        long lastBucketEndTime = TimestampUtils.convertToSeconds(dateEndTime.withTimeAtStartOfDay().minusSeconds(1).getMillis());

        final Map<String, String> dataSourceToCollection = userActivityConfiguration.getDataSourceToCollection();
        List<String> userIds = fetchAllActiveUserIds(dataSources, firstBucketStartTime, lastBucketEndTime, dataSourceToCollection);

        if (userIds.isEmpty()) {
            logger.warn("Could not found any user. Abort job");
            return;
        }

        int numberOfUsers = userIds.size();
        logger.info("Found {} active users for {} activity", getActivityName(), numberOfUsers);

        int actualUserChunkSize = Math.min(MONGO_READ_WRITE_BULK_SIZE, numberOfUsers);
        int numOfHandledUsers;

        Map<String, Double> additionalActivityHistogram = new HashMap<>();

        long currBucketStartTime = firstBucketStartTime;
        long currBucketEndTime = firstBucketEndTime;

        while (currBucketEndTime <= lastBucketEndTime) {

            if (userActivityJobState.getCompletedExecutionDays().contains(currBucketStartTime)) {
                logger.info("Skipping job process for bucket start time {} (already calculated)", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime)));
            }
            numOfHandledUsers = 0;

            logger.info("Going to fetch from Bucket Start Time = {}  till Bucket End time = {}", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime)), TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketEndTime)));

            while (numOfHandledUsers < numberOfUsers) {

                int currentUsersChunkStartIndex = numOfHandledUsers;
                int currentUsersChunkEndIndex = (numOfHandledUsers + MONGO_READ_WRITE_BULK_SIZE <= numberOfUsers) ? numOfHandledUsers + MONGO_READ_WRITE_BULK_SIZE : numberOfUsers;

                List<String> currentUsersChunk = userIds.subList(currentUsersChunkStartIndex, currentUsersChunkEndIndex);

                Map<String, UserActivityDocument> userActivityMap = new HashMap<>(currentUsersChunk.size());

                logger.info("Handling chunk of {} users ({} to {})", actualUserChunkSize, currentUsersChunkStartIndex, currentUsersChunkEndIndex);

                for (String dataSource : dataSources) {
                    String collectionName = userActivityConfiguration.getCollection(dataSource);
                    List<FeatureBucket> bucketsForDataSource = retrieveBuckets(currBucketStartTime, currBucketEndTime, currentUsersChunk, dataSource, collectionName);

                    if (!bucketsForDataSource.isEmpty()) {
                        long updateUsersHistogramInMemoryStartTime = System.nanoTime();
                        updateUsersHistogram(userActivityMap, bucketsForDataSource, currBucketStartTime, currBucketEndTime, dataSources);
                        long updateUsersHistogramInMemoryElapsedTime = System.nanoTime() - updateUsersHistogramInMemoryStartTime;
                        logger.info("Update users histogram in memory for {} users took {} seconds", currentUsersChunk.size(), durationInSecondsWithPrecision(updateUsersHistogramInMemoryElapsedTime));
                    }
                }

                additionalActivityHistogram = updateAdditionalActivitySpecificHistograms(userActivityMap);

                Collection<UserActivityDocument> userActivityToInsertDocument = userActivityMap.values();

                insertUsersActivityToDB(userActivityToInsertDocument);

                numOfHandledUsers += MONGO_READ_WRITE_BULK_SIZE;
            }

            logger.info("Updating job's state..");
            updateJobState(currBucketStartTime);
            logger.info("Job state was updated successfully");

            updateAdditionalActivitySpecificDocumentInDatabase(dataSources, currBucketStartTime, currBucketEndTime, additionalActivityHistogram);

            DateTime currDateTime = new DateTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime), DateTimeZone.UTC);
            currBucketStartTime = TimestampUtils.convertToSeconds(currDateTime.plusDays(1).getMillis());
            currBucketEndTime = TimestampUtils.convertToSeconds(currDateTime.plusDays(2).minus(1).getMillis());
        }

        long fullExecutionElapsedTime = System.nanoTime() - fullExecutionStartTime;
        logger.info("Full execution of Location Activity ({} active users) took {} seconds", userIds.size(), durationInSecondsWithPrecision(fullExecutionElapsedTime));

    }

    protected Map<String, Double> updateAdditionalActivitySpecificHistograms(Map<String, UserActivityDocument> userActivityMap) {
        return Collections.emptyMap();
    }

    protected UserActivityJobState loadAndUpdateJobState(int numOfLastDaysToCalculate) {
        Query query = new Query();
        UserActivityJobState userActivityJobState = null;

        userActivityJobState = mongoTemplate.findOne(query, UserActivityJobState.class);


        if (userActivityJobState == null) {
            userActivityJobState = new UserActivityJobState();
            userActivityJobState.setLastRun(System.currentTimeMillis());

            mongoTemplate.save(userActivityJobState, UserActivityJobState.COLLECTION_NAME);
        }
        else {
            Update update = new Update();
            update.set(UserActivityJobState.LAST_RUN_FIELD, System.currentTimeMillis());

            mongoTemplate.upsert(query, update, UserActivityJobState.class);

            TreeSet<Long> completedExecutionDays = userActivityJobState.getCompletedExecutionDays();

            long endTime = System.currentTimeMillis();
            long startingTime = TimeUtils.calculateStartingTime(endTime, numOfLastDaysToCalculate);

            completedExecutionDays.removeIf(a -> (a < startingTime));

            removeRelevantDocuments(startingTime);
        }

        return userActivityJobState;
    }

    protected List<String> fetchAllActiveUserIds(List<String> dataSources, long startTime, long endTime, Map<String, String> dataSourceToCollection) {
        List<String> userIds = new ArrayList<>();

        for (String dataSource : dataSources) {
            Criteria startTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));
            Criteria endTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(endTime));
            Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria));

            String collectionName = dataSourceToCollection.get(dataSource);

            List<String> contextIdList = mongoTemplate.getCollection(collectionName).distinct("contextId", query.getQueryObject());

            userIds.addAll(contextIdList);
        }

        return userIds;
    }

    protected List<FeatureBucket> retrieveBuckets(long startTime, long endTime, List<String> usersChunk, String dataSource, String collectionName) {
        if (mongoTemplate.collectionExists(collectionName)) {
            Criteria usersCriteria = Criteria.where(FeatureBucket.CONTEXT_ID_FIELD).in(usersChunk);
            Criteria startTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));
            Criteria endTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(endTime));
            Query query = new Query(usersCriteria.andOperator(startTimeCriteria.andOperator(endTimeCriteria)));
            query.fields().include(CONTEXT_ID_FIELD_NAME);
            try {
                final List<String> relevantFields = getRelevantFields(dataSource);
                relevantFields.stream().forEach(field -> query.fields().include(field));
            } catch (IllegalArgumentException e) {
                logger.error("{}. Skipping query data source {}", e.getLocalizedMessage(), dataSource);
                return Collections.emptyList();
            }

            long queryStartTime = System.nanoTime();
            List<FeatureBucket> featureBucketsForDataSource = mongoTemplate.find(query, FeatureBucket.class, collectionName);
            long queryElapsedTime = System.nanoTime() - queryStartTime;
            logger.info("Query {} aggregation collection for {} users took {} seconds", dataSource, usersChunk.size(), durationInSecondsWithPrecision(queryElapsedTime));
            return featureBucketsForDataSource;
        }
        else {
            logger.info("Skipping query data source {}, collection {} does not exist", dataSource, collectionName);
            return Collections.emptyList();
        }
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

    protected void updateJobState(Long startOfDay) {
        Query query = new Query();
        UserActivityJobState userActivityJobState = mongoTemplate.findOne(query, UserActivityJobState.class);

        query = new Query();
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

            if (!userActivityMap.containsKey(contextId)) {
                try {
                    Class<? extends UserActivityDocument> activityDocumentClass = UserActivityType.valueOf(getActivityName()).getDocumentClass();

                    UserActivityDocument userActivityDocument =  activityDocumentClass.newInstance();

                    userActivityDocument.setNormalizedUsername(contextId);
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
            final GenericHistogram featureAsHistogram = convertFeatureToHistogram(featureValue, histogramFeatureName);
            Map<String, Double> bucketHistogram = featureAsHistogram.getHistogramMap();
            for (Map.Entry<String, Double> entry : bucketHistogram.entrySet()) {
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



    protected abstract GenericHistogram convertFeatureToHistogram(Object objectToConvert, String histogramFeatureName);

    protected abstract String getCollectionName();

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
