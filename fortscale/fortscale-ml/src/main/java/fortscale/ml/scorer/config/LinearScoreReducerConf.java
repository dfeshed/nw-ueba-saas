package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class LinearScoreReducerConf extends AbstractScorerConf {
    public static final String SCORER_TYPE = "linear-score-reducer";

    @JsonProperty("reduced-scorer")
    private IScorerConf reducedScorer;
    @JsonProperty("reducing-weight")
    private double reductingWeight;

    public LinearScoreReducerConf( @JsonProperty("name") String name,
                                   @JsonProperty("reduced-scorer") IScorerConf reducedScorer,
                                   @JsonProperty("reducing-weight") double reductingWeight) {
        super(name);
        this.reducedScorer = reducedScorer;
        this.reductingWeight = reductingWeight;
    }

    public IScorerConf getReducedScorer() {
        return reducedScorer;
    }

    public double getReductingWeight() {
        return reductingWeight;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
