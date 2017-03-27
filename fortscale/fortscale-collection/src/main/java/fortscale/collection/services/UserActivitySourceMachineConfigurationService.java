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

	private Map<String, UserActivityDataSourceConfiguration> activityDataSourceConfigurationMap = new HashMap<>();

	@PostConstruct
	public void init(){
		activityDataSourceConfigurationMap.put("prnlog", new UserActivityDataSourceConfiguration("prnlog",
				"aggr_normalized_username_prnlog_hourly",
				"aggregatedFeatures",
				UserActivityType.SOURCE_MACHINE.name()));
		activityDataSourceConfigurationMap.put("dlpmail", new UserActivityDataSourceConfiguration("dlpmail",
				"aggr_normalized_username_dlpmail_hourly",
				"aggregatedFeatures",
				UserActivityType.SOURCE_MACHINE.name()));
		activityDataSourceConfigurationMap.put("dlpfile", new UserActivityDataSourceConfiguration("dlpfile",
				"aggr_normalized_username_dlpfile_hourly",
				"aggregatedFeatures",
				UserActivityType.SOURCE_MACHINE.name()));
	}


	@Override
	public UserActivityConfiguration createUserActivityConfiguration() {
		final Set<String> activities = new HashSet<>();
		final Map<String, String> dataSourceToCollection = new HashMap<>();
		final Map<String, List<String>> activityToDataSources = new HashMap<>();
		for (UserActivityDataSourceConfiguration activity: activityDataSourceConfigurationMap.values()) {
			activities.add(activity.getPropertyName());
			dataSourceToCollection.put(activity.getDatasource(), activity.getCollectionName());
			activityToDataSources.put(activity.getPropertyName(), new ArrayList<>(Collections.singletonList(activity.getDatasource())));
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


}
