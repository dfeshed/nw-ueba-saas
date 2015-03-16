package fortscale.streaming.scorer;

import static org.mockito.Mockito.when;

import org.apache.samza.config.ConfigException;
import org.junit.Assert;
import org.junit.Test;

import fortscale.ml.model.prevalance.field.ContinuousValuesModel;


public class ContinuousModelScorerTest extends ModelScorerBaseTest{
		
	@SuppressWarnings("unchecked")
	protected void prepareConfig(String scorerName, String outputFieldName, String modelName, String fieldName, String contextName,
			String optionalContextReplacementFieldName,
			Double a1, Double a2, Double plargestValue, Boolean isScoreForLargeValues, Boolean isScoreForSmallValues){
		prepareConfig(ContiuousModelScorerFactory.SCORER_TYPE, scorerName, outputFieldName, modelName, fieldName, contextName, optionalContextReplacementFieldName);
		if(scorerName !=null){
			if(a1 != null){
				when(config.getDouble(String.format(ContiuousModelScorer.A1_CONFIG_FORMAT, scorerName))).thenReturn(a1);
			} else {
				when(config.getDouble(String.format(ContiuousModelScorer.A1_CONFIG_FORMAT, scorerName))).thenThrow(ConfigException.class);
			}
			
			if(a2 != null){
				when(config.getDouble(String.format(ContiuousModelScorer.A2_CONFIG_FORMAT, scorerName))).thenReturn(a2);
			} else {
				when(config.getDouble(String.format(ContiuousModelScorer.A2_CONFIG_FORMAT, scorerName))).thenThrow(ConfigException.class);
			}
			
			if(plargestValue != null){
				when(config.getDouble(String.format(ContiuousModelScorer.LARGEST_PVALUE_CONFIG_FORMAT, scorerName))).thenReturn(plargestValue);
			} else {
				when(config.getDouble(String.format(ContiuousModelScorer.LARGEST_PVALUE_CONFIG_FORMAT, scorerName))).thenThrow(ConfigException.class);
			}
			
			if(isScoreForLargeValues != null){
				when(config.getBoolean(String.format(ContiuousModelScorer.IS_SCORE_FOR_LARGE_VALUE_CONFIG_FORMAT, scorerName), ContiuousModelScorer.DEFAULT_IS_SCORE_FOR_LARGE_VALUES)).thenReturn(isScoreForLargeValues);
			} else {
				when(config.getBoolean(String.format(ContiuousModelScorer.IS_SCORE_FOR_LARGE_VALUE_CONFIG_FORMAT, scorerName), ContiuousModelScorer.DEFAULT_IS_SCORE_FOR_LARGE_VALUES)).thenReturn(ContiuousModelScorer.DEFAULT_IS_SCORE_FOR_LARGE_VALUES);
			}
			
			if(isScoreForSmallValues != null){
				when(config.getBoolean(String.format(ContiuousModelScorer.IS_SCORE_FOR_SMALL_VALUE_CONFIG_FORMAT, scorerName), ContiuousModelScorer.DEFAULT_IS_SCORE_FOR_SMALL_VALUES)).thenReturn(isScoreForSmallValues);
			} else {
				when(config.getBoolean(String.format(ContiuousModelScorer.IS_SCORE_FOR_SMALL_VALUE_CONFIG_FORMAT, scorerName), ContiuousModelScorer.DEFAULT_IS_SCORE_FOR_SMALL_VALUES)).thenReturn(ContiuousModelScorer.DEFAULT_IS_SCORE_FOR_SMALL_VALUES);
			}
		}
	}
	
	private Scorer buildScorer(String scorerName, String outputFieldName, String modelName, String fieldName, String contextName,
			String optionalContextReplacementFieldName,
			Double a1, Double a2, Double plargestValue, Boolean isScoreForLargeValues, Boolean isScoreForSmallValues) throws Exception{
		prepareConfig(scorerName, outputFieldName, modelName, fieldName, contextName, optionalContextReplacementFieldName, 
				a1, a2, plargestValue, isScoreForLargeValues, isScoreForSmallValues);
		
		return (Scorer) context.resolve(Scorer.class, scorerName);
	}
	
	@Test 
	public void testScorerBuild() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
		Assert.assertNotNull(scorer);
		Assert.assertTrue(scorer instanceof ModelScorer);
	}

	@Test
	public void testPvalueAboveZeroAndAboveLargestAndDefaultIsScoreForLargeValues() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, 0.9+ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY);
		FeatureScore featureScore = scorer.calculateScore(eventMessage);
		
		Assert.assertNotNull(featureScore);
		Assert.assertEquals(0d, featureScore.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, featureScore.getName());
	}
	
	@Test
	public void testPvalueAboveZeroAndBelowLargestAndDefaultIsScoreForLargeValues() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, 0.01+ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY);
		FeatureScore featureScore = scorer.calculateScore(eventMessage);
		
		Assert.assertNotNull(featureScore);
		Assert.assertEquals(89d, featureScore.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, featureScore.getName());
	}
	
	@Test
	public void testPvalueAboveZeroAndForDensityEqualToZeroAndDefaultIsScoreForLargeValues() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY);
		FeatureScore featureScore = scorer.calculateScore(eventMessage);
		
		Assert.assertNotNull(featureScore);
		Assert.assertEquals(100d, featureScore.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, featureScore.getName());
	}
	
	@Test
	public void testPvalueAboveZeroAndAboveLargestAndNotScoreForLargeValues() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, false, null);
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, 0.9+ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY);
		FeatureScore featureScore = scorer.calculateScore(eventMessage);
		
		Assert.assertNotNull(featureScore);
		Assert.assertEquals(0d, featureScore.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, featureScore.getName());
	}
	
	@Test
	public void testPvalueAboveZeroAndBelowLargestAndNotIsScoreForLargeValues() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, false, null);
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, 0.01+ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY);
		FeatureScore featureScore = scorer.calculateScore(eventMessage);
		
		Assert.assertNotNull(featureScore);
		Assert.assertEquals(0d, featureScore.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, featureScore.getName());
	}
	
	@Test
	public void testPvalueAboveZeroAndForDensityEqualToZeroAndNotIsScoreForLargeValues() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, false, null);
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY);
		FeatureScore featureScore = scorer.calculateScore(eventMessage);
		
		Assert.assertNotNull(featureScore);
		Assert.assertEquals(0d, featureScore.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, featureScore.getName());
	}
	
	@Test
	public void testPvalueBelowZeroAndAboveLargestAndDefaultIsScoreForLargeValues() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, -0.9-ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY);
		FeatureScore featureScore = scorer.calculateScore(eventMessage);
		
		Assert.assertNotNull(featureScore);
		Assert.assertEquals(0d, featureScore.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, featureScore.getName());
	}
	
	@Test
	public void testPvalueBelowZeroAndBelowLargestAndDefaultIsScoreForLargeValues() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, -0.01-ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY);
		FeatureScore featureScore = scorer.calculateScore(eventMessage);
		
		Assert.assertNotNull(featureScore);
		Assert.assertEquals(89d, featureScore.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, featureScore.getName());
	}
	
	@Test
	public void testPvalueBelowZeroAndForDensityEqualToZeroAndDefaultIsScoreForLargeValues() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, -ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY);
		FeatureScore featureScore = scorer.calculateScore(eventMessage);
		
		Assert.assertNotNull(featureScore);
		Assert.assertEquals(100d, featureScore.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, featureScore.getName());
	}
	
	@Test
	public void testPvalueBelowZeroAndAboveLargestAndNotScoreForSmallValues() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, false);
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, -0.9-ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY);
		FeatureScore featureScore = scorer.calculateScore(eventMessage);
		
		Assert.assertNotNull(featureScore);
		Assert.assertEquals(0d, featureScore.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, featureScore.getName());
	}
	
	@Test
	public void testPvalueBelowZeroAndBelowLargestAndNotIsScoreForSmallValues() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, false);
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, -0.01-ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY);
		FeatureScore featureScore = scorer.calculateScore(eventMessage);
		
		Assert.assertNotNull(featureScore);
		Assert.assertEquals(0d, featureScore.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, featureScore.getName());
	}
	
	@Test
	public void testPvalueBelowZeroAndForDensityEqualToZeroAndNotIsScoreForSmallValues() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, false);
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, FIELD_VALUE);
		addToEventMessage(eventMessage, CONTEXT_NAME, CONTEXT);
		prepareModelMock(eventMessage, -ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY);
		FeatureScore featureScore = scorer.calculateScore(eventMessage);
		
		Assert.assertNotNull(featureScore);
		Assert.assertEquals(0d, featureScore.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME, featureScore.getName());
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoScorerName() throws Exception{
		buildScorer(null, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoOutputField() throws Exception{
		buildScorer(SCORER_NAME, null, MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankOutputField() throws Exception{
		buildScorer(SCORER_NAME, "  ", MODEL_NAME, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoModelName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, null, FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankModelName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, " ", FIELD_NAME, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoFieldName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, null, CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankFieldName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, "", CONTEXT_NAME, null, 35.0/3, 100.0/3, 0.2, null, null);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoContextName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, null, null, 35.0/3, 100.0/3, 0.2, null, null);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankContextName() throws Exception{
		buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, MODEL_NAME, FIELD_NAME, "    ", null, 35.0/3, 100.0/3, 0.2, null, null);
	}
}
