package fortscale.streaming.scorer;

import static org.mockito.Mockito.when;

import org.apache.samza.config.ConfigException;
import org.junit.Assert;
import org.junit.Test;



public class ConstantRegexScorerTest extends ScorerBaseTest{
	private static final String FIELD_NAME = "testFieldName";
	private static final String OUTPUT_FIELD_NAME = "outputTestField";
	private static final String SCORER_NAME = "ConstantRegexScorerTestScorerName";


	private Scorer buildScorer(String scorerName, String outputFieldName, String fieldName, String regex, Integer constant){
		if(scorerName !=null){
			if(outputFieldName != null)
				when(config.get(String.format("fortscale.score.%s.output.field.name", scorerName))).thenReturn(outputFieldName);
			if(fieldName != null)
				when(config.get(String.format("fortscale.score.%s.regex.fieldname", scorerName))).thenReturn(fieldName);
			if(regex != null)
				when(config.get(String.format("fortscale.score.%s.regex", scorerName))).thenReturn(regex);
			if(constant != null)
				when(config.getInt(String.format("fortscale.score.%s.constant", scorerName))).thenReturn(constant);
			else{
				String k = String.format("fortscale.score.%s.constant", scorerName);
				when(config.getInt(k)).thenThrow(new ConfigException("Missing key " + k + "."));
			}
		}
		return scorerFactoryService.getScorer(ContstantRegexScorerFactory.SCORER_TYPE,scorerName, config, null);
	}



	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoScorerName() throws Exception{
		@SuppressWarnings("unused")
		Scorer scorer = buildScorer(null, OUTPUT_FIELD_NAME, FIELD_NAME, "test.*", 100);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNonOutputFieldName() throws Exception{
		@SuppressWarnings("unused")
		Scorer scorer = buildScorer(SCORER_NAME, null, FIELD_NAME, "test.*", 100);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankOutputFieldName() throws Exception{
		@SuppressWarnings("unused")
		Scorer scorer = buildScorer(SCORER_NAME, "  ", FIELD_NAME, "test.*", 100);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNonFieldName() throws Exception{
		@SuppressWarnings("unused")
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, null, "test.*", 100);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankFieldName() throws Exception{
		@SuppressWarnings("unused")
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, "   ", "test.*", 100);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNonRegex() throws Exception{
		@SuppressWarnings("unused")
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, null, 100);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankRegex() throws Exception{
		@SuppressWarnings("unused")
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, "", 100);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNonConstant() throws Exception{
		@SuppressWarnings("unused")
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, "test.*", null);
	}
	
	@Test
	public void testBuildScorer() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, "test.*", 100);
		Assert.assertNotNull(scorer);
	}
	
	@Test
	public void testRegexMatchScore() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, "test.*", 100);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, "testA1B");
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(100.0d, score.getScore(), 0.0);
		Assert.assertEquals(OUTPUT_FIELD_NAME,score.getName());
	}
	
	@Test
	public void testRegexNotMatchScore() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, "test.*", 100);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, "tesA1B");
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNull(score);
	}
}
