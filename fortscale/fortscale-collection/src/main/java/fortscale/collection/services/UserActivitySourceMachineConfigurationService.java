package fortscale.collection.services;

import fortscale.collection.jobs.activity.UserActivityType;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("userActivitySourceMachineConfigurationService")
public class UserActivitySourceMachineConfigurationService extends BaseUserActivityConfigurationService implements InitializingBean {

	private static final Logger logger = Logger.getLogger(UserActivitySourceMachineConfigurationService.class);

	private static final String USER_ACTIVITY_SOURCE_MACHINE_CONFIGURATION_KEY =
			"user_activity.source_machine.configuration";

	private Map<String, UserActivityDataSourceConfiguration> activityDataSourceConfigurationMap = new HashMap();

	@PostConstruct
	public void init(){
		activityDataSourceConfigurationMap.put("kerberos_logins", new UserActivityDataSourceConfiguration("kerberos_logins",
																									  "aggr_normalized_username_kerberos_logins_hourly",
																									  "aggregatedFeatures",
																										UserActivityType.SOURCE_MACHINE.name()));
		activityDataSourceConfigurationMap.put("kerberos_tgt", new UserActivityDataSourceConfiguration("kerberos_tgt",
				"aggr_normalized_username_kerberos_tgt_hourly",
				"aggregatedFeatures",
				UserActivityType.SOURCE_MACHINE.name()));
		activityDataSourceConfigurationMap.put("ntlm", new UserActivityDataSourceConfiguration("ntlm",
				"aggr_normalized_username_ntlm_hourly",
				"aggregatedFeatures",
				UserActivityType.SOURCE_MACHINE.name()));
		activityDataSourceConfigurationMap.put("prnlog", new UserActivityDataSourceConfiguration("prnlog",
				"aggr_normalized_username_prnlog_hourly",
				"aggregatedFeatures",
				UserActivityType.SOURCE_MACHINE.name()));
		activityDataSourceConfigurationMap.put("ssh", new UserActivityDataSourceConfiguration("ssh",
				"aggr_normalized_username_ssh_hourly",
				"aggregatedFeatures",
				UserActivityType.SOURCE_MACHINE.name()));
		activityDataSourceConfigurationMap.put("vpn", new UserActivityDataSourceConfiguration("vpn",
				"aggr_normalized_username_ssh_hourly",
				"aggregatedFeatures",
				UserActivityType.SOURCE_MACHINE.name()));
	}

	@Override
	public UserActivityConfiguration createUserActivityConfiguration() {
		final Set<String> activities = new HashSet();
		final Map<String, String> dataSourceToCollection = new HashMap();
		final Map<String, List<String>> activityToDataSources = new HashMap();
		for (UserActivityDataSourceConfiguration activity: activityDataSourceConfigurationMap.values()) {
			activities.add(activity.getPropertyName());
			dataSourceToCollection.put(activity.datasource, activity.collectionName);
			activityToDataSources.put(activity.getPropertyName(), new ArrayList(Arrays.
					asList(activity.getDatasource())));
		}
		return new UserActivityConfiguration(activities, dataSourceToCollection, activityToDataSources);
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public String getActivityName() {
		return UserActivityType.SOURCE_MACHINE.name();
	}

	@Override
	protected String getConfigurationKey() {
		return USER_ACTIVITY_SOURCE_MACHINE_CONFIGURATION_KEY;
	}

	public Map<String, UserActivityDataSourceConfiguration> getActivityDataSourceConfigurationMap() {
		return activityDataSourceConfigurationMap;
	}

	static public class UserActivityDataSourceConfiguration {

		private String datasource;
		private String collectionName;
		private String featureName;
		private String propertyName;

		public UserActivityDataSourceConfiguration(String datasource, String collectionName, String featureName,
				String propertyName) {
			this.collectionName = collectionName;
			this.featureName = featureName;
			this.propertyName = propertyName;
			this.datasource = datasource;
		}

		public String getDatasource() {
			return datasource;
		}

		public void setDatasource(String datasource) {
			this.datasource = datasource;
		}

		public String getCollectionName() {
			return collectionName;
		}

		public void setCollectionName(String collectionName) {
			this.collectionName = collectionName;
		}

		public String getFeatureName() {
			return featureName;
		}

		public void setFeatureName(String featureName) {
			this.featureName = featureName;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}

	}

}