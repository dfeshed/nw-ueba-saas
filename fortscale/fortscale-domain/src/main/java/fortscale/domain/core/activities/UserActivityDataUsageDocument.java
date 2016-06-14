package fortscale.domain.core.activities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Document(collection = UserActivityDataUsageDocument.COLLECTION_NAME)
@CompoundIndexes({ @CompoundIndex(name = "user_start_time", def = "{'normalizedUsername': -1, 'startTime': 1}") })
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivityDataUsageDocument extends UserActivityDocument {

	public static final String COLLECTION_NAME = "user_activity_data_usage";
	public static final String DATA_USAGE_FIELD_NAME = "dataUsage";
	public static final String DATA_USAGE_HISTOGRAM_FIELD_NAME = "dataUsageHistogram";

	@Field(DATA_USAGE_FIELD_NAME)
	private DataUsage dataUsage = new DataUsage();

	public DataUsage getDataUsage() {
		return dataUsage;
	}

	@Override
	public Map<String, Integer> getHistogram() {
		return getDataUsage().getDataUsageHistogram();
	}

	public static class DataUsage {

		private Map<String, Integer> dataUsageHistogram = new HashMap();

		@Field(DATA_USAGE_HISTOGRAM_FIELD_NAME)
		public Map<String, Integer> getDataUsageHistogram() {
			return dataUsageHistogram;
		}

	}

}