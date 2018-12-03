package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

import java.util.List;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY, isGetterVisibility = Visibility.ANY)
public class TimeModelScorerConf extends ModelScorerConf {
	public static final String SCORER_TYPE = "time-model-scorer";
	public static final int DEFAULT_MAX_RARE_COUNT = 8;
	public static final int DEFAULT_MAX_NUM_OF_RARE_PARTITIONS = 15;
	public static final double X_WITH_VALUE_HALF_FACTOR = 0.25;
	public static final double MIN_PROBABILITY = 0.7;

	/*
	 * Inherited non mandatory fields:
	 * ===============================
	 * number-of-partitions-to-influence-enough
	 * use-certainty-to-calculate-score
	 * min-number-of-partitions-to-influence
	 */
	@JsonProperty("max-rare-count")
	private int maxRareCount = DEFAULT_MAX_RARE_COUNT;
	@JsonProperty("max-num-of-rare-partitions")
	private int maxNumOfRarePartitions = DEFAULT_MAX_NUM_OF_RARE_PARTITIONS;
	@JsonProperty("x-with-value-half-factor")
	private double xWithValueHalfFactor = X_WITH_VALUE_HALF_FACTOR;
	@JsonProperty("min-probability")
	private double minProbability = MIN_PROBABILITY;



	@JsonCreator
	public TimeModelScorerConf(@JsonProperty("name") String name,
							   @JsonProperty("additional-models") List<ModelInfo> additionalModelInfos,
							   @JsonProperty("model") ModelInfo modelInfo) {
		super(name, modelInfo, additionalModelInfos);
	}

	@JsonIgnore
	@Override
	public String getFactoryName() {
		return SCORER_TYPE;
	}

	public int getMaxRareCount() {
		return maxRareCount;
	}

	public void setMaxRareCount(int maxRareCount) {
		Assert.isTrue(maxRareCount >= 0);
		this.maxRareCount = maxRareCount;
	}

	public int getMaxNumOfRarePartitions() {
		return maxNumOfRarePartitions;
	}

	public void setMaxNumOfRarePartitions(int maxNumOfRarePartitions) {
		Assert.isTrue(maxNumOfRarePartitions >= 0);
		this.maxNumOfRarePartitions = maxNumOfRarePartitions;
	}


	public double getXWithValueHalfFactor() {
		return xWithValueHalfFactor;
	}

	public void setXWithValueHalfFactor(double xWithValueHalfFactor) {
		this.xWithValueHalfFactor = xWithValueHalfFactor;
	}

	public double getMinProbability() {
		return minProbability;
	}

	public void setMinProbability(double minProbability) {
		this.minProbability = minProbability;
	}
}
