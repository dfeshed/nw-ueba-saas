package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

import java.util.List;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY, isGetterVisibility = Visibility.ANY)
public class ContinuousValuesModelScorerConf extends ModelScorerConf {
	public static final String SCORER_TYPE = "continuous-values-model-scorer";

	@JsonProperty("quad-poly-calibration-conf")
	private QuadPolyCalibrationConf quadPolyCalibrationConf;
	/*
	 * Inherited non mandatory fields:
	 * ===============================
	 * number-of-partitions-to-influence-enough
	 * use-certainty-to-calculate-score
	 * min-number-of-partitions-to-influence
	 */

	@JsonCreator
	public ContinuousValuesModelScorerConf(
			@JsonProperty("name") String name,
			@JsonProperty("model") ModelInfo modelInfo,
			@JsonProperty("additional-models") List<ModelInfo> additionalModelInfos,
			@JsonProperty("quad-poly-calibration-conf") QuadPolyCalibrationConf quadPolyCalibrationConf) {

		super(name, modelInfo, additionalModelInfos);
		Assert.notNull(quadPolyCalibrationConf);
		this.quadPolyCalibrationConf = quadPolyCalibrationConf;
	}

	@JsonIgnore
	@Override
	public String getFactoryName() {
		return SCORER_TYPE;
	}

	public QuadPolyCalibrationConf getQuadPolyCalibrationConf() {
		return quadPolyCalibrationConf;
	}
}
