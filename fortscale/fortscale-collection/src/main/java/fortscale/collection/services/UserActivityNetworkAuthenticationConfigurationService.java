package fortscale.collection.services;

import fortscale.collection.jobs.activity.UserActivityType;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userActivityNetworkAuthenticationConfigurationService")
public class UserActivityNetworkAuthenticationConfigurationService extends BaseUserActivityConfigurationService implements InitializingBean {

	private final static String USER_CRMSF_COLLECTION = "aggr_normalized_username_crmsf_daily";
	private final static String USER_SSH_COLLECTION = "aggr_normalized_username_ssh_daily";
	private final static String USER_KERBEROS_LOGINS_COLLECTION = "aggr_normalized_username_kerberos_logins_daily";
	private final static String USER_ORACLE_COLLECTION = "aggr_normalized_username_oracle_daily";
	private static final String USER_ACTIVITY_NETWORK_AUTHENTICATION_CONFIGURATION_KEY = "user_activity.network_authentication.configuration";
	private static final String ACTIVITY_NETWORK_AUTHENTICATION_PROPERTY_NAME = UserActivityType.NETWORK_AUTHENTICATION.name();
	public static final String DATA_SOURCE_CRMSF_PROPERTY_NAME = "crmsf";
	public static final String DATA_SOURCE_SSH_PROPERTY_NAME = "ssh";
	public static final String DATA_SOURCE_KERBEROS_LOGINS_PROPERTY_NAME = "kerberos_logins";
	public static final String DATA_SOURCE_ORACLE_PROPERTY_NAME = "oracle";
	private static final Logger logger = Logger.getLogger(UserActivityNetworkAuthenticationConfigurationService.class);


	@Override
	public UserActivityConfiguration createUserActivityConfiguration() {
		final Set<String> activities = new HashSet<>();
		activities.add(ACTIVITY_NETWORK_AUTHENTICATION_PROPERTY_NAME);

		final Map<String, String> dataSourceToCollection = new HashMap<>();
		dataSourceToCollection.put(DATA_SOURCE_CRMSF_PROPERTY_NAME, USER_CRMSF_COLLECTION);
		dataSourceToCollection.put(DATA_SOURCE_SSH_PROPERTY_NAME, USER_SSH_COLLECTION);
		dataSourceToCollection.put(DATA_SOURCE_KERBEROS_LOGINS_PROPERTY_NAME, USER_KERBEROS_LOGINS_COLLECTION);
		dataSourceToCollection.put(DATA_SOURCE_ORACLE_PROPERTY_NAME, USER_ORACLE_COLLECTION);


		final Map<String, List<String>> activityToDataSources = new HashMap<>();
		activityToDataSources.put(ACTIVITY_NETWORK_AUTHENTICATION_PROPERTY_NAME, new ArrayList<>(Arrays.asList(
				DATA_SOURCE_CRMSF_PROPERTY_NAME,
				DATA_SOURCE_SSH_PROPERTY_NAME,
				DATA_SOURCE_KERBEROS_LOGINS_PROPERTY_NAME,
				DATA_SOURCE_ORACLE_PROPERTY_NAME)));

		return new UserActivityConfiguration(activities, dataSourceToCollection, activityToDataSources);
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	protected String getActivityName() {
		return "network authentication";
	}

	@Override
	protected String getConfigurationKey() {
		return USER_ACTIVITY_NETWORK_AUTHENTICATION_CONFIGURATION_KEY;
	}
}
