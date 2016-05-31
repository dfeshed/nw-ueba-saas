package fortscale.collection.jobs.activity;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.OrganizationActivityLocation;
import fortscale.domain.core.UserActivityLocation;
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
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author gils
 * 24/05/2016
 */
@Configurable(preConstruction = true)
@Component
public class UserActivityLocationsHandler implements UserActivityHandler {

    private static Logger logger = Logger.getLogger(UserActivityLocationsHandler.class);

    private static final String ACTIVITY_NAME = "locations";

    private static final String CONTEXT_ID_FIELD_NAME = "contextId";
    private static final String AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME = "aggregatedFeatures.country_histogram";
    private static final String COUNTRY_HISTOGRAM_FEATURE_NAME = "country_histogram";

    private final static String CONTEXT_ID_USERNAME_PREFIX = "normalized_username###";
    private static int CONTEXT_ID_USERNAME_PREFIX_LENGTH;

    private final static int MONGO_READ_WRITE_BULK_SIZE = 1000;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserActivityConfigurationService userActivityConfigurationService;

    static {
        CONTEXT_ID_USERNAME_PREFIX_LENGTH = CONTEXT_ID_USERNAME_PREFIX.length();
    }

    public void handle(long startTime, long endTime, UserActivityConfigurationService userActivityConfigurationService1, MongoTemplate mongoTemplate1) {
        mongoTemplate = mongoTemplate1;
        userActivityConfigurationService = userActivityConfigurationService1;

        logger.info("Going to handle User Locations Activity..");
        logger.info("Start Time = {}  ### End time = {}", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(startTime)), TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(endTime)));

        long fullExecutionStartTime = System.nanoTime();

        List<String> dataSources = userActivityConfigurationService.getDataSources(getActivityName());

        logger.info("Relevant Data sources for locations activity: {}", dataSources);

        DateTime dateStartTime = new DateTime(TimestampUtils.convertToMilliSeconds(startTime), DateTimeZone.UTC);
        long firstBucketStartTime = TimestampUtils.convertToSeconds(dateStartTime.withTimeAtStartOfDay().getMillis());
        long firstBucketEndTime = TimestampUtils.convertToSeconds(dateStartTime.withTimeAtStartOfDay().plusDays(1).minusSeconds(1).getMillis());

        DateTime dateEndTime = new DateTime(TimestampUtils.convertToMilliSeconds(endTime), DateTimeZone.UTC);
        long lastBucketEndTime = TimestampUtils.convertToSeconds(dateEndTime.withTimeAtStartOfDay().minusSeconds(1).getMillis());

        List<String> userIds = fetchAllActiveUserIds(dataSources, firstBucketStartTime, lastBucketEndTime);

        logger.info("Found {} active users for locations activity", userIds.size());

        int numberOfUsers = userIds.size();
        int numOfHandledUsers = 0;

        Map<String, Integer> organizationActivityLocationHistogram = new HashMap<>();

        long currBucketStartTime = firstBucketStartTime;
        long currBucketEndTime = firstBucketEndTime;

        while (currBucketEndTime <= lastBucketEndTime) {
            numOfHandledUsers = 0;

            logger.info("Fetching from Bucket Start Time = {}  till Bucket End time = {}", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime)), TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketEndTime)));

            while (numOfHandledUsers < numberOfUsers) {
                int actualUserChunkSize = Math.min(MONGO_READ_WRITE_BULK_SIZE, numberOfUsers);
                int startIndex = numOfHandledUsers;
                int endIndex = (numOfHandledUsers + MONGO_READ_WRITE_BULK_SIZE <= numberOfUsers) ? numOfHandledUsers + MONGO_READ_WRITE_BULK_SIZE : numberOfUsers - 1;

                List<String> usersChunk = userIds.subList(startIndex, endIndex);

                Map<String, UserActivityLocation> userActivityLocationMap = new HashMap<>(usersChunk.size());

                logger.info("Handling chunk of {} users ({} to {})", actualUserChunkSize, startIndex, endIndex);

                for (String dataSource : dataSources) {
                    List<FeatureBucket> locationsBucketsForDataSource = retrieveBuckets(currBucketStartTime, currBucketEndTime, usersChunk, dataSource);

                    long updateUsersHistogramInMemoryStartTime = System.nanoTime();
                    updateUsersHistogram(userActivityLocationMap, locationsBucketsForDataSource, currBucketStartTime, currBucketEndTime, dataSources);
                    long updateUsersHistogramInMemoryElapsedTime = System.nanoTime() - updateUsersHistogramInMemoryStartTime;
                    logger.info("Update users histogram in memory for {} users took {} seconds", usersChunk.size(), TimeUnit.SECONDS.convert(updateUsersHistogramInMemoryElapsedTime, TimeUnit.NANOSECONDS));
                }

                long updateOrgHistogramInMemoryStartTime = System.nanoTime();
                updateOrganizationHistogram(organizationActivityLocationHistogram, userActivityLocationMap, currBucketStartTime, currBucketEndTime, dataSources);
                long updateOrgHistogramInMemoryElapsedTime = System.nanoTime() - updateOrgHistogramInMemoryStartTime;
                logger.info("Update org histogram in memory took {} seconds", TimeUnit.SECONDS.convert(updateOrgHistogramInMemoryElapsedTime, TimeUnit.NANOSECONDS));

                Collection<UserActivityLocation> userActivityLocationsToInsert = userActivityLocationMap.values();

                insertUsersToDB(userActivityLocationsToInsert);

                numOfHandledUsers += MONGO_READ_WRITE_BULK_SIZE;
            }

            DateTime currDateTime = new DateTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime), DateTimeZone.UTC);
            currBucketStartTime = TimestampUtils.convertToSeconds(currDateTime.plusDays(1).getMillis());
            currBucketEndTime = TimestampUtils.convertToSeconds(currDateTime.plusDays(2).minus(1).getMillis());
        }

        long updateOrgHistogramInMongoStartTime = System.nanoTime();
        updateOrgHistogramInDB(startTime, endTime, dataSources, organizationActivityLocationHistogram);
        long updateOrgHistogramInMemoryElapsedTime = System.nanoTime() - updateOrgHistogramInMongoStartTime;
        logger.info("Update org histogram in Mongo took {} seconds", TimeUnit.SECONDS.convert(updateOrgHistogramInMemoryElapsedTime, TimeUnit.NANOSECONDS));

        long fullExecutionElapsedTime = System.nanoTime() - fullExecutionStartTime;
        logger.info("Full execution of Location Activity ({} active users) took {} seconds", userIds.size(), TimeUnit.SECONDS.convert(fullExecutionElapsedTime, TimeUnit.NANOSECONDS));
    }

    private void insertUsersToDB(Collection<UserActivityLocation> userActivityLocationsToInsert) {
        long insertStartTime = System.nanoTime();
        mongoTemplate.insert(userActivityLocationsToInsert, UserActivityLocation.COLLECTION_NAME);
        long elapsedInsertTime = System.nanoTime() - insertStartTime;
        logger.info("Insert {} users to Mongo took {} seconds", userActivityLocationsToInsert.size(), TimeUnit.SECONDS.convert(elapsedInsertTime, TimeUnit.NANOSECONDS));
    }

    private List<FeatureBucket> retrieveBuckets(long startTime, long endTime, List<String> usersChunk, String dataSource) {
        Criteria usersCriteria = Criteria.where(FeatureBucket.CONTEXT_ID_FIELD).in(usersChunk);
        Criteria startTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));
        Criteria endTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(endTime));
        Query query = new Query(usersCriteria.andOperator(startTimeCriteria.andOperator(endTimeCriteria)));
        query.fields().include(CONTEXT_ID_FIELD_NAME);
        query.fields().include(AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME);

        long queryStartTime = System.nanoTime();
        List<FeatureBucket> featureBucketsForDataSource = mongoTemplate.find(query, FeatureBucket.class, userActivityConfigurationService.getCollectionName(dataSource));
        long queryElapsedTime = System.nanoTime() - queryStartTime;
        logger.info("Query {} aggregation collection for {} users took {} seconds", dataSource, usersChunk.size(), TimeUnit.SECONDS.convert(queryElapsedTime, TimeUnit.NANOSECONDS));
        return featureBucketsForDataSource;
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

    private List<String> fetchAllActiveUserIds(List<String> dataSources, long startTime, long endTime) {
        List<String> userIds = new ArrayList<>();

        for (String dataSource : dataSources) {
            Criteria startTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));
            Criteria endTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(endTime));
            Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria));

            String collectionName = userActivityConfigurationService.getCollectionName(dataSource);

            List<String> contextIdList = mongoTemplate.getCollection(collectionName).distinct("contextId", query.getQueryObject());

            userIds.addAll(contextIdList);
        }

        return userIds;
    }

    private void updateOrganizationHistogram(Map<String, Integer> organizationActivityLocationHistogram, Map<String, UserActivityLocation> userActivityLocationMap, Long startTime, Long endTime, List<String> dataSources) {
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
