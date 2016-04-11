package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY)
public class ScoreAndCertaintyMultiplierScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "score-and-certainty-multiplier-scorer";

    @JsonProperty("base-scorer")
    private IScorerConf baseScorerConf;

    @JsonCreator
    public ScoreAndCertaintyMultiplierScorerConf(@JsonProperty("name") String name,
                                                 @JsonProperty("base-scorer") IScorerConf baseScorerConf) {
        super(name);
        Assert.notNull(baseScorerConf, "baseScorerConf must not be null");
        this.baseScorerConf = baseScorerConf;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }

    public IScorerConf getBaseScorerConf() {
        return baseScorerConf;
    }
}
