package fortscale.collection.services;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("userActivitySourceMachineConfigurationService")
public class UserActivitySourceMachineConfigurationService extends BaseUserActivityConfigurationService implements InitializingBean {

	//private static final String ACTIVITY_SOURCE_MACHINE_PROPERTY_NAME = "source_machine";

//	private final static String USER_KERBEROS_LOGINS_COLLECTION = "aggr_normalized_username_kerberos_logins_hourly";
	private static final String USER_ACTIVITY_SOURCE_MACHINE_CONFIGURATION_KEY = "user_activity.source_machine.configuration";
//	public static final String DATA_SOURCE_KERBEROS_LOGINS_PROPERTY_NAME = "kerberos_logins";

	private static final Logger logger = Logger.getLogger(UserActivitySourceMachineConfigurationService.class);

	private Map<String, ActivityDataSourceConfiguration> activityDataSourceConfigurationMap = new HashMap<>();

	@PostConstruct
	public void init(){
		activityDataSourceConfigurationMap.put("kerberos_logins", new ActivityDataSourceConfiguration("kerberos_logins",
																									  "aggr_normalized_username_kerberos_logins_hourly",
																									  "histogram",
																									  "aggregatedFeatures.normalized_src_machine_histogram"));
	}


	@Override
	public UserActivityConfiguration createUserActivityConfiguration() {


		final Set<String> activities = new HashSet<>();
		final Map<String, String> dataSourceToCollection = new HashMap<>();
		final Map<String, List<String>> activityToDataSources = new HashMap<>();

		for (ActivityDataSourceConfiguration activiy: activityDataSourceConfigurationMap.values()) {
			activities.add(activiy.getPropertyName());
			dataSourceToCollection.put(activiy.datasource, activiy.collectionName);
			activityToDataSources.put(activiy.getPropertyName(), new ArrayList<>(Arrays.asList(	activiy.getDatasource())));
		}


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
		return USER_ACTIVITY_SOURCE_MACHINE_CONFIGURATION_KEY;
	}

	public Map<String, ActivityDataSourceConfiguration> getActivityDataSourceConfigurationMap() {
		return activityDataSourceConfigurationMap;
	}

	static public class ActivityDataSourceConfiguration{

		private String datasource;
		private String collectionName;
		private String featureName;
		private String propertyName;

		public ActivityDataSourceConfiguration(String datasource, String collectionName, String featureName, String propertyName) {
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
