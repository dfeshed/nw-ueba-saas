package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityLocationConfigurationService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
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
import java.util.function.Function;

/**
 * User activity locations handler implementation
 *
 * @author gils
 * 24/05/2016
 */
@Configurable(preConstruction = true)
@Component
public class UserActivityLocationsHandler extends UserActivityBaseHandler {

    private static final UserActivityType ACTIVITY = UserActivityType.LOCATIONS;
    private static final String AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME = "aggregatedFeatures.country_histogram";
    private static final String COUNTRY_HISTOGRAM_FEATURE_NAME = "country_histogram";
    private static Logger logger = Logger.getLogger(UserActivityLocationsHandler.class);

    @Autowired
    protected UserActivityLocationConfigurationService userActivityLocationConfigurationService;

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
    protected GenericHistogram convertFeatureToHistogram(Object objectToConvert, String histogramFeatureName) {
        if (objectToConvert instanceof Feature && ((Feature) objectToConvert).getValue() instanceof GenericHistogram) {
            return (GenericHistogram) ((Feature) objectToConvert).getValue();
        }
        else {
            final String errorMessage = String.format("Can't convert %s object of class %s", objectToConvert, objectToConvert.getClass());
            getLogger().error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }


    @Override
    Function<Integer, Integer> valueReducer() {
        return (newValue) -> 1;
    };

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
    protected List<String> getHistogramFeatureNames() {
        return Collections.singletonList(COUNTRY_HISTOGRAM_FEATURE_NAME);
    }

    @Override
    public UserActivityType getActivity() {
        return ACTIVITY;
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
