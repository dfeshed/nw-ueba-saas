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
    private static final String COUNTRY_HISTOGRAM_FEATURE_NAME = "country_histogram";
    private static final String AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME = "aggregatedFeatures." + COUNTRY_HISTOGRAM_FEATURE_NAME;
    private static Logger logger = Logger.getLogger(UserActivityLocationsHandler.class);

    @Autowired
    protected UserActivityLocationConfigurationService userActivityLocationConfigurationService;

    private void updateOrgHistogramInDB(long startTime, long endTime, List<String> dataSources, Map<String, Double> organizationActivityLocationHistogram) {
        OrganizationActivityLocationDocument organizationActivityLocationDocument = new OrganizationActivityLocationDocument();

        organizationActivityLocationDocument.setStartTime(startTime);
        organizationActivityLocationDocument.setEndTime(endTime);
        organizationActivityLocationDocument.setDataSources(dataSources);

        OrganizationActivityLocationDocument.Locations locations = new OrganizationActivityLocationDocument.Locations();
        locations.getCountryHistogram().putAll(organizationActivityLocationHistogram);
        organizationActivityLocationDocument.setLocations(locations);

        mongoTemplate.save(organizationActivityLocationDocument, OrganizationActivityLocationDocument.COLLECTION_NAME);
    }

    private void updateOrganizationHistogram(Map<String, Double> organizationActivityLocationHistogram, Map<String, UserActivityDocument> userActivityMap) {
        for (UserActivityDocument userActivityDocument : userActivityMap.values()) {
            Map<String, Double> countryHistogram = userActivityDocument.getHistogram();

            for (Map.Entry<String, Double> histogramEntry : countryHistogram.entrySet()) {
                String key = histogramEntry.getKey();
                double value = histogramEntry.getValue();

                double oldValue = organizationActivityLocationHistogram.get(key) == null ? 0 : organizationActivityLocationHistogram.get(key);

                organizationActivityLocationHistogram.put(key, oldValue + value);
            }
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
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
    protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram) {
        long updateOrgHistogramInMongoStartTime = System.nanoTime();
        updateOrgHistogramInDB(currBucketStartTime, currBucketEndTime, dataSources, additionalActivityHistogram);
        long updateOrgHistogramInMemoryElapsedTime = System.nanoTime() - updateOrgHistogramInMongoStartTime;
        logger.info("Update org histogram in Mongo took {} seconds", TimeUnit.MILLISECONDS.convert(updateOrgHistogramInMemoryElapsedTime, TimeUnit.NANOSECONDS));
    }

    @Override
    protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
        return Collections.singletonList(COUNTRY_HISTOGRAM_FEATURE_NAME);
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
    protected Map<String, Double> updateAdditionalActivitySpecificHistograms(Map<String, UserActivityDocument> userActivityMap){
        Map<String, Double> organizationActivityLocationHistogram = new HashMap<>();
        long updateOrgHistogramInMemoryStartTime = System.nanoTime();
        updateOrganizationHistogram(organizationActivityLocationHistogram, userActivityMap);
        long updateOrgHistogramInMemoryElapsedTime = System.nanoTime() - updateOrgHistogramInMemoryStartTime;
        logger.info("Update org histogram in memory took {} seconds", durationInSecondsWithPrecision(updateOrgHistogramInMemoryElapsedTime));

        return organizationActivityLocationHistogram;
    }
}
