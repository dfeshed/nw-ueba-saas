package fortscale.ml.scorer.config;

/**
 * ReductingScorerConf params to ease the testing.
 * The default parameters here are intentionally different from the defaults in the conf itself.
 * Use the setters to override the specific parameter you want to test.
 */
public class ReductionScorerConfParams {
    String name = "ReductionScorer1";
    CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams mainScorerConfParams = new CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams().setName("main scorer");
    CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams reductionScorerConfParams = new CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams().setName("reduction scorer");
    Double reductionWeight = 0.5;
    Double reductionZeroScoreWeight = 0.8;

    public String getName() {
        return name;
    }

    public ReductionScorerConfParams setName(String name) {
        this.name = name;
        return this;
    }

    public CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams getMainScorerConfParams() {
        return mainScorerConfParams;
    }

    public ReductionScorerConfParams setMainScorerConfParams(CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams mainScorerConfParams) {
        this.mainScorerConfParams = mainScorerConfParams;
        return this;
    }

    public CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams getReductionScorerConfParams() {
        return reductionScorerConfParams;
    }

    public ReductionScorerConfParams setReductionScorerConfParams(CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams reductionScorerConfParams) {
        this.reductionScorerConfParams = reductionScorerConfParams;
        return this;
    }

    public Double getReductionWeight() {
        return reductionWeight;
    }

    public ReductionScorerConfParams setReductionWeight(Double reductionWeight) {
        this.reductionWeight = reductionWeight;
        return this;
    }

    public Double getReductionZeroScoreWeight() {
        return reductionZeroScoreWeight;
    }

    public ReductionScorerConfParams setReductionZeroScoreWeight(Double reductionZeroScoreWeight) {
        this.reductionZeroScoreWeight = reductionZeroScoreWeight;
        return this;
    }
}
