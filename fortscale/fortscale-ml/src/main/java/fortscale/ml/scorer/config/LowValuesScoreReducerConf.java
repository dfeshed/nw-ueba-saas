package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class LowValuesScoreReducerConf extends AbstractScorerConf {
	public static final String SCORER_TYPE = "low-values-score-reducer";

	private IScorerConf baseScorerConf;
	private ReductionConfigurations reductionConfigs;

	@JsonCreator
	public LowValuesScoreReducerConf(
			@JsonProperty("name") String name,
			@JsonProperty("base-scorer") IScorerConf baseScorerConf,
			@JsonProperty("reduction-configs") ReductionConfigurations reductionConfigs) {

		super(name);
		Assert.notNull(baseScorerConf);
		Assert.notNull(reductionConfigs);
		this.baseScorerConf = baseScorerConf;
		this.reductionConfigs = reductionConfigs;
	}

	@Override
	public String getFactoryName() {
		return SCORER_TYPE;
	}

	public IScorerConf getBaseScorerConf() {
		return baseScorerConf;
	}

	public ReductionConfigurations getReductionConfigs() {
		return reductionConfigs;
	}
}
