package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ContinuousValuesModelScorerConf extends ModelScorerConf {
	public static final String SCORER_TYPE = "continuous-values-model-scorer";

	/*
	 * Inherited non mandatory fields:
	 * ===============================
	 * number-of-samples-to-influence-enough
	 * use-certainty-to-calculate-score
	 * min-number-of-samples-to-influence
	 */

	@JsonCreator
	public ContinuousValuesModelScorerConf(
			@JsonProperty("name") String name,
			@JsonProperty("model") ModelInfo modelInfo) {

		super(name, modelInfo);
	}

	@Override
	public String getFactoryName() {
		return SCORER_TYPE;
	}
}
