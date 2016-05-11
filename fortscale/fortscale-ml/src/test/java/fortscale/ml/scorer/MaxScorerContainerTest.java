package fortscale.ml.scorer;


import fortscale.common.event.EventMessage;
import fortscale.domain.core.FeatureScore;
import fortscale.ml.scorer.params.MaxScorerContainerParams;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class MaxScorerContainerTest {

    static private double delta = 0.000000001;


    private MaxScorerContainer createMaxScorerContainer(MaxScorerContainerParams params) {
        return new MaxScorerContainer(params.getName(), params.getScorerList());
    }

    protected EventMessage buildEventMessage(String fieldName, Object fieldValue){
        JSONObject jsonObject = null;
        jsonObject = new JSONObject();
        jsonObject.put(fieldName, fieldValue);
        return new EventMessage(jsonObject);
    }

    public static void assertScorerParams(MaxScorerContainerParams params, MaxScorerContainer scorer) {
        Assert.assertEquals(params.getName(), scorer.getName());
        Assert.assertEquals(params.getScorerList(), scorer.getScorers());
    }


    private void testScore(MaxScorerContainer scorer, EventMessage eventMessage, MaxScorerContainerParams params, Double expectedScore) throws Exception{
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
        MaxScorerContainerParams params = new MaxScorerContainerParams().addScorer(new SimpleTestScorer(50.0, "first scorer"));
        MaxScorerContainer scorer = createMaxScorerContainer(params);
        assertScorerParams(params, scorer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_name_test() {
        MaxScorerContainerParams params = new MaxScorerContainerParams().addScorer(new SimpleTestScorer(50.0, "first scorer")).setName(null);
        MaxScorerContainer scorer = createMaxScorerContainer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_name_test() {
        MaxScorerContainerParams params = new MaxScorerContainerParams().addScorer(new SimpleTestScorer(50.0, "first scorer")).setName("");
        MaxScorerContainer scorer = createMaxScorerContainer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_blank_name_test() {
        MaxScorerContainerParams params = new MaxScorerContainerParams().addScorer(new SimpleTestScorer(50.0, "first scorer")).setName(" ");
        MaxScorerContainer scorer = createMaxScorerContainer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_scorerList_test() {
        MaxScorerContainerParams params = new MaxScorerContainerParams().setScorerParamsList(null);
        MaxScorerContainer scorer = createMaxScorerContainer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_scorerList_test() {
        MaxScorerContainerParams params = new MaxScorerContainerParams();
        MaxScorerContainer scorer = createMaxScorerContainer(params);
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
    }}
