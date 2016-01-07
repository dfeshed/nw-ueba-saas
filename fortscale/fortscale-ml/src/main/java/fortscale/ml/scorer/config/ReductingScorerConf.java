package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ReductingScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "reducting-scorer";

    @JsonProperty("main-scorer")
    private IScorerConf mainScorer;
    @JsonProperty("reducting-scorer")
    private IScorerConf reductingScorer;
    @JsonProperty("reducting-weight")
    private double reductingWeight;
    @JsonProperty("reducting-zero-score-weight")
    private double reductingZeroScoreWeight;


    public ReductingScorerConf(@JsonProperty("name") String name,
                               @JsonProperty("main-scorer") IScorerConf mainScorer,
                               @JsonProperty("reducting-scorer") IScorerConf reductingScorer,
                               @JsonProperty("reducting-weight")double reductingWeight,
                               @JsonProperty("reducting-zero-score-weight") double reductingZeroScoreWeight) {
        super(name);
        this.mainScorer = mainScorer;
        this.reductingScorer = reductingScorer;
        this.reductingWeight = reductingWeight;
        this.reductingZeroScoreWeight = reductingZeroScoreWeight;
    }

    public IScorerConf getMainScorer() {
        return mainScorer;
    }

    public IScorerConf getReductingScorer() {
        return reductingScorer;
    }

    public double getReductingWeight() {
        return reductingWeight;
    }

    public double getReductingZeroScoreWeight() {
        return reductingZeroScoreWeight;
    }

}
