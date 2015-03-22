package fortscale.streaming.scorer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.samza.config.ConfigException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fortscale.ml.model.prevalance.field.DiscreetValuesCalibratedModel;

public class DiscreetValuesModelScorerTest  extends ModelScorerBaseTest{
	
	private DiscreetValuesCalibratedModel calibratedModel;
	
	@Before
	public void setUp(){
		super.setUp();
		calibratedModel = mock(DiscreetValuesCalibratedModel.class);
	}
	
	protected void prepareConfig(String scorerName, String outputFieldName, String modelName, String fieldName, String contextName,
			String optionalContextReplacementFieldName,
			Integer minNumOfDiscreetValuesToInfluence, Integer enoughNumOfDiscreetValuesToInfluence){
		prepareConfig(DiscreetValuesModelScorerFactory.SCORER_TYPE, scorerName, outputFieldName, modelName, fieldName, contextName, optionalContextReplacementFieldName);
		if(scorerName !=null){
			if(minNumOfDiscreetValuesToInfluence != null)
				when(config.getInt(String.format("fortscale.score.%s.discreet.values.to.influence.min", scorerName),0)).thenReturn(minNumOfDiscreetValuesToInfluence);
			else
				when(config.getInt(String.format("fortscale.score.%s.discreet.values.to.influence.min", scorerName),0)).thenReturn(0);
			
			if(enoughNumOfDiscreetValuesToInfluence != null)
				when(config.getInt(String.format("fortscale.score.%s.discreet.values.to.influence.enough", scorerName),0)).thenReturn(enoughNumOfDiscreetValuesToInfluence);
			else
				when(config.getInt(String.format("fortscale.score.%s.discreet.values.to.influence.enough", scorerName),0)).thenReturn(0);
		}
	}
	
	private Scorer buildScorer(String scorerName, String outputFieldName, String modelName, String fieldName, String contextName,
			String optionalContextReplacementFieldName, Integer minNumOfDiscreetValuesToInfluence, Integer enoughNumOfDiscreetValuesToInfluence) throws Exception{
		prepareConfig(scorerName, outputFieldName, modelName, fieldName, contextName, optionalContextReplacementFieldName, minNumOfDiscreetValuesToInfluence, enoughNumOfDiscreetValuesToInfluence);
		
		return (Scorer) context.resolve(Scorer.class, scorerName);
	}
	
	@Test 
	public void testScorerBuild() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 2, 10);
		Assert.assertNotNull(scorer);
		Assert.assertTrue(scorer instanceof ModelScorer);
	}
	
	private void prepareModelMock(EventMessage eventMessage, double score, int numOfFeatureValues){
		when(model.calculateScore(eventMessage.getJsonObject(), FIELD_NAME)).thenReturn(score);
		when(model.getFieldModel(FIELD_NAME)).thenReturn(calibratedModel);
		when(calibratedModel.getNumOfFeatureValues()).thenReturn(numOfFeatureValues);
	}
	
	@Test
	public void testScoreAndCertaintyOfNumOfFeatureValuesBelowMin() throws Exception{
		int min = 2;
		int enough = 10;
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, min, enough);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, 40d, min-1);
		
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(40d, score.getScore(), 0.0);
		Assert.assertEquals(0d, score.getCertainty(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, score.getName());
	}
	
	@Test
	public void testScoreAndCertaintyOfNumOfFeatureValuesBelowMinAndMinGreaterThanEnough() throws Exception{
		int min = 20;
		int enough = 10;
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, min, enough);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, 40d, min-1);
		
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(40d, score.getScore(), 0.0);
		Assert.assertEquals(0d, score.getCertainty(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, score.getName());
	}
	
	@Test
	public void testScoreAndCertaintyOfNumOfFeatureValuesEqualsToEnough() throws Exception{
		int min = 2;
		int enough = 10;
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, min, enough);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, 40d, enough);
		
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(40d, score.getScore(), 0.0);
		Assert.assertEquals(1d, score.getCertainty(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, score.getName());
	}
	
	@Test
	public void testScoreAndCertaintyOfNumOfFeatureValuesEqualsToEnoughAndMinGreaterThanEnough() throws Exception{
		int min = 10;
		int enough = 2;
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, min, enough);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, 40d, enough);
		
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(40d, score.getScore(), 0.0);
		Assert.assertEquals(0d, score.getCertainty(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, score.getName());
	}
	
	@Test
	public void testScoreAndCertaintyOfNumOfFeatureValuesEqualsToMin() throws Exception{
		int min = 2;
		int enough = 10;
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, min, enough);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, 40d, min);
		
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(40d, score.getScore(), 0.0);
		double expectedCertainty = 1d/(enough-min+1);
		Assert.assertEquals(expectedCertainty, score.getCertainty(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, score.getName());
	}
	
	@Test
	public void testScoreAndCertaintyOfNumOfFeatureValuesEqualsToMinAndMinGreaterThanEnough() throws Exception{
		int min = 2;
		int enough = 1;
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, min, enough);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, 40d, min);
		
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(40d, score.getScore(), 0.0);
		double expectedCertainty = 1d;
		Assert.assertEquals(expectedCertainty, score.getCertainty(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, score.getName());
	}
	
	@Test
	public void testScoreWithNoContextInEventMessage() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null,2,10);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		when(model.calculateScore(eventMessage.getJsonObject(), FIELD_NAME)).thenReturn(40d);
		
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(0d, score.getScore(), 0.0);
		Assert.assertEquals(0d, score.getCertainty(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, score.getName());
	}
	
	@Test
	public void testScoreWithNoModel() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null,2,10);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(0d, score.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, score.getName());
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoScorerName() throws Exception{
		buildScorer(null, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null,2,10);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoOutputField() throws Exception{
		buildScorer(SCORER_NAME, null, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null,2,10);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankOutputField() throws Exception{
		buildScorer(SCORER_NAME, "  ", MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null,2,10);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoModelName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, null, FIELD_NAME, CONTEXT_NAME, null,2,10);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankModelName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, " ", FIELD_NAME, CONTEXT_NAME, null,2,10);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoFieldName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, null, CONTEXT_NAME, null,2,10);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankFieldName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, "", CONTEXT_NAME, null,2,10);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoContextName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, null, null,2,10);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankContextName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, "    ", null,2,10);
	}
}
