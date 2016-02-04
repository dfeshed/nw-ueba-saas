package fortscale.ml.scorer;

/**
 * Created by amira on 26/01/2016.
 */
class ReductionScorerParams {
    String name = "reduction-scorer";
    Double mainScorerScore = 100.0;
    Double reductionScorerScore = 50.0;
    Double reductionWeight = 0.5;
    Double reductionZeroScoreWeight = 0.8;

    public String getName() {
        return name;
    }

    public ReductionScorerParams setName(String name) {
        this.name = name;
        return this;
    }

    public Double getMainScorerScore() {
        return mainScorerScore;
    }

    public ReductionScorerParams setMainScorerScore(Double mainScorerScore) {
        this.mainScorerScore = mainScorerScore;
        return this;
    }

    public Double getReductionScorerScore() {
        return reductionScorerScore;
    }

    public ReductionScorerParams setReductionScorerScore(Double reductionScorerScore) {
        this.reductionScorerScore = reductionScorerScore;
        return this;
    }

    public Double getReductionWeight() {
        return reductionWeight;
    }

    public ReductionScorerParams setReductionWeight(Double reductionWeight) {
        this.reductionWeight = reductionWeight;
        return this;
    }

    public Double getReductionZeroScoreWeight() {
        return reductionZeroScoreWeight;
    }

    public ReductionScorerParams setReductionZeroScoreWeight(Double reductionZeroScoreWeight) {
        this.reductionZeroScoreWeight = reductionZeroScoreWeight;
        return this;
    }
}
