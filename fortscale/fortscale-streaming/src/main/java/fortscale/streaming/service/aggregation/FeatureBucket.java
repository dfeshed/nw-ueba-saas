package fortscale.streaming.service.aggregation;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.HashMap;
import java.util.Map;

@CompoundIndexes({@CompoundIndex(name = "feature_bucket_index", def = FeatureBucket.COMPOUND_INDEX_DEF)})
public class FeatureBucket {
	static final String COMPOUND_INDEX_DEF =
		"{'" + FeatureBucket.STRATEGY_ID_FIELD + "': 1, " +
		"'" + FeatureBucket.USER_NAME_FIELD + "': 1, " +
		"'" + FeatureBucket.MACHINE_NAME_FIELD + "': 1, " +
		"'" + FeatureBucket.START_TIME_FIELD + "': 1, " +
		"'" + FeatureBucket.END_TIME_FIELD + "': 1}";

	public static final String STRATEGY_ID_FIELD = "strategyId";
	public static final String USER_NAME_FIELD = "userName";
	public static final String MACHINE_NAME_FIELD = "machineName";
	public static final String START_TIME_FIELD = "startTime";
	public static final String END_TIME_FIELD = "endTime";

	@Field(STRATEGY_ID_FIELD)
	private String strategyId;
	@Field(USER_NAME_FIELD)
	private String userName;
	@Field(MACHINE_NAME_FIELD)
	private String machineName;
	@Field(START_TIME_FIELD)
	private long startTime;
	@Field(END_TIME_FIELD)
	private long endTime;

	private Map<String, Object> features;

	public FeatureBucket() {
		features = new HashMap<>();
	}

	public void setStrategyId(String strategyId) {
		this.strategyId = strategyId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public void addFeature(String name, Object feature) {
		features.put(name, feature);
	}

	public Map<String, Object> getFeatures() {
		return features;
	}

	public void setFeatures(Map<String, Object> features) {
		this.features = features;
	}
}
