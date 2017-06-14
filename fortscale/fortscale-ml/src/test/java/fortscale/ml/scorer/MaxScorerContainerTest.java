package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.params.MaxScorerContainerParams;
import fortscale.ml.scorer.record.JsonAdeRecord;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import presidio.ade.domain.record.AdeRecord;

import java.time.Instant;

public class MaxScorerContainerTest {
    private MaxScorerContainer createMaxScorerContainer(MaxScorerContainerParams params) {
        return new MaxScorerContainer(params.getName(), params.getScorerList());
    }

    protected AdeRecord buildEventMessage(String fieldName, Object fieldValue) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(fieldName, fieldValue);
        return new JsonAdeRecord(Instant.now(), jsonObject);
    }

    public static void assertScorerParams(MaxScorerContainerParams params, MaxScorerContainer scorer) {
        Assert.assertEquals(params.getName(), scorer.getName());
        Assert.assertEquals(params.getScorerList(), scorer.getScorers());
    }


    private void testScore(MaxScorerContainer scorer, AdeRecord eventMessage, MaxScorerContainerParams params, Double expectedScore) throws Exception {
        assertScorerParams(params, scorer);
        FeatureScore score = scorer.calculateScore(eventMessage);
        Assert.assertNotNull(score);
        Assert.assertEquals(expectedScore, score.getScore(), 0.000000001);
    }

    //==================================================================================================================
    // TESTING THE CONSTRUCTOR
    //==================================================================================================================

    @Test
    public void constructor_good_test() {
        MaxScorerContainerParams params = new MaxScorerContainerParams().addScorer(new SimpleTestScorer(50.0, "first scorer"));
        MaxScorerContainer scorer = createMaxScorerContainer(params);
        assertScorerParams(params, scorer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_name_test() {
        MaxScorerContainerParams params = new MaxScorerContainerParams().addScorer(new SimpleTestScorer(50.0, "first scorer")).setName(null);
        createMaxScorerContainer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_name_test() {
        MaxScorerContainerParams params = new MaxScorerContainerParams().addScorer(new SimpleTestScorer(50.0, "first scorer")).setName("");
        createMaxScorerContainer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_blank_name_test() {
        MaxScorerContainerParams params = new MaxScorerContainerParams().addScorer(new SimpleTestScorer(50.0, "first scorer")).setName(" ");
        createMaxScorerContainer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_scorerList_test() {
        MaxScorerContainerParams params = new MaxScorerContainerParams().setScorerParamsList(null);
        createMaxScorerContainer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_scorerList_test() {
        MaxScorerContainerParams params = new MaxScorerContainerParams();
        createMaxScorerContainer(params);
    }

    //==================================================================================================================
    // TESTING calculateScore
    //==================================================================================================================


    @Test
    public void test_build_scorer_with_two_scores() throws Exception {
        MaxScorerContainerParams params = new MaxScorerContainerParams()
                .addScorer(new SimpleTestScorer(100.0, "first scorer"))
                .addScorer(new SimpleTestScorer(90.0, "second scorer"));

        MaxScorerContainer scorer = createMaxScorerContainer(params);
        testScore(scorer, buildEventMessage("field1", "value1"), params, 100.0);
    }

    @Test
    public void test_build_scorer_with_one_scorer() throws Exception {
        MaxScorerContainerParams params = new MaxScorerContainerParams()
                .addScorer(new SimpleTestScorer(90.0, "first scorer"));

        MaxScorerContainer scorer = createMaxScorerContainer(params);
        testScore(scorer, buildEventMessage("field1", "value1"), params, 90.0);
    }


    @Test
    public void test_build_scorer_with_two_scores_reversed() throws Exception {
        MaxScorerContainerParams params = new MaxScorerContainerParams()
                .addScorer(new SimpleTestScorer(90.0, "first scorer"))
                .addScorer(new SimpleTestScorer(100.0, "second scorer"));

        MaxScorerContainer scorer = createMaxScorerContainer(params);
        testScore(scorer, buildEventMessage("field1", "value1"), params, 100.0);
    }

    @Test
    public void test_build_scorer_with_three_scores_first_return_null_score() throws Exception {
        MaxScorerContainerParams params = new MaxScorerContainerParams()
                .addScorer(new SimpleTestScorer(null, "first scorer"))
                .addScorer(new SimpleTestScorer(100.0, "second scorer"))
                .addScorer(new SimpleTestScorer(50.0, "third scorer"));

        MaxScorerContainer scorer = createMaxScorerContainer(params);
        testScore(scorer, buildEventMessage("field1", "value1"), params, 100.0);
    }

    @Test
    public void test_build_scorer_with_three_scores_first_return_null_score2() throws Exception {
        MaxScorerContainerParams params = new MaxScorerContainerParams()
                .addScorer(new SimpleTestScorer(null, "first scorer"))
                .addScorer(new SimpleTestScorer(50.0, "second scorer"))
                .addScorer(new SimpleTestScorer(100.0, "third scorer"));

        MaxScorerContainer scorer = createMaxScorerContainer(params);
        testScore(scorer, buildEventMessage("field1", "value1"), params, 100.0);
    }
}
