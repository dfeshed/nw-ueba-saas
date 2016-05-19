package fortscale.ml.scorer;


import fortscale.common.event.Event;
import fortscale.common.event.EventMessage;
import fortscale.domain.core.FeatureScore;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ParetoScorerTest {

    static class SimpleScorer implements Scorer {

        private Double score;
        private String name;

        public SimpleScorer(Double score, String name) {
            this.score = score;
            this.name = name;
        }

        public String getName() {

            return name;
        }

        public Double getScore() {
            return score;
        }


        @Override
        public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
            return new FeatureScore("SimpleTestScorer", score);
        }
    }

    static class ParetoScorerParams {
        List<Scorer> scorerList = new ArrayList<>();
        double highestScoreWeight;
        String name = "pareto scorer name";

        public List<Scorer> getScorerList() {
            return scorerList;
        }

        public ParetoScorerParams setScorerList(List<Scorer> scorerList) {
            this.scorerList = scorerList;
            return this;
        }

        public Double getHighestScoreWeight() {
            return highestScoreWeight;
        }

        public ParetoScorerParams setHighestScoreWeight(Double highestScoreWeight) {
            this.highestScoreWeight = highestScoreWeight;
            return this;
        }

        public String getName() {
            return name;
        }

        public ParetoScorerParams setName(String name) {
            this.name = name;
            return this;
        }

        public ParetoScorerParams addScorer(Scorer scorer) {
            scorerList.add(scorer);
            return this;
        }
    }

    static private double delta = 0.000000001;


    private ParetoScorer createParetoScorer(ParetoScorerParams params) {
        return new ParetoScorer(params.getName(), params.getScorerList(), params.getHighestScoreWeight());
    }

    protected EventMessage buildEventMessage(String fieldName, Object fieldValue){
        JSONObject jsonObject = null;
        jsonObject = new JSONObject();
        jsonObject.put(fieldName, fieldValue);
        return new EventMessage(jsonObject);
    }

    public static void assertScorerParams(ParetoScorerParams params, ParetoScorer scorer) {
        Assert.assertEquals(params.getName(), scorer.getName());
        Assert.assertEquals(params.getScorerList(), scorer.getScorers());
        Assert.assertEquals(params.getHighestScoreWeight(), scorer.getHighestScoreWeight(), delta);
    }


    private void testScore(ParetoScorer scorer, EventMessage eventMessage, ParetoScorerParams params, Double expectedScore) throws Exception{
        assertScorerParams(params, scorer);
        FeatureScore score = scorer.calculateScore(eventMessage, 0);
        Assert.assertNotNull(score);
        Assert.assertEquals(expectedScore, score.getScore(), delta);
    }

    //==================================================================================================================
    // TESTING THE CONSTRUCTOR
    //==================================================================================================================

    @Test
    public void constructor_good_test() {
        ParetoScorerParams params = new ParetoScorerParams().addScorer(new SimpleScorer(50.0, "first scorer"));
        ParetoScorer scorer = createParetoScorer(params);
        assertScorerParams(params, scorer);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_null_name_test() {
        ParetoScorerParams params = new ParetoScorerParams().addScorer(new SimpleScorer(50.0, "first scorer")).setName(null);
        ParetoScorer scorer = createParetoScorer(params);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_empty_name_test() {
        ParetoScorerParams params = new ParetoScorerParams().addScorer(new SimpleScorer(50.0, "first scorer")).setName("");
        ParetoScorer scorer = createParetoScorer(params);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_blank_name_test() {
        ParetoScorerParams params = new ParetoScorerParams().addScorer(new SimpleScorer(50.0, "first scorer")).setName(" ");
        ParetoScorer scorer = createParetoScorer(params);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_null_scorerList_test() {
        ParetoScorerParams params = new ParetoScorerParams().setScorerList(null);
        ParetoScorer scorer = createParetoScorer(params);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_empty_scorerList_test() {
        ParetoScorerParams params = new ParetoScorerParams();
        ParetoScorer scorer = createParetoScorer(params);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_zero_hieghestScoreWeight_test() {
        ParetoScorerParams params = new ParetoScorerParams().setHighestScoreWeight(0.0);
        ParetoScorer scorer = createParetoScorer(params);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_negative_hieghestScoreWeight_zero() {
        ParetoScorerParams params = new ParetoScorerParams().setHighestScoreWeight(-0.60);
        ParetoScorer scorer = createParetoScorer(params);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_1_hieghestScoreWeight_zero() {
        ParetoScorerParams params = new ParetoScorerParams().setHighestScoreWeight(1.0);
        ParetoScorer scorer = createParetoScorer(params);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_larger_then_1_hieghestScoreWeight_zero() {
        ParetoScorerParams params = new ParetoScorerParams().setHighestScoreWeight(1.6);
        ParetoScorer scorer = createParetoScorer(params);
    }

    //==================================================================================================================
    // TESTING calculateScore
    //==================================================================================================================


    @Test
    public void test_build_scorer_with_two_scores() throws Exception {
        ParetoScorerParams params = new ParetoScorerParams()
                .addScorer(new SimpleScorer(100.0, "first scorer"))
                .addScorer(new ReductionScorerTest.SimpleScorer(90.0, "second scorer"))
                .setHighestScoreWeight(0.8);

        ParetoScorer scorer = createParetoScorer(params);
        testScore(scorer, buildEventMessage("field1", "value1"), params, 98.0);
    }

    @Test
    public void test_build_scorer_with_one_score() throws Exception {
        ParetoScorerParams params = new ParetoScorerParams()
                .addScorer(new SimpleScorer(90.0, "first scorer"))
                .setHighestScoreWeight(0.7);

        ParetoScorer scorer = createParetoScorer(params);
        testScore(scorer, buildEventMessage("field1", "value1"), params, 63.0);
    }


    @Test
    public void test_build_scorer_with_two_scores_reversed() throws Exception {
        ParetoScorerParams params = new ParetoScorerParams()
                .addScorer(new SimpleScorer(90.0, "first scorer"))
                .addScorer(new ReductionScorerTest.SimpleScorer(100.0, "second scorer"))
                .setHighestScoreWeight(0.7);

        ParetoScorer scorer = createParetoScorer(params);
        testScore(scorer, buildEventMessage("field1", "value1"), params, 97.0);
    }

}
