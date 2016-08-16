package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;


@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ModelBasedScoreMapperConf extends AbstractScorerConf {
	public static final String SCORER_TYPE = "model-based-score-mapper";

	private IScorerConf baseScorerConf;
	private ModelInfo modelInfo;

	@JsonCreator
	public ModelBasedScoreMapperConf(@JsonProperty("name") String name,
									 @JsonProperty("model") ModelInfo modelInfo,
									 @JsonProperty("base-scorer") IScorerConf baseScorerConf) {

		super(name);
		Assert.notNull(modelInfo);
		Assert.notNull(baseScorerConf);
		this.modelInfo = modelInfo;
		this.baseScorerConf = baseScorerConf;
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
}
