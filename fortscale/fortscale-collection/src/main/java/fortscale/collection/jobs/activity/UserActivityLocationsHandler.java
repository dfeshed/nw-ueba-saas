package fortscale.collection.jobs.activity;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.collection.services.UserActivityLocationConfigurationServiceImpl;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.OrganizationActivityLocation;
import fortscale.domain.core.UserActivityJobState;
import fortscale.domain.core.UserActivityLocation;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * User activity locations handler implementation
 *
 * @author gils
 * 24/05/2016
 */
@Configurable(preConstruction = true)
@Component
public class UserActivityLocationsHandler extends UserActivityBaseHandler {

    private static final int NUM_OF_DAYS = 90;
    private static Logger logger = Logger.getLogger(UserActivityLocationsHandler.class);

    private static final String ACTIVITY_NAME = "locations";

    private static final String AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME = "aggregatedFeatures.country_histogram";
    private static final String COUNTRY_HISTOGRAM_FEATURE_NAME = "country_histogram";

    public void calculate() {
        long endTime = System.currentTimeMillis();
        long startingTime = TimeUtils.calculateStartingTime(endTime, NUM_OF_DAYS);

        logger.info("Going to handle User Locations Activity..");
        logger.info("Start Time = {}  ### End time = {}", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(startingTime)), TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(endTime)));

        long fullExecutionStartTime = System.nanoTime();

        UserActivityJobState userActivityJobState = loadAndUpdateJobState();

        UserActivityLocationConfigurationServiceImpl.UserActivityLocationConfiguration userActivityConfigurationService = userActivityLocationConfigurationService.getUserActivityLocationConfiguration();
        List<String> dataSources = userActivityConfigurationService.getDataSources();
        logger.info("Relevant Data sources for locations activity: {}", dataSources);

        DateTime dateStartTime = new DateTime(TimestampUtils.convertToMilliSeconds(startingTime), DateTimeZone.UTC);
        long firstBucketStartTime = TimestampUtils.convertToSeconds(dateStartTime.withTimeAtStartOfDay().getMillis());
        long firstBucketEndTime = TimestampUtils.convertToSeconds(dateStartTime.withTimeAtStartOfDay().plusDays(1).minusSeconds(1).getMillis());

        DateTime dateEndTime = new DateTime(TimestampUtils.convertToMilliSeconds(endTime), DateTimeZone.UTC);
        long lastBucketEndTime = TimestampUtils.convertToSeconds(dateEndTime.withTimeAtStartOfDay().minusSeconds(1).getMillis());

        final Map<String, String> dataSourceToCollection = userActivityConfigurationService.getDataSourceToCollection();
        List<String> userIds = fetchAllActiveUserIds(dataSources, firstBucketStartTime, lastBucketEndTime, dataSourceToCollection);

        if (userIds.isEmpty()) {
            logger.warn("Could not found any user. Abort job");

            return;
        }

        logger.info("Found {} active users for locations activity", userIds.size());

        int numberOfUsers = userIds.size();
        int numOfHandledUsers;

        Map<String, Integer> organizationActivityLocationHistogram = new HashMap<>();

        long currBucketStartTime = firstBucketStartTime;
        long currBucketEndTime = firstBucketEndTime;

        while (currBucketEndTime <= lastBucketEndTime) {

            if (userActivityJobState.getCompletedExecutionDays().contains(currBucketStartTime)) {
                logger.info("Skipping job process for bucket start time {} (already calculated)", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime)));
            }
            numOfHandledUsers = 0;

            logger.info("Going to fetch from Bucket Start Time = {}  till Bucket End time = {}", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime)), TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketEndTime)));

            while (numOfHandledUsers < numberOfUsers) {
                int actualUserChunkSize = Math.min(MONGO_READ_WRITE_BULK_SIZE, numberOfUsers);
                int startIndex = numOfHandledUsers;
                int endIndex = (numOfHandledUsers + MONGO_READ_WRITE_BULK_SIZE <= numberOfUsers) ? numOfHandledUsers + MONGO_READ_WRITE_BULK_SIZE : numberOfUsers - 1;

                List<String> usersChunk = userIds.subList(startIndex, endIndex);

                Map<String, UserActivityLocation> userActivityLocationMap = new HashMap<>(usersChunk.size());

                logger.info("Handling chunk of {} users ({} to {})", actualUserChunkSize, startIndex, endIndex);

                for (String dataSource : dataSources) {
                    String collectionName = userActivityConfigurationService.getCollection(dataSource);
                    List<FeatureBucket> locationsBucketsForDataSource = retrieveBuckets(currBucketStartTime, currBucketEndTime, usersChunk, dataSource, collectionName);

                    if (!locationsBucketsForDataSource.isEmpty()) {
                        long updateUsersHistogramInMemoryStartTime = System.nanoTime();
                        updateUsersHistogram(userActivityLocationMap, locationsBucketsForDataSource, currBucketStartTime, currBucketEndTime, dataSources);
                        long updateUsersHistogramInMemoryElapsedTime = System.nanoTime() - updateUsersHistogramInMemoryStartTime;
                        logger.info("Update users histogram in memory for {} users took {} seconds", usersChunk.size(), durationInSecondsWithPrecision(updateUsersHistogramInMemoryElapsedTime));
                    }
                }

                long updateOrgHistogramInMemoryStartTime = System.nanoTime();
                updateOrganizationHistogram(organizationActivityLocationHistogram, userActivityLocationMap);
                long updateOrgHistogramInMemoryElapsedTime = System.nanoTime() - updateOrgHistogramInMemoryStartTime;
                logger.info("Update org histogram in memory took {} seconds", durationInSecondsWithPrecision(updateOrgHistogramInMemoryElapsedTime));

                Collection<UserActivityLocation> userActivityLocationsToInsert = userActivityLocationMap.values();

                insertUsersActivityToDB(userActivityLocationsToInsert);

                numOfHandledUsers += MONGO_READ_WRITE_BULK_SIZE;
            }

            logger.info("Updating job's state..");
            updateJobState(currBucketStartTime);
            logger.info("Job state was updated successfully");

            long updateOrgHistogramInMongoStartTime = System.nanoTime();
            updateOrgHistogramInDB(currBucketStartTime, currBucketEndTime, dataSources, organizationActivityLocationHistogram);
            long updateOrgHistogramInMemoryElapsedTime = System.nanoTime() - updateOrgHistogramInMongoStartTime;
            logger.info("Update org histogram in Mongo took {} seconds", TimeUnit.MILLISECONDS.convert(updateOrgHistogramInMemoryElapsedTime, TimeUnit.NANOSECONDS));

            DateTime currDateTime = new DateTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime), DateTimeZone.UTC);
            currBucketStartTime = TimestampUtils.convertToSeconds(currDateTime.plusDays(1).getMillis());
            currBucketEndTime = TimestampUtils.convertToSeconds(currDateTime.plusDays(2).minus(1).getMillis());
        }

        long fullExecutionElapsedTime = System.nanoTime() - fullExecutionStartTime;
        logger.info("Full execution of Location Activity ({} active users) took {} seconds", userIds.size(), durationInSecondsWithPrecision(fullExecutionElapsedTime));
    }

    private double durationInSecondsWithPrecision(long updateUsersHistogramInMemoryElapsedTime) {
        return (double)TimeUnit.MILLISECONDS.convert(updateUsersHistogramInMemoryElapsedTime, TimeUnit.NANOSECONDS) / 1000;
    }

    private UserActivityJobState loadAndUpdateJobState() {
        Query query = new Query();
        UserActivityJobState userActivityJobState = mongoTemplate.findOne(query, UserActivityJobState.class);

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
            long startingTime = TimeUtils.calculateStartingTime(endTime, NUM_OF_DAYS);

            completedExecutionDays.removeIf(a -> (a < startingTime));

            query = new Query();
            query.addCriteria(Criteria.where(UserActivityLocation.START_TIME_FIELD_NAME).lt(startingTime));

            mongoTemplate.remove(query, UserActivityLocation.class);

            query = new Query();
            query.addCriteria(Criteria.where(OrganizationActivityLocation.START_TIME_FIELD_NAME).lt(startingTime));
            mongoTemplate.remove(query, OrganizationActivityLocation.class);
        }

        return userActivityJobState;
    }

    private void insertUsersActivityToDB(Collection<UserActivityLocation> userActivityLocationsToInsert) {
        long insertStartTime = System.nanoTime();
        mongoTemplate.insert(userActivityLocationsToInsert, UserActivityLocation.COLLECTION_NAME);
        long elapsedInsertTime = System.nanoTime() - insertStartTime;
        logger.info("Insert {} users to Mongo took {} seconds", userActivityLocationsToInsert.size(), durationInSecondsWithPrecision(elapsedInsertTime));
    }

    private void updateJobState(Long startOfDay) {
        Query query = new Query();
        UserActivityJobState userActivityJobState = mongoTemplate.findOne(query, UserActivityJobState.class);

        query = new Query();
        query.addCriteria(Criteria.where(UserActivityJobState.ID_FIELD).is(userActivityJobState.getId()));

        userActivityJobState.getCompletedExecutionDays().add(startOfDay);

        Update update = new Update();
        update.set(UserActivityJobState.COMPLETED_EXECUTION_DAYS_FIELD, userActivityJobState.getCompletedExecutionDays());

        mongoTemplate.upsert(query, update, UserActivityJobState.class);
    }

    private List<FeatureBucket> retrieveBuckets(long startTime, long endTime, List<String> usersChunk, String dataSource, String collectionName) {
        if (mongoTemplate.collectionExists(collectionName)) {
            Criteria usersCriteria = Criteria.where(FeatureBucket.CONTEXT_ID_FIELD).in(usersChunk);
            Criteria startTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));
            Criteria endTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(endTime));
            Query query = new Query(usersCriteria.andOperator(startTimeCriteria.andOperator(endTimeCriteria)));
            query.fields().include(CONTEXT_ID_FIELD_NAME);
            query.fields().include(AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME);

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

    private void updateOrgHistogramInDB(long startTime, long endTime, List<String> dataSources, Map<String, Integer> organizationActivityLocationHistogram) {
        OrganizationActivityLocation organizationActivityLocation = new OrganizationActivityLocation();

        organizationActivityLocation.setStartTime(startTime);
        organizationActivityLocation.setEndTime(endTime);
        organizationActivityLocation.setDataSources(dataSources);

        OrganizationActivityLocation.Locations locations = new OrganizationActivityLocation.Locations();
        locations.getCountryHistogram().putAll(organizationActivityLocationHistogram);
        organizationActivityLocation.setLocations(locations);

        mongoTemplate.save(organizationActivityLocation, OrganizationActivityLocation.COLLECTION_NAME);
    }

    private List<String> fetchAllActiveUserIds(List<String> dataSources, long startTime, long endTime, Map<String, String> dataSourceToCollection) {
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

    private void updateOrganizationHistogram(Map<String, Integer> organizationActivityLocationHistogram, Map<String, UserActivityLocation> userActivityLocationMap) {
        for (UserActivityLocation userActivityLocation : userActivityLocationMap.values()) {
            Map<String, Integer> countryHistogram = userActivityLocation.getLocations().getCountryHistogram();

            for (Map.Entry<String, Integer> histogramEntry : countryHistogram.entrySet()) {
                String key = histogramEntry.getKey();
                int value = histogramEntry.getValue();

                int oldValue = organizationActivityLocationHistogram.get(key) == null ? 0 : organizationActivityLocationHistogram.get(key);

                organizationActivityLocationHistogram.put(key, oldValue + value);
            }
        }
    }

    private void updateUsersHistogram(Map<String, UserActivityLocation> userActivityLocationMap, List<FeatureBucket> featureBucketsForDataSource,
                                      Long startTime, Long endTime, List<String> dataSources) {
        for (FeatureBucket featureBucket : featureBucketsForDataSource) {
            String contextId = featureBucket.getContextId().substring(CONTEXT_ID_USERNAME_PREFIX_LENGTH);

            if (!userActivityLocationMap.containsKey(contextId)) {
                UserActivityLocation userActivityLocation = new UserActivityLocation();
                userActivityLocation.setNormalizedUsername(contextId);
                userActivityLocation.setStartTime(startTime);
                userActivityLocation.setEndTime(endTime);
                userActivityLocation.setDataSources(dataSources);

                userActivityLocationMap.put(contextId, userActivityLocation);
            }

            updateLocationsHistogram(userActivityLocationMap, featureBucket, contextId);
        }
    }

    private void updateLocationsHistogram(Map<String, UserActivityLocation> userActivityLocationMap, FeatureBucket featureBucket, String contextId) {
        UserActivityLocation userActivityLocation = userActivityLocationMap.get(contextId);
        Map<String, Integer> countryHistogramOfUser = userActivityLocation.getLocations().getCountryHistogram();

        Feature featureValue = featureBucket.getAggregatedFeatures().get(COUNTRY_HISTOGRAM_FEATURE_NAME);

        Map<String, Double> bucketHistogram = ((GenericHistogram) featureValue.getValue()).getHistogramMap();
        for (Map.Entry<String, Double> entry : bucketHistogram.entrySet()) {
            int oldValue = countryHistogramOfUser.get(entry.getKey()) != null ? countryHistogramOfUser.get(entry.getKey()) : 0;
            int newValue = entry.getValue().intValue();
            countryHistogramOfUser.put(entry.getKey(), oldValue + newValue);
        }
    }

    @Override
    public String getActivityName() {
        return ACTIVITY_NAME;
    }
}
