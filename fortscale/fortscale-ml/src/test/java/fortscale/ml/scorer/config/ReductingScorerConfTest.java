package fortscale.ml.scorer.config;


import fortscale.ml.scorer.ReductionScorer;
import org.junit.Assert;

public class ReductingScorerConfTest {

    /**
     * ReductingScorerConf params to ease the testing.
     * The default parameters here are intentionally different from the defaults in the conf itself.
     * Use the setters to override the specific parameter you want to test.
     */
    static class ReductionScorerConfParams {
        String name = "ReductionScorer1";
        CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams mainScorer = new CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams().setName("main scorer");
        CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams reductionScorer = new CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams().setName("reduction scorer");
        Double reductionWeight = 0.5;
        Double reductionZeroScoreWeight = 0.8;

        public String getName() {
            return name;
        }

        public ReductionScorerConfParams setName(String name) {
            this.name = name;
            return this;
        }

        public CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams getMainScorer() {
            return mainScorer;
        }

        public ReductionScorerConfParams setMainScorer(CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams mainScorer) {
            this.mainScorer = mainScorer;
            return this;
        }

        public CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams getReductionScorer() {
            return reductionScorer;
        }

        public ReductionScorerConfParams setReductionScorer(CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams reductionScorer) {
            this.reductionScorer = reductionScorer;
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

    String buildScorerConfJsonString(String name,
                                     CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams mainScorer,
                                     CategoryRarityModelScorerConfTest.CategoryRarityModelScorerConfParams reductionScorer,
                                     Double reductionWeight,
                                     Double reductionZeroScoreWeight) {

        String jName = name==null? null : String.format("\"name\":\"%s\"", name);
        String jType = "\"type\":\""+ReductionScorerConf.SCORER_TYPE+"\"";
        String jMainScorer = mainScorer==null ? null : String.format(" \"main-scorer\":%s", CategoryRarityModelScorerConfTest.buildCategoryRarityModelScorerConfJsonString(mainScorer));
        String jRedtionScorer = reductionScorer==null ? null : String.format(" \"reduction-scorer\":%s", CategoryRarityModelScorerConfTest.buildCategoryRarityModelScorerConfJsonString(reductionScorer));
        String jReductionWeight = reductionWeight == null ? null : String.format("\"reduction-weight\":%f", reductionWeight);
        String jReductionZeroScoreWeight = reductionZeroScoreWeight==null ? null : String.format("\"reduction-zero-score-weight\":%f", reductionZeroScoreWeight);

        // Building the json string and making sure that there is no redundant comma.
        String s = jMainScorer==null ? null : jMainScorer;
        s = jRedtionScorer!=null ? (s!=null?jRedtionScorer+", "+s:jRedtionScorer) : s;
        s = jReductionWeight!=null ? (s!=null?jReductionWeight+", "+s:jReductionWeight) : s;
        s = jReductionZeroScoreWeight!=null ? (s!=null?jReductionZeroScoreWeight+", "+s:jReductionZeroScoreWeight) : s;
        s = jType!=null ? (s!=null?jType+", "+s:jType) : s;
        s = jName!=null ? (s!=null?jName+", "+s:jName) : s;
        s = "{" + s + "}";

        return s;
    }

    private void assertConf(ReductionScorerConf conf,
                            ReductionScorerConfParams params) {

        double diff = 0.000000001;
        Assert.assertEquals(params.getName(), conf.getName());

        if (params.getReductionZeroScoreWeight() == null) {
            Assert.assertEquals(ReductionScorer.REDUCTION_ZERO_SCORE_WEIGHT_DEFAULT, conf.getReductionZeroScoreWeight(), diff);
        } else {
            Assert.assertEquals(params.getReductionZeroScoreWeight(), conf.getReductionZeroScoreWeight(), diff);

            Assert.assertEquals(params.getReductionWeight(), conf.getReductionWeight(), diff);


        }
    }
}
