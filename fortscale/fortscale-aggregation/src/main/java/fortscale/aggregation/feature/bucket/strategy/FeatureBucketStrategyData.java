package fortscale.aggregation.feature.bucket.strategy;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class FeatureBucketStrategyData {
	public static final String STRATEGY_CONTEXT_ID_FIELD = "strategyContextId";
	public static final String STRATEGY_NAME_FIELD = "strategyName";
	public static final String START_TIME_FIELD = "startTime";
	public static final String END_TIME_FIELD = "endTime";

	@Id
	private String id;

	@Field(STRATEGY_CONTEXT_ID_FIELD)
	private String strategyContextId;
	@Field(STRATEGY_NAME_FIELD)
	private String strategyName;
	@Field(START_TIME_FIELD)
	private long startTime;
	@Field(END_TIME_FIELD)
	private long endTime;

	@JsonCreator
	public FeatureBucketStrategyData(@JsonProperty("strategyContextId") String strategyContextId, @JsonProperty("strategyName") String strategyName, @JsonProperty("startTime") long startTime, @JsonProperty("endTime") long endTime) {
		this.strategyContextId = strategyContextId;
		this.strategyName = strategyName;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public String getStrategyContextId() {
		return strategyContextId;
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
		return String.format("%s_%d", strategyContextId, startTime);
	}
}
