package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.FieldValueScoreLimiter;
import org.springframework.util.Assert;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class FieldValueScoreReducerScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "field-value-score-reducer";
    public static final String NULL_BASE_SCORER_ERROR_MSG = "baseScorerConf cannot be null";
    public static final String NULL_LIMITERS_ERROR_MSG = "limiters cannot be null";

    @JsonProperty("base-scorer")
    IScorerConf baseScorerConf;
    @JsonProperty("limiters")
    List<FieldValueScoreLimiter> limiters;

    @JsonCreator
    public FieldValueScoreReducerScorerConf(@JsonProperty("name") String name,
                                            @JsonProperty("base-scorer")IScorerConf baseScorerConf,
                                            @JsonProperty("limiters")List<FieldValueScoreLimiter> limiters) {
        super(name);
        Assert.notNull(baseScorerConf, NULL_BASE_SCORER_ERROR_MSG);
        Assert.notNull(limiters, NULL_LIMITERS_ERROR_MSG);

        this.limiters = limiters;
        this.baseScorerConf = baseScorerConf;

    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }

    public IScorerConf getBaseScorerConf() {
        return baseScorerConf;
    }

    public List<FieldValueScoreLimiter> getLimiters() {
        return limiters;
    }
}
