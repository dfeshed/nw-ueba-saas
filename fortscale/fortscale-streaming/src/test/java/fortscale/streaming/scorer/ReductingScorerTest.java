package fortscale.streaming.scorer;

import static org.mockito.Mockito.when;

import org.apache.samza.config.ConfigException;
import org.junit.Assert;
import org.junit.Test;

public class ReductingScorerTest extends ScorerBaseTest{

	protected static final String CONST_FIELD_NAME1 = "testFieldName1";
	protected static final String CONST_FIELD_NAME2 = "testFieldName2";
	protected static final String CONST_OUTPUT_FIELD_NAME = "outputTestField";
	protected static final String CONST_SCORER_NAME1 = "ConstantRegexScorerTestScorerName1";
	protected static final String CONST_SCORER_NAME2 = "ConstantRegexScorerTestScorerName2";
	protected static final String OUTPUT_FIELD_NAME = "outputTestField";
	protected static final String SCORER_NAME = "ReductingScorerTestScorerName";
	
	
	private void configConstantScorer(String scorerName, String outputFieldName, String fieldName, String regex, Integer constant){
		when(config.get(String.format("fortscale.score.%s.scorer", scorerName))).thenReturn(ContstantRegexScorerFactory.SCORER_TYPE);
		when(config.get(String.format("fortscale.score.%s.output.field.name", scorerName))).thenReturn(outputFieldName);
		when(config.get(String.format("fortscale.score.%s.regex.fieldname", scorerName))).thenReturn(fieldName);
		when(config.get(String.format("fortscale.score.%s.regex", scorerName))).thenReturn(regex);
		when(config.getInt(String.format("fortscale.score.%s.constant", scorerName))).thenReturn(constant);
	}
	
	protected void configScorers(Integer score1, Integer score2){
		configConstantScorer(CONST_SCORER_NAME1, CONST_OUTPUT_FIELD_NAME, CONST_FIELD_NAME1, "test.*", score1);
		configConstantScorer(CONST_SCORER_NAME2, CONST_OUTPUT_FIELD_NAME, CONST_FIELD_NAME2, "unit.*", score2);
	}
	
	@SuppressWarnings("unchecked")
	protected Scorer buildScorer(String scorerName, String outputFieldName, String mainScorer, String reductingScorer, Double reducting){
		if(scorerName !=null){
			when(config.get(String.format("fortscale.score.%s.scorer", scorerName))).thenReturn(ReductingScorerFactory.SCORER_TYPE);
			if(outputFieldName != null)
				when(config.get(String.format("fortscale.score.%s.output.field.name", scorerName))).thenReturn(outputFieldName);
			if(mainScorer != null)
				when(config.get(String.format("fortscale.score.%s.main.scorer", scorerName))).thenReturn(mainScorer);
			if(reductingScorer != null)
				when(config.get(String.format("fortscale.score.%s.reducting.scorer", scorerName))).thenReturn(reductingScorer);
			if(reducting != null)
				when(config.getDouble(String.format("fortscale.score.%s.reducting.weight", scorerName))).thenReturn(reducting);
			else
				when(config.getDouble(String.format("fortscale.score.%s.reducting.weight", scorerName))).thenThrow(ConfigException.class);
		}
		return (Scorer) context.resolve(Scorer.class, scorerName);
	}
	
	@Test
	public void testBuildScorerWithMainScoreHigerThanReductingScore() throws Exception{
		int mainScore = 100;
		int reductingScore = 20;
		double reducting = 0.2d;
		configScorers(mainScore,reductingScore); 
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, CONST_SCORER_NAME1, CONST_SCORER_NAME2, reducting);
		EventMessage eventMessage = buildEventMessage(true, CONST_FIELD_NAME1, "testA1B");
		addToEventMessage(eventMessage, CONST_FIELD_NAME2, "unit908o");
		testScore(scorer, eventMessage, mainScore, reductingScore, reducting);
	}
	
	private void testScore(Scorer scorer, EventMessage eventMessage, int mainScore, int reductingScore, double reducting) throws Exception{
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		double expectedScore = mainScore;
		if(mainScore > reductingScore){
			expectedScore = reductingScore*reducting + mainScore*(1-reducting);
		}
		Assert.assertEquals(expectedScore, score.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, score.getName());
	}
	
	@Test
	public void testBuildScorerWithMainScoreLowerThanRedcutingScore() throws Exception{
		int mainScore = 10;
		int reductingScore = 20;
		double reducting = 0.2d;
		configScorers(mainScore,reductingScore); 
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, CONST_SCORER_NAME1, CONST_SCORER_NAME2, reducting);
		EventMessage eventMessage = buildEventMessage(true, CONST_FIELD_NAME1, "testA1B");
		addToEventMessage(eventMessage, CONST_FIELD_NAME2, "unit908o");
		testScore(scorer, eventMessage, mainScore, reductingScore, reducting);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoScorerName() throws Exception{
		int mainScore = 10;
		int reductingScore = 20;
		double reducting = 0.2d;
		configScorers(mainScore,reductingScore); 
		buildScorer(null, OUTPUT_FIELD_NAME, CONST_SCORER_NAME1, CONST_SCORER_NAME2, reducting);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNonOutputFieldName() throws Exception{
		int mainScore = 10;
		int reductingScore = 20;
		double reducting = 0.2d;
		configScorers(mainScore,reductingScore); 
		buildScorer(SCORER_NAME, null, CONST_SCORER_NAME1, CONST_SCORER_NAME2, reducting);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankOutputFieldName() throws Exception{
		int mainScore = 10;
		int reductingScore = 20;
		double reducting = 0.2d;
		configScorers(mainScore,reductingScore); 
		buildScorer(SCORER_NAME, "   ", CONST_SCORER_NAME1, CONST_SCORER_NAME2, reducting);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNonMainScorer() throws Exception{
		int mainScore = 10;
		int reductingScore = 20;
		double reducting = 0.2d;
		configScorers(mainScore,reductingScore); 
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, null, CONST_SCORER_NAME2, reducting);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankMainScorer() throws Exception{
		int mainScore = 10;
		int reductingScore = 20;
		double reducting = 0.2d;
		configScorers(mainScore,reductingScore); 
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, "  ", CONST_SCORER_NAME2, reducting);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNonReductingScorer() throws Exception{
		int mainScore = 10;
		int reductingScore = 20;
		double reducting = 0.2d;
		configScorers(mainScore,reductingScore); 
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, CONST_SCORER_NAME1, null, reducting);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankReductingScorer() throws Exception{
		int mainScore = 10;
		int reductingScore = 20;
		double reducting = 0.2d;
		configScorers(mainScore,reductingScore); 
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, CONST_SCORER_NAME1, "", reducting);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNonReducting() throws Exception{
		int mainScore = 10;
		int reductingScore = 20;
		configScorers(mainScore,reductingScore); 
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, CONST_SCORER_NAME1, CONST_SCORER_NAME2, null);
	}	
}
