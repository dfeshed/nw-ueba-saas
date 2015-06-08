package fortscale.streaming.service.aggregation;

import org.springframework.data.mongodb.core.mapping.Field;
import java.util.HashMap;
import java.util.Map;

public class FeatureBucket {
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
