package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class LowValuesScoreReducerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "low-values-score-reducer";

    @JsonProperty("base-scorer")
    protected IScorerConf baseScorer;
    @JsonProperty("reduction-configuration")
    protected ReductionConfigurations reductionConfigs;

    public LowValuesScoreReducerConf(@JsonProperty("name") String name,
                                     @JsonProperty("base-scorer") IScorerConf baseScorer,
                                     @JsonProperty("reduction-configuration") ReductionConfigurations reductionConfigs) {
        super(name);
        this.baseScorer = baseScorer;
        this.reductionConfigs = reductionConfigs;
    }

    public IScorerConf getBaseScorer() {
        return baseScorer;
    }

    public ReductionConfigurations getReductionConfigs() {
        return reductionConfigs;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
