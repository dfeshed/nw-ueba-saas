package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.ScoreMapping;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ScoreMapperConf extends AbstractScorerMapperConf {
	public static final String SCORER_TYPE = "score-mapper";

	private ScoreMapping.ScoreMappingConf scoreMappingConf;

	@JsonCreator
	public ScoreMapperConf(
			@JsonProperty("name") String name,
			@JsonProperty("base-scorer") IScorerConf baseScorerConf,
			@JsonProperty("score-mapping-conf") ScoreMapping.ScoreMappingConf scoreMappingConf) {

		super(name, baseScorerConf);
		Assert.notNull(scoreMappingConf);
		this.scoreMappingConf = scoreMappingConf;
	}

	@Override
	public String getFactoryName() {
		return SCORER_TYPE;
	}

	public ScoreMapping.ScoreMappingConf getScoreMappingConf() {
		return scoreMappingConf;
	}
}
