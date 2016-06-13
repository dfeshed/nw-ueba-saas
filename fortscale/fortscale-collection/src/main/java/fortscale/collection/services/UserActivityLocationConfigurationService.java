package fortscale.collection.services;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userActivityLocationConfigurationService")
public class UserActivityLocationConfigurationService extends BaseUserActivityConfigurationService implements InitializingBean {

    private final static String USER_VPN_COLLECTION = "aggr_normalized_username_vpn_daily";
    private final static String USER_CRMSF_COLLECTION = "aggr_normalized_username_crmsf_daily";
    private static final String USER_ACTIVITY_LOCATION_CONFIGURATION_KEY = "system.user_activity.location";
    public static final String ACTIVITY_LOCATIONS_PROPERTY_NAME = "locations";
    public static final String DATA_SOURCE_CRMSF_PROPERTY_NAME = "crmsf";
    public static final String DATA_SOURCE_VPN_PROPERTY_NAME = "vpn";
    private static final Logger logger = Logger.getLogger(UserActivityLocationConfigurationService.class);


    public UserActivityLocationConfigurationService() {
    }

    @Override
    public UserActivityConfiguration createUserActivityConfiguration() {
        final Set<String> activities = new HashSet<>();
        activities.add(ACTIVITY_LOCATIONS_PROPERTY_NAME);

        final Map<String, String> dataSourceToCollection = new HashMap<>();
        dataSourceToCollection.put(DATA_SOURCE_VPN_PROPERTY_NAME, USER_VPN_COLLECTION);
        dataSourceToCollection.put(DATA_SOURCE_CRMSF_PROPERTY_NAME, USER_CRMSF_COLLECTION);

        final Map<String, List<String>> activityToDataSources = new HashMap<>();
        activityToDataSources.put(ACTIVITY_LOCATIONS_PROPERTY_NAME, new ArrayList<>(Arrays.asList(DATA_SOURCE_VPN_PROPERTY_NAME, DATA_SOURCE_CRMSF_PROPERTY_NAME)));

        return new UserActivityConfiguration(activities, dataSourceToCollection, activityToDataSources);
    }


    @Override
    protected String getActivityName() {
        return "location";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    protected String getConfigurationKey() {
        return USER_ACTIVITY_LOCATION_CONFIGURATION_KEY;
    }
}

