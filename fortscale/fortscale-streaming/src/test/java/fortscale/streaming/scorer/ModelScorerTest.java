package fortscale.streaming.scorer;

import static org.mockito.Mockito.when;

import org.apache.samza.config.ConfigException;
import org.junit.Assert;
import org.junit.Test;

import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;


public class ModelScorerTest extends ModelScorerBaseTest{
	
	private Scorer buildScorer(String scorerName, String outputFieldName, String modelName, String fieldName, String contextName) throws Exception{
		prepareConfig(ModelScorerFactory.SCORER_TYPE, scorerName, outputFieldName, modelName, fieldName, contextName);
		
		return (Scorer) context.resolve(Scorer.class, scorerName);
	}
	
	@Test 
	public void testScorerBuild() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME);
		Assert.assertNotNull(scorer);
		Assert.assertTrue(scorer instanceof ModelScorer);
	}
	
	@Test
	public void testScore() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		when(model.calculateScore(eventMessage.getJsonObject(), FIELD_NAME)).thenReturn(40d);
		
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(40.0d, score.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, score.getName());
	}
	
	@Test(expected=StreamMessageNotContainFieldException.class)
	public void testScoreWithNoContextInEventMessage() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		when(model.calculateScore(eventMessage.getJsonObject(), FIELD_NAME)).thenReturn(40d);
		
		scorer.calculateScore(eventMessage);
	}
	
	@Test
	public void testScoreWithNoModel() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(0d, score.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, score.getName());
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoScorerName() throws Exception{
		buildScorer(null, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoOutputField() throws Exception{
		buildScorer(SCORER_NAME, null, MODEL_NAME, FIELD_NAME, CONTEXT_NAME);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankOutputField() throws Exception{
		buildScorer(SCORER_NAME, "  ", MODEL_NAME, FIELD_NAME, CONTEXT_NAME);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoModelName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, null, FIELD_NAME, CONTEXT_NAME);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankModelName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, " ", FIELD_NAME, CONTEXT_NAME);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoFieldName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, null, CONTEXT_NAME);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankFieldName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, "", CONTEXT_NAME);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoContextName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, null);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankContextName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, "    ");
	}
}
