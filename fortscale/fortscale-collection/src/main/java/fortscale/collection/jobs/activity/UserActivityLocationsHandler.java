package fortscale.collection.jobs.activity;

import fortscale.aggregation.feature.functions.AggGenericNAFeatureValues;
import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityLocationConfigurationService;
import fortscale.domain.core.User;
import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.utils.time.TimestampUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * User activity locations handler implementation
 *
 * @author gils
 *         24/05/2016
 */
@Configurable(preConstruction = true)
@Component
public class UserActivityLocationsHandler extends UserActivityBaseHandler {

    private static final UserActivityType ACTIVITY = UserActivityType.LOCATIONS;
    private static final String COUNTRY_HISTOGRAM_FEATURE_NAME = "country_histogram";
    private static final String AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME = AGGREGATED_FEATURES_PREFIX + "." + COUNTRY_HISTOGRAM_FEATURE_NAME;

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

        userActivityFeaturesExtractionsRepositoryUtil.saveDocument(OrganizationActivityLocationDocument.COLLECTION_NAME, organizationActivityLocationDocument);
    }

    private void updateOrganizationHistogram(Map<String, Double> organizationActivityLocationHistogram, Map<String, UserActivityDocument> userActivityMap) {
        for (UserActivityDocument userActivityDocument : userActivityMap.values()) {
            Map<String, Double> countryHistogram = userActivityDocument.getHistogram();

            for (Map.Entry<String, Double> histogramEntry : countryHistogram.entrySet()) {
                String key = histogramEntry.getKey();
                if (key.equals(AggGenericNAFeatureValues.NOT_AVAILABLE) && !countNAValues()) {
                    continue;
                }
                double value = histogramEntry.getValue();

                double oldValue = organizationActivityLocationHistogram.get(key) == null ? 0 : organizationActivityLocationHistogram.get(key);

                organizationActivityLocationHistogram.put(key, oldValue + value);
            }
        }
    }

    @Override
    protected boolean countNAValues() {
        return false;
    }

    @Override
    Function<Double, Double> valueReducer() {
        return (newValue) -> 1.0;
    }

    ;

    @Override
    protected String getCollectionName() {
        return UserActivityLocationDocument.COLLECTION_NAME;
    }

    @Override
    protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
        final String dataSourceLowerCase = dataSource.toLowerCase();
        if (dataSourceLowerCase.equals(UserActivityLocationConfigurationService.DATA_SOURCE_CRMSF_PROPERTY_NAME) || dataSourceLowerCase.equals(UserActivityLocationConfigurationService.DATA_SOURCE_VPN_PROPERTY_NAME)) {
            return new ArrayList<>(Collections.singletonList(AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME));
        } else {
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
    public UserActivityType getActivity() {
        return ACTIVITY;
    }

    @Override
    protected UserActivityConfigurationService getUserActivityConfigurationService() {
        return userActivityLocationConfigurationService;
    }

    @Override
    protected Map<String, Double> updateAdditionalActivitySpecificHistograms(Map<String, UserActivityDocument> userActivityMap) {
        Map<String, Double> organizationActivityLocationHistogram = new HashMap<>();
        long updateOrgHistogramInMemoryStartTime = System.nanoTime();
        updateOrganizationHistogram(organizationActivityLocationHistogram, userActivityMap);
        long updateOrgHistogramInMemoryElapsedTime = System.nanoTime() - updateOrgHistogramInMemoryStartTime;
        logger.info("Update org histogram in memory took {} seconds", durationInSecondsWithPrecision(updateOrgHistogramInMemoryElapsedTime));

        return organizationActivityLocationHistogram;
    }


    //This fetches all the active users from a certain point in time.
    //There is an underlying assumption that we will always search for usernames with the CONTEXT_ID_USERNAME_PREFIX
    @Override
    protected Map<String, List<String>> fetchUserIdPerDatasource(List<String> dataSources, long startTime, long endTime, Map<String, String> dataSourceToCollection) {
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
}
