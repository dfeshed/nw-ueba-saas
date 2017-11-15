package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.ScoreExponentialMapping;
import org.springframework.util.Assert;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ScoreExponentialMapperConf extends AbstractScorerMapperConf {
    public static final String SCORER_TYPE = "score-exponential-mapper";

    private ScoreExponentialMapping.ScoreExponentialMappingConf scoreMappingConf;

    @JsonCreator
    public ScoreExponentialMapperConf(
            @JsonProperty("name") String name,
            @JsonProperty("base-scorer") IScorerConf baseScorerConf,
            @JsonProperty("score-mapping-conf") ScoreExponentialMapping.ScoreExponentialMappingConf scoreMappingConf) {

        super(name, baseScorerConf);
        Assert.notNull(scoreMappingConf);
        this.scoreMappingConf = scoreMappingConf;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }

    public ScoreExponentialMapping.ScoreExponentialMappingConf getScoreMappingConf() {
        return scoreMappingConf;
    }
}
