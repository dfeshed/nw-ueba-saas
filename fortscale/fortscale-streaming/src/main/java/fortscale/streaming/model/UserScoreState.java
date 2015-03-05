package fortscale.streaming.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * State for each user to computer user score on each data source. Holds top events data structure for
 * each type of data source.
 */
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, isGetterVisibility= JsonAutoDetect.Visibility.NONE, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class UserScoreState {

	private String username;
	private Map<String,UserTopEvents> dataSourcesMap;

	public UserScoreState(@JsonProperty("username") String username) {
		this.username = username;
		this.dataSourcesMap = new HashMap<>();
	}

	public boolean containsDataSource(String dataSource) {
		return dataSourcesMap.containsKey(dataSource);
	}

	public UserTopEvents getUserTopEvents(String dataSource) {
		if (!dataSourcesMap.containsKey(dataSource)) {
			UserTopEvents topEvents = new UserTopEvents(dataSource);
			dataSourcesMap.put(dataSource, topEvents);
		}
		return dataSourcesMap.get(dataSource);
	}

	public Iterable<UserTopEvents> getUserTopEvents() {
		return dataSourcesMap.values();
	}

}
