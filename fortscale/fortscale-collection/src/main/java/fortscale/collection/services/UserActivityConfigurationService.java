package fortscale.collection.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

/**
 * @author gils
 * 24/05/2016
 */
public class UserActivityConfigurationService implements InitializingBean {

    private final static String USER_VPN_COLLECTION = "aggr_normalized_username_vpn_daily";
    private final static String USER_CRMSF_COLLECTION = "aggr_normalized_username_crmsf_daily";
    private static final String USER_ACTIVITY_LOCATION_CONFIGURATION_KEY = "user_activity.location.configuration";
    private static final String LOCATIONS_PROPERTY_NAME = "locations";
    private static final String CRMSF_DATA_SOURCE_PROPERTY_NAME = "crmsf";
    private static final String VPN_DATA_SOURCE_PROPERTY_NAME = "vpn";
    private static final Logger logger = Logger.getLogger(UserActivityConfigurationService.class);


    private final ApplicationConfigurationService applicationConfigurationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public UserActivityConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (applicationConfigurationService != null) {
            saveUserLocationActivityConfigurationToDatabase();
        }
        else {
            throw new RuntimeException("Failed to inject ApplicationConfigurationService. User Activity Configuration was not set");
        }

    }

    public UserLocationActivityConfiguration getUserLocationActivityConfiguration() {
        try {
            return getUserLocationActivityConfigurationFromDatabase();
        } catch (RuntimeException e) {
            logger.error(e.getLocalizedMessage());
            throw e;
        }
    }


    private UserLocationActivityConfiguration getUserLocationActivityConfigurationFromDatabase() {
        final Optional<String> optionalUserLocationActivityConfiguration = applicationConfigurationService.readFromConfigurationService(USER_ACTIVITY_LOCATION_CONFIGURATION_KEY);
        if (optionalUserLocationActivityConfiguration.isPresent()) {
            try {
                return objectMapper.readValue(optionalUserLocationActivityConfiguration.get(), UserLocationActivityConfiguration.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to get user location activity from database", e);
            }
        }
        else {
            throw new RuntimeException("Failed to get user location activity from database. Got empty response");
        }
    }



    private void saveUserLocationActivityConfigurationToDatabase() throws JsonProcessingException {
        UserLocationActivityConfiguration userLocationActivityConfiguration = createUserLocationActivity();
        String locationActivityAsJsonString = objectMapper.writeValueAsString(userLocationActivityConfiguration);
        applicationConfigurationService.insertConfigItem(USER_ACTIVITY_LOCATION_CONFIGURATION_KEY, locationActivityAsJsonString);
    }

    private UserLocationActivityConfiguration createUserLocationActivity() {
        final Set<String> activities = new HashSet<>();
        activities.add(LOCATIONS_PROPERTY_NAME);

        final Map<String, String> dataSourceToCollection = new HashMap<>();
        dataSourceToCollection.put(VPN_DATA_SOURCE_PROPERTY_NAME, USER_VPN_COLLECTION);
        dataSourceToCollection.put(CRMSF_DATA_SOURCE_PROPERTY_NAME, USER_CRMSF_COLLECTION);

        final Map<String, List<String>> activityToDataSources = new HashMap<>();
        activityToDataSources.put(LOCATIONS_PROPERTY_NAME, new ArrayList<>(Arrays.asList(VPN_DATA_SOURCE_PROPERTY_NAME, CRMSF_DATA_SOURCE_PROPERTY_NAME)));

        return new UserLocationActivityConfiguration(activities, dataSourceToCollection, activityToDataSources);
    }

    public static class UserLocationActivityConfiguration {

        private Set<String> activities;
        private Map<String, String> dataSourceToCollection;
        private Map<String, List<String>> activityToDataSources;

        private UserLocationActivityConfiguration(Set<String> activities, Map<String, String> dataSourceToCollection, Map<String, List<String>> activityToDataSources) {
            this.activities = activities;
            this.dataSourceToCollection = dataSourceToCollection;
            this.activityToDataSources = activityToDataSources;
        }

        public Set<String> getActivities() {
            return activities;
        }

        public void setActivities(Set<String> activities) {
            this.activities = activities;
        }

        public Map<String, String> getDataSourceToCollection() {
            return dataSourceToCollection;
        }

        public void setDataSourceToCollection(Map<String, String> dataSourceToCollection) {
            this.dataSourceToCollection = dataSourceToCollection;
        }

        public Map<String, List<String>> getActivityToDataSources() {
            return activityToDataSources;
        }

        public void setActivityToDataSources(Map<String, List<String>> activityToDataSources) {
            this.activityToDataSources = activityToDataSources;
        }
    }


}

