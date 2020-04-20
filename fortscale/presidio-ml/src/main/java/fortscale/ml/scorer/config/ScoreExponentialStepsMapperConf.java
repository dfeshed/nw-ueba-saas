package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ScoreExponentialStepsMapperConf extends AbstractScorerMapperConf {
    public static final String SCORER_TYPE = "score-exponential-steps-mapper";

    private ScoreExponentialStepsMappingConf scoreMappingConf;

    @JsonCreator
    public ScoreExponentialStepsMapperConf(
            @JsonProperty("name") String name,
            @JsonProperty("base-scorer") IScorerConf baseScorerConf,
            @JsonProperty("score-mapping-conf") ScoreExponentialStepsMappingConf scoreMappingConf) {

        super(name, baseScorerConf);
        Assert.notNull(scoreMappingConf);
        this.scoreMappingConf = scoreMappingConf;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }

    public ScoreExponentialStepsMappingConf getScoreMappingConf() {
        return scoreMappingConf;
    }
}
