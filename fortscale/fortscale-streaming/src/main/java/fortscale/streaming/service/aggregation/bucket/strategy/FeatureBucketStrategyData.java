package fortscale.streaming.service.aggregation.bucket.strategy;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class FeatureBucketStrategyData {
	private String strategyContextId;
	private String strategyName;
	private long startTime;
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
