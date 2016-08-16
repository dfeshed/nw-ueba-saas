package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;


@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class PersonalThresholdModelScorerConf extends AbstractScorerConf {
	public static final String SCORER_TYPE = "personal-threshold-model-scorer";

	private ModelInfo modelInfo;
	private IScorerConf baseScorerConf;
	private double maxRatioFromUniformThreshold;

	@JsonCreator
	public PersonalThresholdModelScorerConf(@JsonProperty("name") String name,
											@JsonProperty("model") ModelInfo modelInfo,
											@JsonProperty("base-scorer") IScorerConf baseScorerConf,
											@JsonProperty("max-ratio-from-uniform-threshold") double maxRatioFromUniformThreshold) {

		super(name);
		Assert.notNull(modelInfo);
		Assert.notNull(baseScorerConf);
		Assert.isTrue(maxRatioFromUniformThreshold > 0, "max-ratio-from-uniform-threshold must be positive.");
		this.modelInfo = modelInfo;
		this.baseScorerConf = baseScorerConf;
		this.maxRatioFromUniformThreshold = maxRatioFromUniformThreshold;
	}

	@Override
	public String getFactoryName() {
		return SCORER_TYPE;
	}

	public IScorerConf getBaseScorerConf() {
		return baseScorerConf;
	}

	public ModelInfo getModelInfo() {
		return modelInfo;
	}

	public double getMaxRatioFromUniformThreshold() {
		return maxRatioFromUniformThreshold;
	}
}
