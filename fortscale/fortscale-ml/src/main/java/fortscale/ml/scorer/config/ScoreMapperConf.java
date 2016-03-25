package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ScoreMapperConf extends AbstractScorerConf {
	public static final String SCORER_TYPE = "score-mapper";

	private IScorerConf baseScorerConf;
	private ScoreMappingConf scoreMappingConf;

	@JsonCreator
	public ScoreMapperConf(
			@JsonProperty("name") String name,
			@JsonProperty("base-scorer") IScorerConf baseScorerConf,
			@JsonProperty("score-mapping-conf") ScoreMappingConf scoreMappingConf) {

		super(name);
		Assert.notNull(baseScorerConf);
		Assert.notNull(scoreMappingConf);
		this.baseScorerConf = baseScorerConf;
		this.scoreMappingConf = scoreMappingConf;
	}

	@Override
	public String getFactoryName() {
		return SCORER_TYPE;
	}

	public IScorerConf getBaseScorerConf() {
		return baseScorerConf;
	}

	public ScoreMappingConf getScoreMappingConf() {
		return scoreMappingConf;
	}
}
