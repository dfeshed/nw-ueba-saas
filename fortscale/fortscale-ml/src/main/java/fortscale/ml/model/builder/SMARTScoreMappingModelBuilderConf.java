package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;


public class SMARTScoreMappingModelBuilderConf implements IModelBuilderConf {
	public static final String SMART_SCORE_MAPPING_MODEL_BUILDER = "smart_score_mapping_model_builder";

	private double minThreshold;
	private double minMaximalScore;

	@JsonCreator
	public SMARTScoreMappingModelBuilderConf(
			@JsonProperty("minThreshold") double minThreshold,
			@JsonProperty("minMaximalScore") double minMaximalScore) {
		Assert.isTrue(minMaximalScore >= minThreshold);
		this.minThreshold = minThreshold;
		this.minMaximalScore = minMaximalScore;
	}

	@Override
	public String getFactoryName() {
		return SMART_SCORE_MAPPING_MODEL_BUILDER;
	}

	public double getMinThreshold() {
		return minThreshold;
	}

	public double getMinMaximalScore() {
		return minMaximalScore;
	}
}
