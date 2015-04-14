package fortscale.streaming.scorer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fortscale.ml.model.prevalance.PrevalanceModel;

public class TaskScorerAmtConfigTest extends TaskScorerConfigTest{
	
	private static final String AMT_SESSION_USER_MODEL_NAME = "amtsessionsuser";
	private static final String AMT_SESSION_USER_CONTEXT = "testuser";
	private static final String AMT_SESSION_HOST_MODEL_NAME = "amtsessionshost";
	private static final String AMT_SESSION_HOST_CONTEXT = "testhost";
	private static final String CONTEXT_USER_FIELD_NAME = "username";
	private static final String CONTEXT_HOST_FIELD_NAME = "normalized_amt_host";
	private static final String HOST_FIELD_NAME = "amt_host";
	private static final String HOST_SCORER_NAME = "hostScorer";
	private static final double HOST_SCORER_REDUCTING_WEIGHT = 0.5;
	private static final String HOST_SCORER_OUTPUT_FIELD_NAME = "amt_host_score";

	private PrevalanceModel amtsessionsuserModel;
	private PrevalanceModel amtsessionshostModel;
	
	@Before
	public void setUp(){
		super.setUp();
		
		amtsessionsuserModel = mock(PrevalanceModel.class);
		whenModelServiceGetModel(AMT_SESSION_USER_CONTEXT, AMT_SESSION_USER_MODEL_NAME, amtsessionsuserModel);
		
		amtsessionshostModel = mock(PrevalanceModel.class);
		whenModelServiceGetModel(AMT_SESSION_HOST_CONTEXT, AMT_SESSION_HOST_MODEL_NAME, amtsessionshostModel);
	}
	
	@Test
	public void testSanity() throws IOException{
		buildScorersFromTaskConfig("config/amtsessions-prevalance-stats.properties");
	}
	
	@Test
	public void testAmtHostScore() throws Exception{
		buildScorersFromTaskConfig("config/amtsessions-prevalance-stats.properties");
		
		EventMessage eventMessage = buildEventMessage(true, CONTEXT_USER_FIELD_NAME, AMT_SESSION_USER_CONTEXT);
		addToEventMessage(eventMessage, HOST_FIELD_NAME, AMT_SESSION_HOST_CONTEXT);
		
		double mainScore = 80;
		double reductingScore = 20;
		when(amtsessionsuserModel.calculateScore(featureExtractionService, eventMessage.getJsonObject(), CONTEXT_HOST_FIELD_NAME)).thenReturn(mainScore);
		when(amtsessionshostModel.calculateScore(featureExtractionService, eventMessage.getJsonObject(), CONTEXT_USER_FIELD_NAME)).thenReturn(reductingScore);
		
		Scorer hostScorer = (Scorer) context.resolve(Scorer.class, HOST_SCORER_NAME);
		
		FeatureScore score = hostScorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(HOST_SCORER_OUTPUT_FIELD_NAME,score.getName());
		Assert.assertEquals(mainScore*(1-HOST_SCORER_REDUCTING_WEIGHT) + reductingScore*HOST_SCORER_REDUCTING_WEIGHT, score.getScore(), 0.0);
	}
}
