package fortscale.aggregation.feature.bucket.strategy;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.time.TimeRange;

import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class FeatureBucketStrategyData {
	public static final String STRATEGY_EVENT_CONTEXT_ID_FIELD = "strategyEventContextId";
	public static final String START_TIME_FIELD = "startTime";
	public static final String END_TIME_FIELD = "endTime";
	public static final String CONTEXT_MAP_FIELD = "contextMap";

	private String id;

	private String strategyEventContextId;
	private String strategyName;
	private TimeRange timeRange;
	private Map<String, String> contextMap = new HashMap<>();
	private String strategyId;

	@JsonCreator
	public FeatureBucketStrategyData(@JsonProperty("strategyEventContextId") String strategyEventContextId, @JsonProperty("strategyName") String strategyName, @JsonProperty("timeRange") TimeRange timeRange) {
		this.strategyEventContextId = strategyEventContextId;
		this.strategyName = strategyName;
		this.timeRange = timeRange;
		this.strategyId = String.format("%s_%d", strategyEventContextId, timeRange.getStart().getEpochSecond());
	}
	
	public FeatureBucketStrategyData(String strategyEventContextId, String strategyName, TimeRange timeRange, Map<String, String> contextMap) {
		this.strategyEventContextId = strategyEventContextId;
		this.strategyName = strategyName;
		this.timeRange = timeRange;
		this.contextMap = contextMap;
		this.strategyId = String.format("%s_%d", strategyEventContextId, timeRange.getStart().getEpochSecond());
	}

	public String getStrategyEventContextId() {
		return strategyEventContextId;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public TimeRange getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(TimeRange timeRange) {
		this.timeRange = timeRange;
	}

	public String getStrategyId() {

		return strategyId;
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
