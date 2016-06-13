package fortscale.domain.core.activities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = UserActivityDataUsageDocument.COLLECTION_NAME)
@CompoundIndexes({ @CompoundIndex(name = "user_start_time", def = "{'normalizedUsername': -1, 'startTime': 1}") })
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivityDataUsageDocument extends UserActivityDocument {

	public static final String COLLECTION_NAME = "user_activity_data_usage";
	public static final String DATA_USAGE_FIELD_NAME = "dataUsage";

	@Field(DATA_USAGE_FIELD_NAME)
	private DataUsageEntry dataUsageEntry;

	@Override
	public Map<String, Integer> getHistogram() {
		//not relevant for this type of document
		return null;
	}

	public DataUsageEntry getDataUsageEntry() {
		return dataUsageEntry;
	}

	public void setDataUsageEntry(DataUsageEntry dataUsageEntry) {
		this.dataUsageEntry = dataUsageEntry;
	}

}