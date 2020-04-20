package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.params.PriorityScorerParams;
import fortscale.ml.scorer.record.TestAdeRecord;
import org.junit.Assert;
import org.junit.Test;
import presidio.ade.domain.record.AdeRecordReader;

public class PriorityScorerContainerTest {
    private PriorityScorerContainer createPriorityScorerContainer(PriorityScorerParams params) {
        return new PriorityScorerContainer(params.getName(), params.getScorerList());
    }

    public static void assertScorerParams(PriorityScorerParams params, PriorityScorerContainer scorer) {
        Assert.assertEquals(params.getName(), scorer.getName());
        Assert.assertEquals(params.getScorerList(), scorer.getScorers());
    }


    private void testScore(
            PriorityScorerContainer scorer, AdeRecordReader adeRecordReader,
            PriorityScorerParams params, Double expectedScore) throws Exception {

        assertScorerParams(params, scorer);
        FeatureScore score = scorer.calculateScore(adeRecordReader);
        Assert.assertNotNull(score);
        Assert.assertEquals(expectedScore, score.getScore(), 0.000000001);
    }

    //==================================================================================================================
    // TESTING THE CONSTRUCTOR
    //==================================================================================================================

    @Test
    public void constructor_good_test() {
        PriorityScorerParams params = new PriorityScorerParams().addScorer(new SimpleTestScorer(50.0, "first scorer"));
        PriorityScorerContainer scorer = createPriorityScorerContainer(params);
        assertScorerParams(params, scorer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_name_test() {
        PriorityScorerParams params = new PriorityScorerParams().addScorer(new SimpleTestScorer(50.0, "first scorer")).setName(null);
        createPriorityScorerContainer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_name_test() {
        PriorityScorerParams params = new PriorityScorerParams().addScorer(new SimpleTestScorer(50.0, "first scorer")).setName("");
        createPriorityScorerContainer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_blank_name_test() {
        PriorityScorerParams params = new PriorityScorerParams().addScorer(new SimpleTestScorer(50.0, "first scorer")).setName(" ");
        createPriorityScorerContainer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_scorerList_test() {
        PriorityScorerParams params = new PriorityScorerParams().setScorerParamsList(null);
        createPriorityScorerContainer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_scorerList_test() {
        PriorityScorerParams params = new PriorityScorerParams();
        createPriorityScorerContainer(params);
    }

    //==================================================================================================================
    // TESTING calculateScore
    //==================================================================================================================


    @Test
    public void test_build_scorer_with_two_scores() throws Exception {
        PriorityScorerParams params = new PriorityScorerParams()
                .addScorer(new SimpleTestScorer(100.0, "first scorer"))
                .addScorer(new SimpleTestScorer(90.0, "second scorer"));

        PriorityScorerContainer scorer = createPriorityScorerContainer(params);
        testScore(scorer, new TestAdeRecord().setField1("value1").getAdeRecordReader(), params, 100.0);
    }

    @Test
    public void test_build_scorer_with_one_scorer() throws Exception {
        PriorityScorerParams params = new PriorityScorerParams()
                .addScorer(new SimpleTestScorer(90.0, "first scorer"));

        PriorityScorerContainer scorer = createPriorityScorerContainer(params);
        testScore(scorer, new TestAdeRecord().setField1("value1").getAdeRecordReader(), params, 90.0);
    }


    @Test
    public void test_build_scorer_with_two_scores_reversed() throws Exception {
        PriorityScorerParams params = new PriorityScorerParams()
                .addScorer(new SimpleTestScorer(90.0, "first scorer"))
                .addScorer(new SimpleTestScorer(100.0, "second scorer"));

        PriorityScorerContainer scorer = createPriorityScorerContainer(params);
        testScore(scorer, new TestAdeRecord().setField1("value1").getAdeRecordReader(), params, 90.0);
    }

    @Test
    public void test_build_scorer_with_three_scores_first_return_null_score() throws Exception {
        PriorityScorerParams params = new PriorityScorerParams()
                .addScorer(new SimpleTestScorer(null, "first scorer"))
                .addScorer(new SimpleTestScorer(100.0, "second scorer"))
                .addScorer(new SimpleTestScorer(50.0, "third scorer"));

        PriorityScorerContainer scorer = createPriorityScorerContainer(params);
        testScore(scorer, new TestAdeRecord().setField1("value1").getAdeRecordReader(), params, 100.0);
    }

    @Test
    public void test_build_scorer_with_three_scores_first_return_null_score2() throws Exception {
        PriorityScorerParams params = new PriorityScorerParams()
                .addScorer(new SimpleTestScorer(null, "first scorer"))
                .addScorer(new SimpleTestScorer(50.0, "second scorer"))
                .addScorer(new SimpleTestScorer(100.0, "third scorer"));

        PriorityScorerContainer scorer = createPriorityScorerContainer(params);
        testScore(scorer, new TestAdeRecord().setField1("value1").getAdeRecordReader(), params, 50.0);
    }
}
