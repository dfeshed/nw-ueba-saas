package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.common.event.EventMessage;
import fortscale.ml.scorer.params.ReductionScorerParams;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = ScorerTestsContext.class)
public class ReductionScorerTest {
    protected static final String CONST_FIELD_NAME1 = "testFieldName1";
    protected static final String CONST_FIELD_NAME2 = "testFieldName2";
    private static final String MAIN_SCORER_NAME = "MAIN-SCORER";
    private static final String REDUCTION_SCORER_NAME = "REDUCTION-SCORER";


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

    private ReductionScorer createReductionScorer(ReductionScorerParams params) {
        return createReductionScorer(params, true);
    }

    private ReductionScorer createReductionScorer(ReductionScorerParams params, boolean useConstrctorWithZeroScoreWeight) {
        if(!useConstrctorWithZeroScoreWeight) {
            return new ReductionScorer(params.getName(),
                    params.getMainScorerScore()==null?null:new SimpleScorer(params.getMainScorerScore(), MAIN_SCORER_NAME),
                    params.getReductionScorerScore()==null?null:new SimpleScorer(params.getReductionScorerScore(), REDUCTION_SCORER_NAME),
                    params.getReductionWeight());
        } else {
            return new ReductionScorer(params.getName(),
                    params.getMainScorerScore() == null ? null : new SimpleScorer(params.getMainScorerScore(),  MAIN_SCORER_NAME),
                    params.getReductionScorerScore() == null ? null : new SimpleScorer(params.getReductionScorerScore(), REDUCTION_SCORER_NAME),
                    params.getReductionWeight(),
                    params.getReductionZeroScoreWeight());
        }
    }

    public static void assertScorerParams(ReductionScorerParams params, ReductionScorer scorer) {
        assertScorerParams(params, scorer, false);
    }

    public static void assertScorerParams(ReductionScorerParams params, ReductionScorer scorer, boolean testDefaultValueZeroScoreWeight) {
        Double diff = 0.0;
        Assert.assertEquals(params.getName(), scorer.getName());
        Assert.assertEquals(params.getReductionWeight(), scorer.getReductionWeight(), diff);

        if(testDefaultValueZeroScoreWeight || params.getReductionZeroScoreWeight()==null) {
            Assert.assertEquals(ReductionScorer.REDUCTION_ZERO_SCORE_WEIGHT_DEFAULT, scorer.getReductionZeroScoreWeight(), diff);
        } else {
            Assert.assertEquals(params.getReductionZeroScoreWeight(), scorer.getReductionZeroScoreWeight(), diff);
        }
        Assert.assertEquals(params.getMainScorerScore(), ((SimpleScorer)scorer.getMainScorer()).getScore(), diff);
        Assert.assertEquals(params.getReductionScorerScore(), ((SimpleScorer)scorer.getReductionScorer()).getScore(), diff);
        Assert.assertEquals(MAIN_SCORER_NAME, ((SimpleScorer)scorer.getMainScorer()).getName());
        Assert.assertEquals(REDUCTION_SCORER_NAME, ((SimpleScorer)scorer.getReductionScorer()).getName());
    }


    private void testScore(ReductionScorer scorer, EventMessage eventMessage, ReductionScorerParams params) throws Exception{
        assertScorerParams(params, scorer, true);
        FeatureScore score = scorer.calculateScore(eventMessage, 0);
        Assert.assertNotNull(score);
        double mainScore = params.getMainScorerScore();
        double reductingScore = params.getReductionScorerScore();
        double reducting = params.getReductionWeight();
        double reductingZeroScoreWeight = params.getReductionZeroScoreWeight();

        double expectedScore = mainScore;
        if(mainScore > reductingScore){
            if(reductingScore>0){
                expectedScore = reductingScore*reducting + mainScore*(1-reducting);
            } else{
                expectedScore = reductingScore*reductingZeroScoreWeight + mainScore*(1-reductingZeroScoreWeight);
            }
        }
        Assert.assertEquals(expectedScore, score.getScore(), 0.0);
    }

    //==================================================================================================================
    // TESTING THE 5 PARAMS CONSTRUCTOR
    //==================================================================================================================

    @Test
    public void constructor_test() {
        ReductionScorerParams params = new ReductionScorerParams();
        ReductionScorer reductionScorer = createReductionScorer(params);
        assertScorerParams(params, reductionScorer);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_null_zeroScoreWeight_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionZeroScoreWeight(null);
        ReductionScorer reductionScorer = createReductionScorer(params);
        assertScorerParams(params, reductionScorer);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_zero_zeroScoreWeight_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionZeroScoreWeight(0.0);
        ReductionScorer reductionScorer = createReductionScorer(params);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_one_zeroScoreWeight_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionZeroScoreWeight(1.0);
        ReductionScorer reductionScorer = createReductionScorer(params);
    }
    @Test
    public void constructor_less_then_1_zeroScoreWeight_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionZeroScoreWeight(0.1);
        ReductionScorer reductionScorer = createReductionScorer(params);
        assertScorerParams(params, reductionScorer);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_null_reductionWeight_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionWeight(null);
        ReductionScorer reductionScorer = createReductionScorer(params);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_zero_reductionWeight_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionWeight(0.0);
        ReductionScorer reductionScorer = createReductionScorer(params);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_one_reductionWeight_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionWeight(1.0);
        ReductionScorer reductionScorer = createReductionScorer(params);
    }
    @Test
    public void constructor_less_then_1_reductionWeight_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionWeight(0.1);
        ReductionScorer reductionScorer = createReductionScorer(params);
        assertScorerParams(params, reductionScorer);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_null_mainScorer_test() {
        ReductionScorerParams params = new ReductionScorerParams().setMainScorerScore(null);
        createReductionScorer(params);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_null_reductionScorer_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionScorerScore(null);
        createReductionScorer(params);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_null_name_test() {
        ReductionScorerParams params = new ReductionScorerParams().setName(null);
        createReductionScorer(params);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_empty_name_test() {
        ReductionScorerParams params = new ReductionScorerParams().setName("");
        createReductionScorer(params);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_blank_name_test() {
        ReductionScorerParams params = new ReductionScorerParams().setName("   ");
        createReductionScorer(params);
    }

    //==================================================================================================================
    // TESTING THE 4 PARAMS CONSTRUCTOR
    //==================================================================================================================
    @Test
    public void constructor_4params_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionZeroScoreWeight(null);
        ReductionScorer reductionScorer = createReductionScorer(params, false);
        assertScorerParams(params, reductionScorer);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_4params_null_reductionWeight_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionWeight(null);
        ReductionScorer reductionScorer = createReductionScorer(params, false);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_4params_zero_reductionWeight_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionWeight(0.0);
        ReductionScorer reductionScorer = createReductionScorer(params, false);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_4params_one_reductionWeight_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionWeight(1.0);
        ReductionScorer reductionScorer = createReductionScorer(params, false);
    }
    @Test
    public void constructor_4params_less_then_1_reductionWeight_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionWeight(0.1).setReductionZeroScoreWeight(null);
        ReductionScorer reductionScorer = createReductionScorer(params, false);
        assertScorerParams(params, reductionScorer);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_4params_null_mainScorer_test() {
        ReductionScorerParams params = new ReductionScorerParams().setMainScorerScore(null).setReductionZeroScoreWeight(null);;
        createReductionScorer(params, false);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_4params_null_reductionScorer_test() {
        ReductionScorerParams params = new ReductionScorerParams().setReductionScorerScore(null).setReductionZeroScoreWeight(null);;
        createReductionScorer(params, false);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_4params_null_name_test() {
        ReductionScorerParams params = new ReductionScorerParams().setName(null).setReductionZeroScoreWeight(null);;
        createReductionScorer(params, false);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_4params_empty_name_test() {
        ReductionScorerParams params = new ReductionScorerParams().setName("").setReductionZeroScoreWeight(null);;
        createReductionScorer(params, false);
    }
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_4params_blank_name_test() {
        ReductionScorerParams params = new ReductionScorerParams().setName("   ").setReductionZeroScoreWeight(null);;
        createReductionScorer(params, false);
    }

    //==================================================================================================================
    // TESTING calculateScore
    //==================================================================================================================


    protected EventMessage buildEventMessage(String fieldName, Object fieldValue){
        JSONObject jsonObject = null;
        jsonObject = new JSONObject();
        jsonObject.put(fieldName, fieldValue);
        return new EventMessage(jsonObject);
    }

    protected void addToEventMessage(EventMessage eventMessage, String fieldName, Object fieldValue){
        JSONObject jsonObject = eventMessage.getJsonObject();
        jsonObject.put(fieldName, fieldValue);
    }

    @Test
	public void testBuildScorerWithMainScoreHigerThanReductingScore() throws Exception{
        ReductionScorerParams params = new ReductionScorerParams().setMainScorerScore(100.0).setReductionScorerScore(20.0)
                .setReductionWeight(0.2).setReductionZeroScoreWeight(ReductionScorer.REDUCTION_ZERO_SCORE_WEIGHT_DEFAULT);;
        ReductionScorer scorer = createReductionScorer(params, false);
		EventMessage eventMessage = buildEventMessage(CONST_FIELD_NAME1, "testA1B");
		addToEventMessage(eventMessage, CONST_FIELD_NAME2, "unit908o");
		testScore(scorer, eventMessage, params);
	}

	@Test
	public void testBuildScorerWithMainScoreBiggerThanZeroAndReductingScoreEqualToZero() throws Exception{
        ReductionScorerParams params = new ReductionScorerParams().setMainScorerScore(100.0).setReductionScorerScore(0.0)
                .setReductionWeight(0.2).setReductionZeroScoreWeight(ReductionScorer.REDUCTION_ZERO_SCORE_WEIGHT_DEFAULT);
        ReductionScorer scorer = createReductionScorer(params, false);
        EventMessage eventMessage = buildEventMessage(CONST_FIELD_NAME1, "testA1B");
        addToEventMessage(eventMessage, CONST_FIELD_NAME2, "unit908o");
        testScore(scorer, eventMessage, params);
    }


	@Test
	public void testBuildScorerWithMainScoreAndReductingScoreEqualToZero() throws Exception{
        ReductionScorerParams params = new ReductionScorerParams().setMainScorerScore(0.0).setReductionScorerScore(0.0)
                .setReductionWeight(0.2).setReductionZeroScoreWeight(ReductionScorer.REDUCTION_ZERO_SCORE_WEIGHT_DEFAULT);
        ReductionScorer scorer = createReductionScorer(params, false);
        EventMessage eventMessage = buildEventMessage(CONST_FIELD_NAME1, "testA1B");
        addToEventMessage(eventMessage, CONST_FIELD_NAME2, "unit908o");
        testScore(scorer, eventMessage, params);
	}


	@Test
	public void testBuildScorerWithMainScoreLowerThanRedcutingScore() throws Exception{
        ReductionScorerParams params = new ReductionScorerParams().setMainScorerScore(10.0).setReductionScorerScore(20.0)
                .setReductionWeight(0.2).setReductionZeroScoreWeight(ReductionScorer.REDUCTION_ZERO_SCORE_WEIGHT_DEFAULT);
        ReductionScorer scorer = createReductionScorer(params, false);
        EventMessage eventMessage = buildEventMessage(CONST_FIELD_NAME1, "testA1B");
        addToEventMessage(eventMessage, CONST_FIELD_NAME2, "unit908o");
        testScore(scorer, eventMessage, params);
    }

}
