package fortscale.collection.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service("userActivityLocationConfigurationService")
public class UserActivityLocationConfigurationServiceImpl implements UserActivityLocationConfigurationService, InitializingBean {

    private final static String USER_VPN_COLLECTION = "aggr_normalized_username_vpn_daily";
    private final static String USER_CRMSF_COLLECTION = "aggr_normalized_username_crmsf_daily";
    private static final String USER_ACTIVITY_LOCATION_CONFIGURATION_KEY = "user_activity.location.configuration";
    private static final String LOCATIONS_PROPERTY_NAME = "locations";
    private static final String CRMSF_DATA_SOURCE_PROPERTY_NAME = "crmsf";
    private static final String VPN_DATA_SOURCE_PROPERTY_NAME = "vpn";
    private static final Logger logger = Logger.getLogger(UserActivityLocationConfigurationServiceImpl.class);


    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserActivityLocationConfigurationServiceImpl() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (applicationConfigurationService != null) {
            saveUserActivityLocationConfigurationToDatabase();
        }
        else {
            throw new RuntimeException("Failed to inject ApplicationConfigurationService. User Activity location Configuration was not set");
        }

    }



    private UserActivityLocationConfiguration getUserActivityLocationConfigurationFromDatabase() {
        final Optional<String> optionalUserLocationActivityConfiguration = applicationConfigurationService.readFromConfigurationService(USER_ACTIVITY_LOCATION_CONFIGURATION_KEY);
        if (optionalUserLocationActivityConfiguration.isPresent()) {
            try {
                return objectMapper.readValue(optionalUserLocationActivityConfiguration.get(), UserActivityLocationConfiguration.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to get user activity location from database", e);
            }
        }
        else {
            throw new RuntimeException("Failed to get user activity location from database. Got empty response");
        }
    }

    private void saveUserActivityLocationConfigurationToDatabase() throws JsonProcessingException {
        UserActivityLocationConfiguration userActivityLocationConfiguration = createUserActivityLocationConfiguration();
        String userActivityLocationConfigurationAsJsonString = objectMapper.writeValueAsString(userActivityLocationConfiguration);
        applicationConfigurationService.insertConfigItem(USER_ACTIVITY_LOCATION_CONFIGURATION_KEY, userActivityLocationConfigurationAsJsonString);
    }

    private UserActivityLocationConfiguration createUserActivityLocationConfiguration() {
        final Set<String> activities = new HashSet<>();
        activities.add(LOCATIONS_PROPERTY_NAME);

        final Map<String, String> dataSourceToCollection = new HashMap<>();
        dataSourceToCollection.put(VPN_DATA_SOURCE_PROPERTY_NAME, USER_VPN_COLLECTION);
        dataSourceToCollection.put(CRMSF_DATA_SOURCE_PROPERTY_NAME, USER_CRMSF_COLLECTION);

        final Map<String, List<String>> activityToDataSources = new HashMap<>();
        activityToDataSources.put(LOCATIONS_PROPERTY_NAME, new ArrayList<>(Arrays.asList(VPN_DATA_SOURCE_PROPERTY_NAME, CRMSF_DATA_SOURCE_PROPERTY_NAME)));

        return new UserActivityLocationConfiguration(activities, dataSourceToCollection, activityToDataSources);
    }

    @Override
    public UserActivityLocationConfiguration getUserActivityLocationConfiguration() {
        try {
            return getUserActivityLocationConfigurationFromDatabase();
        } catch (RuntimeException e) {
            logger.error(e.getLocalizedMessage());
            throw e;
        }
    }

    public static class UserActivityLocationConfiguration {

        private Set<String> activities;
        private Map<String, String> dataSourceToCollection;
        private Map<String, List<String>> activityToDataSources;

        private UserActivityLocationConfiguration(Set<String> activities, Map<String, String> dataSourceToCollection, Map<String, List<String>> activityToDataSources) {
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

        public List<String> getDataSources() {
            return new ArrayList<>(dataSourceToCollection.keySet());
        }

        public String getCollection(String dataSource) {
            final String collectionName = dataSourceToCollection.get(dataSource);
            if (collectionName == null) {
                final String errorMessage = String.format("Failed to get collection for data source %s", dataSource);
                logger.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }

            return collectionName;
        }
    }


}

