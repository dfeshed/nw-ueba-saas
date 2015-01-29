package fortscale.streaming.scorer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fortscale.ml.model.prevalance.PrevalanceModel;

public class TaskScorerSshConfigTest extends TaskScorerConfigTest{
	//eventscorer.scorers=dateTimeScorer,normalizedDstMachineScorer,normalizedSrcMachineScorer,authMethodScorer
	
	private static final String MODEL_NAME = "sshuser";
	private static final String CONTEXT = "testuser";
	private static final String DATE_TIME_FIELD_NAME = "date_time_unix";
	private static final String DATE_TIME_OUTPUT_FIELD_NAME = "date_timeScore";
	private static final String NORMALIZE_DST_MACHINE_FIELD_NAME = "normalized_dst_machine";
	private static final String NORMALIZE_DST_MACHINE_OUTPUT_FIELD_NAME = "normalized_dst_machine_score";
	private static final String NORMALIZE_SRC_MACHINE_FIELD_NAME = "normalized_src_machine";
	private static final String NORMALIZE_SRC_MACHINE_OUTPUT_FIELD_NAME = "normalized_src_machine_score";
	private static final String AUTH_METHOD_FIELD_NAME = "auth_method";
	private static final String AUTH_METHOD_OUTPUT_FIELD_NAME = "auth_method_score";
	private static final String CONTEXT_NAME = "normalized_username";
	
	private PrevalanceModel model;
	
	@Before
	public void setUp(){
		super.setUp();
		model = mock(PrevalanceModel.class);
		
		whenModelServiceGetModel(CONTEXT, MODEL_NAME, model);
	}
	
	

	@Test
	public void testSanity() throws IOException{
		buildScorersFromTaskConfig("config/ssh-prevalance-stats.properties");
	}
	
	@Test
	public void testDateTimeScoreHighest() throws Exception{
		List<Scorer> scorers = buildScorersFromTaskConfig("config/ssh-prevalance-stats.properties");
		Scorer scorer = scorers.get(0);
		
		EventMessage eventMessage = buildEventMessage(true, CONTEXT_NAME, CONTEXT);
//		addToEventMessage(eventMessage, DATE_TIME_FIELD_NAME, CONTEXT);
		when(model.calculateScore(eventMessage.getJsonObject(), DATE_TIME_FIELD_NAME)).thenReturn(50d);
		when(model.calculateScore(eventMessage.getJsonObject(), NORMALIZE_DST_MACHINE_FIELD_NAME)).thenReturn(40d);
		when(model.calculateScore(eventMessage.getJsonObject(), NORMALIZE_SRC_MACHINE_FIELD_NAME)).thenReturn(30d);
		when(model.calculateScore(eventMessage.getJsonObject(), AUTH_METHOD_FIELD_NAME)).thenReturn(20d);
		
		Double score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(50.0d, score, 0.0);
		score = eventMessage.getScore(DATE_TIME_OUTPUT_FIELD_NAME);
		Assert.assertNotNull(score);
		Assert.assertEquals(50.0d, score, 0.0);
		score = eventMessage.getScore(NORMALIZE_DST_MACHINE_OUTPUT_FIELD_NAME);
		Assert.assertNotNull(score);
		Assert.assertEquals(40.0d, score, 0.0);
		score = eventMessage.getScore(NORMALIZE_SRC_MACHINE_OUTPUT_FIELD_NAME);
		Assert.assertNotNull(score);
		Assert.assertEquals(30.0d, score, 0.0);
		score = eventMessage.getScore(AUTH_METHOD_OUTPUT_FIELD_NAME);
		Assert.assertNotNull(score);
		Assert.assertEquals(20.0d, score, 0.0);
	}
	
	@Test
	public void testDstMachineScoreHighest() throws Exception{
		List<Scorer> scorers = buildScorersFromTaskConfig("config/ssh-prevalance-stats.properties");
		Scorer scorer = scorers.get(0);
		
		EventMessage eventMessage = buildEventMessage(true, CONTEXT_NAME, CONTEXT);
//		addToEventMessage(eventMessage, DATE_TIME_FIELD_NAME, CONTEXT);
		when(model.calculateScore(eventMessage.getJsonObject(), DATE_TIME_FIELD_NAME)).thenReturn(50d);
		when(model.calculateScore(eventMessage.getJsonObject(), NORMALIZE_DST_MACHINE_FIELD_NAME)).thenReturn(90d);
		when(model.calculateScore(eventMessage.getJsonObject(), NORMALIZE_SRC_MACHINE_FIELD_NAME)).thenReturn(30d);
		when(model.calculateScore(eventMessage.getJsonObject(), AUTH_METHOD_FIELD_NAME)).thenReturn(20d);
		
		Double score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(90.0d, score, 0.0);
		score = eventMessage.getScore(DATE_TIME_OUTPUT_FIELD_NAME);
		Assert.assertNotNull(score);
		Assert.assertEquals(50.0d, score, 0.0);
		score = eventMessage.getScore(NORMALIZE_DST_MACHINE_OUTPUT_FIELD_NAME);
		Assert.assertNotNull(score);
		Assert.assertEquals(90.0d, score, 0.0);
		score = eventMessage.getScore(NORMALIZE_SRC_MACHINE_OUTPUT_FIELD_NAME);
		Assert.assertNotNull(score);
		Assert.assertEquals(30.0d, score, 0.0);
		score = eventMessage.getScore(AUTH_METHOD_OUTPUT_FIELD_NAME);
		Assert.assertNotNull(score);
		Assert.assertEquals(20.0d, score, 0.0);
	}
}
