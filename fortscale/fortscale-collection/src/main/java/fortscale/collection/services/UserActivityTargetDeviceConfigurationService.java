package fortscale.collection.services;

import fortscale.collection.jobs.activity.UserActivityType;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service("userActivityTargetDeviceConfigurationService")
public class UserActivityTargetDeviceConfigurationService extends BaseUserActivityConfigurationService implements InitializingBean {


	private static final String USER_ACTIVITY_TARGET_DEVICE_CONFIGURATION_KEY = "user_activity.target_device.configuration";
	private static final Logger logger = Logger.getLogger(UserActivityTargetDeviceConfigurationService.class);

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
	public String getActivityName() {
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
