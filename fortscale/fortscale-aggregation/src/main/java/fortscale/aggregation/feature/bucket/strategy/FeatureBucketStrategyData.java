package fortscale.aggregation.feature.bucket.strategy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class FeatureBucketStrategyData {
	public static final String STRATEGY_EVENT_CONTEXT_ID_FIELD = "strategyEventContextId";
	public static final String STRATEGY_NAME_FIELD = "strategyName";
	public static final String START_TIME_FIELD = "startTime";
	public static final String END_TIME_FIELD = "endTime";
	public static final String CONTEXT_MAP_FIELD = "contextMap";

	@Id
	private String id;

	@Field(STRATEGY_EVENT_CONTEXT_ID_FIELD)
	private String strategyEventContextId;
	@Field(STRATEGY_NAME_FIELD)
	private String strategyName;
	@Field(START_TIME_FIELD)
	private long startTime;
	@Field(END_TIME_FIELD)
	private long endTime;
	@Field(CONTEXT_MAP_FIELD)
	private Map<String, String> contextMap = new HashMap<>();

	@JsonCreator
	public FeatureBucketStrategyData(@JsonProperty("strategyEventContextId") String strategyEventContextId, @JsonProperty("strategyName") String strategyName, @JsonProperty("startTime") long startTime, @JsonProperty("endTime") long endTime) {
		this.strategyEventContextId = strategyEventContextId;
		this.strategyName = strategyName;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public FeatureBucketStrategyData(String strategyEventContextId, String strategyName, long startTime, long endTime, Map<String, String> contextMap) {
		this.strategyEventContextId = strategyEventContextId;
		this.strategyName = strategyName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.contextMap = contextMap;
	}

	public String getStrategyEventContextId() {
		return strategyEventContextId;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getStrategyId() {
		return String.format("%s_%d", strategyEventContextId, startTime);
	}

	public Map<String, String> getContextMap() {
		return contextMap;
	}

	public void setContextMap(Map<String, String> contextMap) {
		this.contextMap = contextMap;
	}
	
	public void putContext(String key, String val){
		contextMap.put(key, val);
	}
	
	
	
	public static String getContextNameField(String contextName) {
		return String.format("%s.%s", FeatureBucketStrategyData.CONTEXT_MAP_FIELD, contextName);
	}
}
