package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityLocationConfigurationService;
import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    private static final String ACTIVITY_NAME = "locations";
    private static final String AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME = "aggregatedFeatures.country_histogram";
    private static final String COUNTRY_HISTOGRAM_FEATURE_NAME = "country_histogram";
    private static Logger logger = Logger.getLogger(UserActivityLocationsHandler.class);

    @Autowired
    protected UserActivityLocationConfigurationService userActivityLocationConfigurationService;
//
//    public void calculate2(int numOfLastDaysToCalculate) {
//        long endTime = System.currentTimeMillis();
//        long startingTime = TimestampUtils.toStartOfDay(TimeUtils.calculateStartingTime(endTime, numOfLastDaysToCalculate));
//
//        logger.info("Going to handle User Locations Activity..");
//        logger.info("Start Time = {}  ### End time = {}", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(startingTime)), TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(endTime)));
//
//        long fullExecutionStartTime = System.nanoTime();
//
//        UserActivityJobState userActivityJobState = loadAndUpdateJobState(numOfLastDaysToCalculate);
//
//        UserActivityConfiguration userActivityConfiguration = userActivityLocationConfigurationService.getUserActivityConfiguration();
//        List<String> dataSources = userActivityConfiguration.getDataSources();
//        logger.info("Relevant Data sources for locations activity: {}", dataSources);
//
//        DateTime dateStartTime = new DateTime(TimestampUtils.convertToMilliSeconds(startingTime), DateTimeZone.UTC);
//        long firstBucketStartTime = TimestampUtils.convertToSeconds(dateStartTime.withTimeAtStartOfDay().getMillis());
//        long firstBucketEndTime = TimestampUtils.convertToSeconds(dateStartTime.withTimeAtStartOfDay().plusDays(1).minusSeconds(1).getMillis());
//
//        DateTime dateEndTime = new DateTime(TimestampUtils.convertToMilliSeconds(endTime), DateTimeZone.UTC);
//        long lastBucketEndTime = TimestampUtils.convertToSeconds(dateEndTime.withTimeAtStartOfDay().minusSeconds(1).getMillis());
//
//        final Map<String, String> dataSourceToCollection = userActivityConfiguration.getDataSourceToCollection();
//        List<String> userIds = fetchAllActiveUserIds(dataSources, firstBucketStartTime, lastBucketEndTime, dataSourceToCollection);
//
//        if (userIds.isEmpty()) {
//            logger.warn("Could not found any user. Abort job");
//
//            return;
//        }
//
//        int numberOfUsers = userIds.size();
//        logger.info("Found {} active users for locations activity", numberOfUsers);
//
//        int numOfHandledUsers;
//
//        long currBucketStartTime = firstBucketStartTime;
//        long currBucketEndTime = firstBucketEndTime;
//
//        while (currBucketEndTime <= lastBucketEndTime) {
//
//            if (userActivityJobState.getCompletedExecutionDays().contains(currBucketStartTime)) {
//                logger.info("Skipping job process for bucket start time {} (already calculated)", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime)));
//            }
//            numOfHandledUsers = 0;
//
//            logger.info("Going to fetch from Bucket Start Time = {}  till Bucket End time = {}", TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime)), TimeUtils.getUTCFormattedTime(TimestampUtils.convertToMilliSeconds(currBucketEndTime)));
//
//            while (numOfHandledUsers < numberOfUsers) {
//                int actualUserChunkSize = Math.min(MONGO_READ_WRITE_BULK_SIZE, numberOfUsers);
//                int startIndex = numOfHandledUsers;
//                int endIndex = (numOfHandledUsers + MONGO_READ_WRITE_BULK_SIZE <= numberOfUsers) ? numOfHandledUsers + MONGO_READ_WRITE_BULK_SIZE : numberOfUsers - 1;
//
//                List<String> usersChunk = userIds.subList(startIndex, endIndex);
//
//                Map<String, UserActivityLocationDocument> userActivityLocationMap = new HashMap<>(usersChunk.size());
//
//                logger.info("Handling chunk of {} users ({} to {})", actualUserChunkSize, startIndex, endIndex);
//
//                for (String dataSource : dataSources) {
//                    String collectionName = userActivityConfiguration.getCollection(dataSource);
//                    List<FeatureBucket> locationsBucketsForDataSource = retrieveBuckets(currBucketStartTime, currBucketEndTime, usersChunk, dataSource, collectionName);
//
//                    if (!locationsBucketsForDataSource.isEmpty()) {
//                        long updateUsersHistogramInMemoryStartTime = System.nanoTime();
//                        updateUsersHistogram(userActivityLocationMap, locationsBucketsForDataSource, currBucketStartTime, currBucketEndTime, dataSources);
//                        long updateUsersHistogramInMemoryElapsedTime = System.nanoTime() - updateUsersHistogramInMemoryStartTime;
//                        logger.info("Update users histogram in memory for {} users took {} seconds", usersChunk.size(), durationInSecondsWithPrecision(updateUsersHistogramInMemoryElapsedTime));
//                    }
//                }
//
//                Map<String, Integer> organizationActivityLocationHistogram = updateAdditionalActivitySpecificHistograms(userActivityLocationMap);
//
//                Collection<UserActivityLocationDocument> userActivityLocationsToInsertDocument = userActivityLocationMap.values();
//
//                insertUsersActivityToDB(userActivityLocationsToInsertDocument);
//
//                numOfHandledUsers += MONGO_READ_WRITE_BULK_SIZE;
//            }
//
//            logger.info("Updating job's state..");
//            updateJobState(currBucketStartTime);
//            logger.info("Job state was updated successfully");
//
//            long updateOrgHistogramInMongoStartTime = System.nanoTime();
//            updateOrgHistogramInDB(currBucketStartTime, currBucketEndTime, dataSources, organizationActivityLocationHistogram);
//            long updateOrgHistogramInMemoryElapsedTime = System.nanoTime() - updateOrgHistogramInMongoStartTime;
//            logger.info("Update org histogram in Mongo took {} seconds", TimeUnit.MILLISECONDS.convert(updateOrgHistogramInMemoryElapsedTime, TimeUnit.NANOSECONDS));
//
//            DateTime currDateTime = new DateTime(TimestampUtils.convertToMilliSeconds(currBucketStartTime), DateTimeZone.UTC);
//            currBucketStartTime = TimestampUtils.convertToSeconds(currDateTime.plusDays(1).getMillis());
//            currBucketEndTime = TimestampUtils.convertToSeconds(currDateTime.plusDays(2).minus(1).getMillis());
//        }
//
//        long fullExecutionElapsedTime = System.nanoTime() - fullExecutionStartTime;
//        logger.info("Full execution of Location Activity ({} active users) took {} seconds", userIds.size(), durationInSecondsWithPrecision(fullExecutionElapsedTime));
//    }

    private void updateOrgHistogramInDB(long startTime, long endTime, List<String> dataSources, Map<String, Integer> organizationActivityLocationHistogram) {
        OrganizationActivityLocationDocument organizationActivityLocationDocument = new OrganizationActivityLocationDocument();

        organizationActivityLocationDocument.setStartTime(startTime);
        organizationActivityLocationDocument.setEndTime(endTime);
        organizationActivityLocationDocument.setDataSources(dataSources);

        OrganizationActivityLocationDocument.Locations locations = new OrganizationActivityLocationDocument.Locations();
        locations.getCountryHistogram().putAll(organizationActivityLocationHistogram);
        organizationActivityLocationDocument.setLocations(locations);

        mongoTemplate.save(organizationActivityLocationDocument, OrganizationActivityLocationDocument.COLLECTION_NAME);
    }

    private void updateOrganizationHistogram(Map<String, Integer> organizationActivityLocationHistogram, Map<String, UserActivityDocument> userActivityMap) {
        for (UserActivityDocument userActivityDocument : userActivityMap.values()) {
            Map<String, Integer> countryHistogram = userActivityDocument.getHistogram();

            for (Map.Entry<String, Integer> histogramEntry : countryHistogram.entrySet()) {
                String key = histogramEntry.getKey();
                int value = histogramEntry.getValue();

                int oldValue = organizationActivityLocationHistogram.get(key) == null ? 0 : organizationActivityLocationHistogram.get(key);

                organizationActivityLocationHistogram.put(key, oldValue + value);
            }
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void removeRelevantDocuments(Object startingTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where(UserActivityLocationDocument.START_TIME_FIELD_NAME).lt(startingTime));

        mongoTemplate.remove(query, UserActivityLocationDocument.class);

        query = new Query();
        query.addCriteria(Criteria.where(OrganizationActivityLocationDocument.START_TIME_FIELD_NAME).lt(startingTime));
        mongoTemplate.remove(query, OrganizationActivityLocationDocument.class);
    }

    @Override
    protected String getCollectionName() {
        return UserActivityLocationDocument.COLLECTION_NAME;
    }

    @Override
    protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
        final String dataSourceLowerCase = dataSource.toLowerCase();
        if (dataSourceLowerCase.equals(UserActivityLocationConfigurationService.DATA_SOURCE_CRMSF_PROPERTY_NAME) || dataSourceLowerCase.equals(UserActivityLocationConfigurationService.DATA_SOURCE_VPN_PROPERTY_NAME)) {
            return new ArrayList<>(Collections.singletonList(AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME));
        }
        else {
            throw new IllegalArgumentException("Invalid data source: " + dataSource);
        }
    }

    @Override
    protected List<Class> getRelevantDocumentClasses() {
        return new ArrayList<>(Arrays.asList(UserActivityLocationDocument.class, OrganizationActivityLocationDocument.class));
    }

    @Override
    protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Integer> additionalActivityHistogram) {
        long updateOrgHistogramInMongoStartTime = System.nanoTime();
        updateOrgHistogramInDB(currBucketStartTime, currBucketEndTime, dataSources, additionalActivityHistogram);
        long updateOrgHistogramInMemoryElapsedTime = System.nanoTime() - updateOrgHistogramInMongoStartTime;
        logger.info("Update org histogram in Mongo took {} seconds", TimeUnit.MILLISECONDS.convert(updateOrgHistogramInMemoryElapsedTime, TimeUnit.NANOSECONDS));
    }

    @Override
    protected String getHistogramFeatureName() {
        return COUNTRY_HISTOGRAM_FEATURE_NAME;
    }

    @Override
    public String getActivityName() {
        return ACTIVITY_NAME;
    }

    @Override
    protected UserActivityConfigurationService getUserActivityConfigurationService() {
        return userActivityLocationConfigurationService;
    }

    @Override
    protected Map<String, Integer> updateAdditionalActivitySpecificHistograms(Map<String, UserActivityDocument> userActivityMap){
        Map<String, Integer> organizationActivityLocationHistogram = new HashMap<>();
        long updateOrgHistogramInMemoryStartTime = System.nanoTime();
        updateOrganizationHistogram(organizationActivityLocationHistogram, userActivityMap);
        long updateOrgHistogramInMemoryElapsedTime = System.nanoTime() - updateOrgHistogramInMemoryStartTime;
        logger.info("Update org histogram in memory took {} seconds", durationInSecondsWithPrecision(updateOrgHistogramInMemoryElapsedTime));

        return organizationActivityLocationHistogram;
    }
}
