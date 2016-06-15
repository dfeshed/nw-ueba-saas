package fortscale.collection.services;

import fortscale.collection.jobs.activity.UserActivityType;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userActivityWorkingHoursConfigurationService")
public class UserActivityWorkingHoursConfigurationService extends BaseUserActivityConfigurationService
		implements InitializingBean {

	private static final Logger logger = Logger.getLogger(UserActivityWorkingHoursConfigurationService.class);

	private static final String USER_ACTIVITY_WORKING_HOURS_CONFIGURATION_KEY =
			"user_activity.working_hours.configuration";
	private static final String ACTIVITY_WORKING_HOURS_PROPERTY_NAME = "working_hours";
	private static final String USER_CRMSF_COLLECTION = "aggr_normalized_username_crmsf_daily";
	private static final String USER_SSH_COLLECTION = "aggr_normalized_username_ssh_daily";
	private static final String USER_KERBEROS_LOGINS_COLLECTION = "aggr_normalized_username_kerberos_logins_daily";
	private static final String USER_ORACLE_COLLECTION = "aggr_normalized_username_oracle_daily";
	private static final String USER_KERBEROS_TGT_COLLECTION = "aggr_normalized_username_kerberos_tgt_daily";
	private static final String USER_GWAME_COLLECTION = "aggr_normalized_username_gwame_daily";
	private static final String USER_WAME_COLLECTION = "aggr_normalized_username_wame_daily";
	private static final String USER_PRNLOG_COLLECTION = "aggr_normalized_username_prnlog_daily";
	private static final String USER_NTLM_COLLECTION = "aggr_normalized_username_ntlm_daily";
	private static final String USER_VPN_COLLECTION = "aggr_normalized_username_vpn_daily";

	public static final String DATA_SOURCE_CRMSF_PROPERTY_NAME = "crmsf";
	public static final String DATA_SOURCE_SSH_PROPERTY_NAME = "ssh";
	public static final String DATA_SOURCE_KERBEROS_LOGINS_PROPERTY_NAME = "kerberos_logins";
	public static final String DATA_SOURCE_ORACLE_PROPERTY_NAME = "oracle";
	public static final String DATA_SOURCE_KERBEROS_TGT_PROPERTY_NAME = "kerberos_tgt";
	public static final String DATA_SOURCE_GWAME_PROPERTY_NAME = "gwame";
	public static final String DATA_SOURCE_WAME_PROPERTY_NAME = "wame";
	public static final String DATA_SOURCE_PRNLOG_PROPERTY_NAME = "prnlog";
	public static final String DATA_SOURCE_NTLM_PROPERTY_NAME = "ntlm";
	public static final String DATA_SOURCE_VPN_PROPERTY_NAME = "vpn";

	@Override
	public UserActivityConfiguration createUserActivityConfiguration() {
		final Set<String> activities = new HashSet();
		final Map<String, String> dataSourceToCollection = new HashMap();
		final Map<String, List<String>> activityToDataSources = new HashMap();
		activities.add(ACTIVITY_WORKING_HOURS_PROPERTY_NAME);
		dataSourceToCollection.put(DATA_SOURCE_CRMSF_PROPERTY_NAME, USER_CRMSF_COLLECTION);
		dataSourceToCollection.put(DATA_SOURCE_SSH_PROPERTY_NAME, USER_SSH_COLLECTION);
		dataSourceToCollection.put(DATA_SOURCE_KERBEROS_LOGINS_PROPERTY_NAME, USER_KERBEROS_LOGINS_COLLECTION);
		dataSourceToCollection.put(DATA_SOURCE_ORACLE_PROPERTY_NAME, USER_ORACLE_COLLECTION);
		dataSourceToCollection.put(DATA_SOURCE_KERBEROS_TGT_PROPERTY_NAME, USER_KERBEROS_TGT_COLLECTION);
		dataSourceToCollection.put(DATA_SOURCE_GWAME_PROPERTY_NAME, USER_GWAME_COLLECTION);
		dataSourceToCollection.put(DATA_SOURCE_WAME_PROPERTY_NAME, USER_WAME_COLLECTION);
		dataSourceToCollection.put(DATA_SOURCE_PRNLOG_PROPERTY_NAME, USER_PRNLOG_COLLECTION);
		dataSourceToCollection.put(DATA_SOURCE_NTLM_PROPERTY_NAME, USER_NTLM_COLLECTION);
		dataSourceToCollection.put(DATA_SOURCE_VPN_PROPERTY_NAME, USER_VPN_COLLECTION);
		activityToDataSources.put(ACTIVITY_WORKING_HOURS_PROPERTY_NAME, new ArrayList<>(Arrays.asList(
				DATA_SOURCE_CRMSF_PROPERTY_NAME,
				DATA_SOURCE_SSH_PROPERTY_NAME,
				DATA_SOURCE_KERBEROS_LOGINS_PROPERTY_NAME,
				DATA_SOURCE_ORACLE_PROPERTY_NAME,
				DATA_SOURCE_KERBEROS_TGT_PROPERTY_NAME,
				DATA_SOURCE_GWAME_PROPERTY_NAME,
				DATA_SOURCE_WAME_PROPERTY_NAME,
				DATA_SOURCE_PRNLOG_PROPERTY_NAME,
				DATA_SOURCE_NTLM_PROPERTY_NAME,
				DATA_SOURCE_VPN_PROPERTY_NAME)));
		return new UserActivityConfiguration(activities, dataSourceToCollection, activityToDataSources);
	}


	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public String getActivityName() {
		return UserActivityType.WORKING_HOUR.name();
	}

	@Override
	protected String getConfigurationKey() {
		return USER_ACTIVITY_WORKING_HOURS_CONFIGURATION_KEY;
	}

}