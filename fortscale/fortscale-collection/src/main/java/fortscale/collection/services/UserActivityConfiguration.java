package fortscale.collection.services;

import fortscale.utils.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class UserActivityConfiguration {


	private Set<String> activities;
	private Map<String, String> dataSourceToCollection;
	private Map<String, List<String>> activityToDataSources;
	private static final Logger logger = Logger.getLogger(UserActivityConfiguration.class);

	public UserActivityConfiguration() {
	}

	public UserActivityConfiguration(Set<String> activities, Map<String, String> dataSourceToCollection, Map<String, List<String>> activityToDataSources) {
		this.activities = activities;
		this.dataSourceToCollection = dataSourceToCollection;
		this.activityToDataSources = activityToDataSources;
	}

	public Set<String> getActivities() {
		return activities;
	}

	public void setActivities(Set<String> activities) {
		this.activities = activities;
	}

	public Map<String, String> getDataSourceToCollection() {
		return dataSourceToCollection;
	}

	public void setDataSourceToCollection(Map<String, String> dataSourceToCollection) {
		this.dataSourceToCollection = dataSourceToCollection;
	}

	public Map<String, List<String>> getActivityToDataSources() {
		return activityToDataSources;
	}

	public void setActivityToDataSources(Map<String, List<String>> activityToDataSources) {
		this.activityToDataSources = activityToDataSources;
	}

	public List<String> getDataSources() {
		return new ArrayList<>(dataSourceToCollection.keySet());
	}

	public String getCollection(String dataSource) {
		final String collectionName = dataSourceToCollection.get(dataSource);
		if (collectionName == null) {
			final String errorMessage = String.format("Failed to get collection for data source %s", dataSource);
			logger.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}

		return collectionName;
	}
}

