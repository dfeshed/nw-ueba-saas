package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class SMARTThresholdModelBuilderConf implements IModelBuilderConf {
	public static final String SMART_THRESHOLD_MODEL_BUILDER = "smart_threshold_model_builder";

	private double minThreshold;

	@JsonCreator
	public SMARTThresholdModelBuilderConf(
			@JsonProperty("minThreshold") double minThreshold) {
		this.minThreshold = minThreshold;
	}

	@Override
	public String getFactoryName() {
		return SMART_THRESHOLD_MODEL_BUILDER;
	}

	public double getMinThreshold() {
		return minThreshold;
	}
}
