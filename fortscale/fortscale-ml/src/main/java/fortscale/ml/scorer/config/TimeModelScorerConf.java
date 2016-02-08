package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.ANY)
public class TimeModelScorerConf extends ModelScorerConf {
	public static final String SCORER_TYPE = "time-model-scorer";
	public static final int DEFAULT_MAX_RARE_TIMESTAMP_COUNT = 5;
	public static final int DEFAULT_MAX_NUM_OF_RARE_TIMESTAMPS = 5;

	/*
	 * Inherited non mandatory fields:
	 * ===============================
	 * number-of-samples-to-influence-enough
	 * use-certainty-to-calculate-score
	 * min-number-of-samples-to-influence
	 */
	@JsonProperty("max-rare-timestamp-count")
	private int maxRareTimestampCount = DEFAULT_MAX_RARE_TIMESTAMP_COUNT;
	@JsonProperty("max-num-of-rare-timestamps")
	private int maxNumOfRareTimestamps = DEFAULT_MAX_NUM_OF_RARE_TIMESTAMPS;

	@JsonCreator
	public TimeModelScorerConf(@JsonProperty("name") String name, @JsonProperty("model") ModelInfo modelInfo) {
		super(name, modelInfo);
	}

	@Override
	public String getFactoryName() {
		return SCORER_TYPE;
	}

	public int getMaxRareTimestampCount() {
		return maxRareTimestampCount;
	}

	public void setMaxRareTimestampCount(int maxRareTimestampCount) {
		Assert.isTrue(maxRareTimestampCount >= 0);
		this.maxRareTimestampCount = maxRareTimestampCount;
	}

	public int getMaxNumOfRareTimestamps() {
		return maxNumOfRareTimestamps;
	}

	public void setMaxNumOfRareTimestamps(int maxNumOfRareTimestamps) {
		Assert.isTrue(maxNumOfRareTimestamps >= 0);
		this.maxNumOfRareTimestamps = maxNumOfRareTimestamps;
	}
}
