package fortscale.collection.services.useractivity;

import fortscale.collection.jobs.activity.UserActivityType;
import fortscale.domain.core.activities.dao.DataUsageConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userActivityDataUsageConfigurationService")
public class UserActivityDataUsageConfigurationService extends BaseUserActivityConfigurationService {

	private static final Logger logger = Logger.getLogger(UserActivityDataUsageConfigurationService.class);

	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;

	private Map<String, String> collectionToHistogram;

	private static final String COLLECTION_TEMPLATE = "aggr_normalized_username_%s_daily";
	private static final String USER_ACTIVITY_DATA_USAGE_CONFIGURATION_KEY = "system.user_activity.data_usage";
	private static final String USER_ACTIVITY_DATA_USAGE_DATA_SOURCES_CONFIGURATION_KEY =
			"system.user_activity.data_usage.data_sources";
	private static final String ACTIVITY_DATA_USAGE_PROPERTY_NAME = "data_usage";
	//default values
	private static final String DATA_SOURCE_VPN_SESSION_PROPERTY_NAME = "vpn_session";
	private static final String DATA_SOURCE_PRINT_LOG_PROPERTY_NAME = "prnlog";
	private static final String DATA_SOURCE_ORACLE_PROPERTY_NAME = "oracle";
	private static final String DATA_SOURCE_DLPMAIL_PROPERTY_NAME = "dlpmail";
	private static final String DATA_SOURCE_DLPFILE_PROPERTY_NAME = "dlpfile";
	private static final String FILE_SIZE_HISTOGRAM = "sum_of_file_size";
	private static final String DB_OBJECT_HISTOGRAM = "db_object_histogram";
	private static final String DATABUCKET_HISTOGRAM = "databucket_histogram";
	private static final String ATTACHMENT_FILE_SIZE_HISTOGRAM = "attachment_file_size_histogram";
	private static final String SUM_OF_COPIED_FILES_TO_REMOVABLE_DEVICE_SIZE_HISTOGRAM = "sum_of_copied_files_to_removable_device_size";

	@Override
	public UserActivityConfiguration createUserActivityConfiguration() {
		final Set<String> activities = new HashSet<>();
		final Map<String, String> dataSourceToCollection = new HashMap<>();
		final Map<String, List<String>> activityToDataSources = new HashMap<>();
		activities.add(ACTIVITY_DATA_USAGE_PROPERTY_NAME);
		//set default values
		DataUsageConfiguration dataUsageConfiguration = new DataUsageConfiguration();
		collectionToHistogram = new HashMap<>();
		collectionToHistogram.put(DATA_SOURCE_PRINT_LOG_PROPERTY_NAME, FILE_SIZE_HISTOGRAM);
		collectionToHistogram.put(DATA_SOURCE_ORACLE_PROPERTY_NAME, DB_OBJECT_HISTOGRAM);
		collectionToHistogram.put(DATA_SOURCE_VPN_SESSION_PROPERTY_NAME, DATABUCKET_HISTOGRAM);
		collectionToHistogram.put(DATA_SOURCE_DLPMAIL_PROPERTY_NAME, ATTACHMENT_FILE_SIZE_HISTOGRAM);
		collectionToHistogram.put(DATA_SOURCE_DLPFILE_PROPERTY_NAME, SUM_OF_COPIED_FILES_TO_REMOVABLE_DEVICE_SIZE_HISTOGRAM);
		dataUsageConfiguration.setCollectionToHistogram(collectionToHistogram);
		applicationConfigurationService.insertConfigItemAsObject(USER_ACTIVITY_DATA_USAGE_DATA_SOURCES_CONFIGURATION_KEY, dataUsageConfiguration);
		
		for (Map.Entry<String, String> entry: collectionToHistogram.entrySet()) {
			dataSourceToCollection.put(entry.getKey(), String.format(COLLECTION_TEMPLATE, entry.getKey()));
		}
		activityToDataSources.put(ACTIVITY_DATA_USAGE_PROPERTY_NAME, new ArrayList<>(collectionToHistogram.keySet()));
		return new UserActivityConfiguration(activities, dataSourceToCollection, activityToDataSources);
	}

	@Override
	public String getActivityName() {
		return UserActivityType.DATA_USAGE.name();
	}

	@Override
	protected String getConfigurationKey() {
		return USER_ACTIVITY_DATA_USAGE_CONFIGURATION_KEY;
	}

	public Map<String, String> getCollectionToHistogram() {
		return collectionToHistogram;
	}

}