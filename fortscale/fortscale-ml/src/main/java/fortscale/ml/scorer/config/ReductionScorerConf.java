package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.ReductionScorer;
import org.eclipse.jdt.internal.core.Assert;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY)
public class ReductionScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "reduction-scorer";

    @JsonProperty("main-scorer")
    private IScorerConf mainScorerConf;
    @JsonProperty("reduction-scorer")
    private IScorerConf reductionScorerConf;
    @JsonProperty("reduction-weight")
    private double reductionWeight;
    @JsonProperty("reduction-zero-score-weight")
    private double reductionZeroScoreWeight = ReductionScorer.REDUCTION_ZERO_SCORE_WEIGHT_DEFAULT;

    @JsonCreator
    public ReductionScorerConf(@JsonProperty("name") String name,
                               @JsonProperty("main-scorer") IScorerConf mainScorerConf,
                               @JsonProperty("reduction-scorer") IScorerConf reductingScorerConf,
                               @JsonProperty("reduction-weight")double reductionWeight) {
        super(name);
        Assert.isNotNull(mainScorerConf, "mainScorerConf must not be null");
        Assert.isNotNull(reductingScorerConf, "reductingScorerConf must not be null");
        Assert.isTrue(reductionWeight >0 && reductionWeight < 1.0,String.format("reductionWeight (%f) must be > 0 and < 1.0", reductionWeight));

        this.mainScorerConf = mainScorerConf;
        this.reductionScorerConf = reductingScorerConf;
        this.reductionWeight = reductionWeight;
    }

    public ReductionScorerConf setReductionZeroScoreWeight(double reductionZeroScoreWeight) {
        Assert.isTrue(reductionZeroScoreWeight >0 && reductionZeroScoreWeight < 1.0, String.format("reductionZeroScoreWeight (%f) must be > 0 and < 1.0", reductionZeroScoreWeight));
        this.reductionZeroScoreWeight = reductionZeroScoreWeight;
        return this;
    }

    public IScorerConf getMainScorerConf() {
        return mainScorerConf;
    }

    public IScorerConf getReductionScorerConf() {
        return reductionScorerConf;
    }

    public double getReductionWeight() {
        return reductionWeight;
    }

    public double getReductionZeroScoreWeight() {
        return reductionZeroScoreWeight;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
