package fortscale.collection.services;

import fortscale.collection.jobs.activity.UserActivityType;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("userActivityTargetDeviceConfigurationService")
public class UserActivityTargetDeviceConfigurationService extends BaseUserActivityConfigurationService implements InitializingBean {


	private static final String USER_ACTIVITY_TARGET_DEVICE_CONFIGURATION_KEY = "user_activity.target_device.configuration";
	private static final Logger logger = Logger.getLogger(UserActivityTargetDeviceConfigurationService.class);
	private Map<String, UserActivityDataSourceConfiguration> activityDataSourceConfigurationMap = new HashMap<>();

	@PostConstruct
	public void init(){


		activityDataSourceConfigurationMap.put("crmsf", new UserActivityDataSourceConfiguration("crmsf",
				"aggr_normalized_username_crmsf_hourly",
				"aggregatedFeatures",
				UserActivityType.TARGET_DEVICE.name()));

		activityDataSourceConfigurationMap.put("kerberos_logins", new UserActivityDataSourceConfiguration("kerberos_logins",
				"aggr_normalized_username_kerberos_logins_hourly",
				"aggregatedFeatures",
				UserActivityType.TARGET_DEVICE.name()));


		activityDataSourceConfigurationMap.put("oracle", new UserActivityDataSourceConfiguration("oracle",
				"aggr_normalized_username_oracle_hourly",
				"aggregatedFeatures",
				UserActivityType.TARGET_DEVICE.name()));

		activityDataSourceConfigurationMap.put("ssh", new UserActivityDataSourceConfiguration("ssh",
				"aggr_normalized_username_ssh_hourly",
				"aggregatedFeatures",
				UserActivityType.TARGET_DEVICE.name()));

	}


	@Override
	public UserActivityConfiguration createUserActivityConfiguration() {


		final Set<String> activities = new HashSet<>();
		final Map<String, String> dataSourceToCollection = new HashMap<>();
		final Map<String, List<String>> activityToDataSources = new HashMap<>();

		for (UserActivityDataSourceConfiguration activity: activityDataSourceConfigurationMap.values()) {
			activities.add(activity.getPropertyName());
			dataSourceToCollection.put(activity.getDatasource(), activity.getCollectionName());
			activityToDataSources.put(activity.getPropertyName(), new ArrayList<>(Arrays.asList(	activity.getDatasource())));
		}


		return new UserActivityConfiguration(activities, dataSourceToCollection, activityToDataSources);
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	protected String getActivityName() {
		return UserActivityType.TARGET_DEVICE.name();
	}

	@Override
	protected String getConfigurationKey() {
		return USER_ACTIVITY_TARGET_DEVICE_CONFIGURATION_KEY;
	}

	public Map<String, UserActivityDataSourceConfiguration> getActivityDataSourceConfigurationMap() {
		return activityDataSourceConfigurationMap;
	}


}
