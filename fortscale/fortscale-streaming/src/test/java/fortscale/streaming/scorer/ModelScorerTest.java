package fortscale.streaming.scorer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.samza.config.ConfigException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.ModelService;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;


public class ModelScorerTest extends ScorerBaseTest{
	private static final String FIELD_NAME = "testFieldName";
	private static final String FIELD_VALUE = "testFieldValue";
	private static final String OUTPUT_FIELD_NAME = "outputTestField";
	private static final String SCORER_NAME = "ModelScorerTestScorerName";
	private static final String MODEL_NAME = "testModelName";
	private static final String CONTEXT_NAME = "testContextName";
	private static final String CONTEXT = "testuser";
	
	private ScorerContext context;
	private ModelService modelService;
	private PrevalanceModel model;
	
	@Before
	public void setUp(){
		super.setUp();
		context = new ScorerContext(config);
		modelService = mock(ModelService.class);
		model = mock(PrevalanceModel.class);
		try {
			when(modelService.getModel(CONTEXT, MODEL_NAME)).thenReturn(model);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		context.setBean("modelService", modelService);
	}
	
	private Scorer buildScorer(String scorerName, String outputFieldName, String modelName, String fieldName, String contextName) throws Exception{
		if(scorerName !=null){
			when(config.get(String.format("fortscale.score.%s.scorer", scorerName))).thenReturn(ModelScorerFactory.SCORER_TYPE);
			if(modelName != null){
				when(config.get(String.format("fortscale.score.%s.model.name", scorerName))).thenReturn(modelName);
				if(fieldName != null){
					when(config.get(String.format("fortscale.score.%s.%s.fieldname", scorerName, modelName))).thenReturn(fieldName);
				}
				if(contextName != null){
					when(config.get(String.format("fortscale.score.%s.%s.context.fieldname", scorerName, modelName))).thenReturn(contextName);
				}
			}
			if(outputFieldName != null)
				when(config.get(String.format("fortscale.score.%s.output.field.name", scorerName))).thenReturn(outputFieldName);
		}
		
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
		
		Double score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(40.0d, score, 0.0);
		score = eventMessage.getScore(OUTPUT_FIELD_NAME);
		Assert.assertNotNull(score);
		Assert.assertEquals(40.0d, score, 0.0);
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
		
		Double score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(0d, score, 0.0);
		score = eventMessage.getScore(OUTPUT_FIELD_NAME);
		Assert.assertNotNull(score);
		Assert.assertEquals(0d, score, 0.0);
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
