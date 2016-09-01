package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;


public class SMARTScoreMappingModelBuilderConf implements IModelBuilderConf {
	public static final String SMART_SCORE_MAPPING_MODEL_BUILDER = "smart_score_mapping_model_builder";
	private static final double DEFAULT_THRESHOLD_DEFAULT_VALUE = 50;
	private static final double DEFAULT_MAXIMAL_SCORE_DEFAULT_VALUE = 100;

	private double defaultThreshold;
	private double defaultMaximalScore;
	private double minThreshold;
	private double minMaximalScore;

	@JsonCreator
	public SMARTScoreMappingModelBuilderConf(
			@JsonProperty("defaultThreshold") Double defaultThreshold,
			@JsonProperty("defaultMaximalScore") Double defaultMaximalScore,
			@JsonProperty("minThreshold") double minThreshold,
			@JsonProperty("minMaximalScore") double minMaximalScore) {
		if (defaultThreshold == null) {
			defaultThreshold = DEFAULT_THRESHOLD_DEFAULT_VALUE;
		}
		if (defaultMaximalScore == null) {
			defaultMaximalScore = DEFAULT_MAXIMAL_SCORE_DEFAULT_VALUE;
		}
		Assert.isTrue(minMaximalScore >= minThreshold);
		Assert.isTrue(defaultMaximalScore >= defaultThreshold);
		this.defaultThreshold = defaultThreshold;
		this.defaultMaximalScore = defaultMaximalScore;
		this.minThreshold = minThreshold;
		this.minMaximalScore = minMaximalScore;
	}

	@Override
	public String getFactoryName() {
		return SMART_SCORE_MAPPING_MODEL_BUILDER;
	}

	public double getDefaultThreshold() {
		return defaultThreshold;
	}

	public double getDefaultMaximalScore() {
		return defaultMaximalScore;
	}

	public double getMinThreshold() {
		return minThreshold;
	}

	public double getMinMaximalScore() {
		return minMaximalScore;
	}
}
