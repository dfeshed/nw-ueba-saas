package fortscale.collection.services;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userActivityDataUsageConfigurationService")
public class UserActivityDataUsageConfigurationService extends BaseUserActivityConfigurationService
		implements InitializingBean {

	private static final Logger logger = Logger.getLogger(UserActivityDataUsageConfigurationService.class);

	public static final String DATA_SOURCE_VPN_SESSION_PROPERTY_NAME = "vpn_session";
	public static final String DATA_SOURCE_PRINT_LOG_PROPERTY_NAME = "prnlog";
	public static final String DATA_SOURCE_ORACLE_PROPERTY_NAME = "oracle";

	private static final String USER_PRINT_LOG_COLLECTION = "aggr_normalized_username_prnlog_daily";
	private static final String USER_VPN_SESSIONS_COLLECTION = "aggr_normalized_username_vpn_session_daily";
	private static final String USER_ORACLE_COLLECTION = "aggr_normalized_username_oracle_daily";
	private static final String USER_ACTIVITY_DATA_USAGE_CONFIGURATION_KEY = "system.user_activity.data_usage";
	private static final String ACTIVITY_DATA_USAGE_PROPERTY_NAME = "data_usage";

	@Override
	public UserActivityConfiguration createUserActivityConfiguration() {
		final Set<String> activities = new HashSet();
		final Map<String, String> dataSourceToCollection = new HashMap();
		final Map<String, List<String>> activityToDataSources = new HashMap();
		activities.add(ACTIVITY_DATA_USAGE_PROPERTY_NAME);
		dataSourceToCollection.put(DATA_SOURCE_VPN_SESSION_PROPERTY_NAME, USER_VPN_SESSIONS_COLLECTION);
		dataSourceToCollection.put(USER_PRINT_LOG_COLLECTION, DATA_SOURCE_PRINT_LOG_PROPERTY_NAME);
		dataSourceToCollection.put(DATA_SOURCE_ORACLE_PROPERTY_NAME, USER_ORACLE_COLLECTION);
		activityToDataSources.put(ACTIVITY_DATA_USAGE_PROPERTY_NAME, new ArrayList(Arrays.asList(
				USER_PRINT_LOG_COLLECTION,
				USER_VPN_SESSIONS_COLLECTION,
				DATA_SOURCE_ORACLE_PROPERTY_NAME)));
		return new UserActivityConfiguration(activities, dataSourceToCollection, activityToDataSources);
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	protected String getActivityName() {
		return "data usage";
	}

	@Override
	protected String getConfigurationKey() {
		return USER_ACTIVITY_DATA_USAGE_CONFIGURATION_KEY;
	}

}