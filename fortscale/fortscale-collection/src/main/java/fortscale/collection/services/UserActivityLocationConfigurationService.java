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
public class UserActivityLocationConfigurationService implements UserActivityConfigurationService, InitializingBean {

    private final static String USER_VPN_COLLECTION = "aggr_normalized_username_vpn_daily";
    private final static String USER_CRMSF_COLLECTION = "aggr_normalized_username_crmsf_daily";
    private static final String USER_ACTIVITY_LOCATION_CONFIGURATION_KEY = "user_activity.location.configuration";
    private static final String LOCATIONS_PROPERTY_NAME = "locations";
    private static final String CRMSF_DATA_SOURCE_PROPERTY_NAME = "crmsf";
    private static final String VPN_DATA_SOURCE_PROPERTY_NAME = "vpn";
    private static final Logger logger = Logger.getLogger(UserActivityLocationConfigurationService.class);


    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserActivityLocationConfigurationService() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (applicationConfigurationService != null) {
            saveUserActivityConfigurationToDatabase();
        }
        else {
            throw new RuntimeException("Failed to inject ApplicationConfigurationService. User Activity location Configuration was not set");
        }

    }

    @Override
    public UserActivityConfiguration getUserActivityConfigurationFromDatabase() {
        final Optional<String> optionalUserLocationActivityConfiguration = applicationConfigurationService.getApplicationConfigurationAsString(USER_ACTIVITY_LOCATION_CONFIGURATION_KEY);
        if (optionalUserLocationActivityConfiguration.isPresent()) {
            try {
                return objectMapper.readValue(optionalUserLocationActivityConfiguration.get(), UserActivityConfiguration.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to get user activity location from database", e);
            }
        }
        else {
            throw new RuntimeException("Failed to get user activity location from database. Got empty response");
        }
    }

    @Override
    public void saveUserActivityConfigurationToDatabase() throws JsonProcessingException {
        UserActivityConfiguration userActivityLocationConfiguration = createUserActivityConfiguration();
        String userActivityLocationConfigurationAsJsonString = objectMapper.writeValueAsString(userActivityLocationConfiguration);
        applicationConfigurationService.insertConfigItem(USER_ACTIVITY_LOCATION_CONFIGURATION_KEY, userActivityLocationConfigurationAsJsonString);
    }

    @Override
    public UserActivityConfiguration createUserActivityConfiguration() {
        final Set<String> activities = new HashSet<>();
        activities.add(LOCATIONS_PROPERTY_NAME);

        final Map<String, String> dataSourceToCollection = new HashMap<>();
        dataSourceToCollection.put(VPN_DATA_SOURCE_PROPERTY_NAME, USER_VPN_COLLECTION);
        dataSourceToCollection.put(CRMSF_DATA_SOURCE_PROPERTY_NAME, USER_CRMSF_COLLECTION);

        final Map<String, List<String>> activityToDataSources = new HashMap<>();
        activityToDataSources.put(LOCATIONS_PROPERTY_NAME, new ArrayList<>(Arrays.asList(VPN_DATA_SOURCE_PROPERTY_NAME, CRMSF_DATA_SOURCE_PROPERTY_NAME)));

        return new UserActivityConfiguration(activities, dataSourceToCollection, activityToDataSources);
    }

    @Override
    public UserActivityConfiguration getUserActivityConfiguration() {
        try {
            return getUserActivityConfigurationFromDatabase();
        } catch (RuntimeException e) {
            logger.error(e.getLocalizedMessage());
            throw e;
        }
    }


}

