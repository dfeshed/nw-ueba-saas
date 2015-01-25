package fortscale.streaming.scorer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import net.minidev.json.JSONObject;

import org.apache.samza.config.Config;
import org.apache.samza.config.ConfigException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/scorers-context-test.xml" })
public class ConstantRegexScorerTest {
	private static final String FIELD_NAME = "testFieldName";
	private static final String OUTPUT_FIELD_NAME = "outputTestField";
	private static final String SCORER_NAME = "ConstantRegexScorerTestScorerName";
	
	private Config config;
	
	@Autowired
	private ScorerFactoryService scorerFactoryService;
	
	@Before
    public void setUp() {
        config = mock(Config.class);
    }

	@Test
	public void thisAlwaysPasses() {
	}

	@Test
	@Ignore
	public void thisIsIgnored() {
	}
	
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
		}
		return scorerFactoryService.getScorer(ContstantRegexScorerFactory.SCORER_NAME,scorerName, config, null);
	}
	
	private EventMessage buildEventMessage(boolean isAddInput, String fieldName, String fieldValue){
		JSONObject jsonObject = null;
		if(isAddInput){
			jsonObject = new JSONObject();
			jsonObject.put(fieldName, fieldValue);
		}
		return new EventMessage(jsonObject);
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
	
	@Test
	public void testBuildScorer() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, "test.*", 100);
		Assert.assertNotNull(scorer);
	}
	
	@Test
	public void testRegexMatchScore() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, "test.*", 100);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, "testA1B");
		Double score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(100.0d, score, 0.0);
		score = eventMessage.getScore(OUTPUT_FIELD_NAME);
		Assert.assertNotNull(score);
		Assert.assertEquals(100.0d, score, 0.0);
	}
	
	@Test
	public void testRegexNotMatchScore() throws Exception{
		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, "test.*", 100);
		
		EventMessage eventMessage = buildEventMessage(true, FIELD_NAME, "tesA1B");
		Double score = scorer.calculateScore(eventMessage);
		Assert.assertNull(score);
		score = eventMessage.getScore(OUTPUT_FIELD_NAME);
		Assert.assertNull(score);
	}
}
