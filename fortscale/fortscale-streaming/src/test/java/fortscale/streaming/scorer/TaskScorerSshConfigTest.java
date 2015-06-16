package fortscale.streaming.scorer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fortscale.ml.model.prevalance.FieldModel;
import fortscale.ml.model.prevalance.PrevalanceModel;

public class TaskScorerSshConfigTest extends TaskScorerConfigTest{
	//eventscorer.scorers=dateTimeScorer,normalizedDstMachineScorer,normalizedSrcMachineScorer,authMethodScorer
	
	private static final String MODEL_NAME = "sshuser";
	private static final String CONTEXT = "testuser";
	private static final String DATE_TIME_FIELD_NAME = "date_time_unix";
	private static final String DATE_TIME_OUTPUT_FIELD_NAME = "date_time_score";
	private static final String NORMALIZE_DST_MACHINE_FIELD_NAME = "normalized_dst_machine";
	private static final String NORMALIZE_DST_MACHINE_OUTPUT_FIELD_NAME = "normalized_dst_machine_score";
	private static final String NORMALIZE_SRC_MACHINE_FIELD_NAME = "normalized_src_machine";
	private static final String NORMALIZE_SRC_MACHINE_OUTPUT_FIELD_NAME = "normalized_src_machine_score";
	private static final String AUTH_METHOD_FIELD_NAME = "auth_method";
	private static final String AUTH_METHOD_OUTPUT_FIELD_NAME = "auth_method_score";
	private static final String CONTEXT_NAME = "normalized_username";
	
	private PrevalanceModel model;
	private FieldModel fieldModel;
	
	@Before
	public void setUp(){
		super.setUp();
		model = mock(PrevalanceModel.class);
		fieldModel = mock(FieldModel.class);
		
		whenModelServiceGetModel(CONTEXT, MODEL_NAME, model);
	}
	
	

	@Test
	public void testSanity() throws IOException{
		buildScorersFromTaskConfig("config/ssh-prevalance-stats.properties");
	}
	
	@Test
	public void testDateTimeScoreHighest() throws Exception{
		Map<String, Double> fieldToModelScoreMap = new HashMap<String, Double>();
		Map<String, Double> fieldToScoreMap = new HashMap<String, Double>();
		fieldToModelScoreMap.put(DATE_TIME_OUTPUT_FIELD_NAME, 90d);
		fieldToScoreMap.put(DATE_TIME_OUTPUT_FIELD_NAME, 60d);
		fieldToModelScoreMap.put(NORMALIZE_DST_MACHINE_OUTPUT_FIELD_NAME, 40d);
		fieldToScoreMap.put(NORMALIZE_DST_MACHINE_OUTPUT_FIELD_NAME, 40d);
		fieldToModelScoreMap.put(NORMALIZE_SRC_MACHINE_OUTPUT_FIELD_NAME, 30d);
		fieldToScoreMap.put(NORMALIZE_SRC_MACHINE_OUTPUT_FIELD_NAME, 30d);
		fieldToModelScoreMap.put(AUTH_METHOD_OUTPUT_FIELD_NAME, 30d);
		fieldToScoreMap.put(AUTH_METHOD_OUTPUT_FIELD_NAME, 8d);

		runTest("config/ssh-prevalance-stats.properties", 79.91, fieldToModelScoreMap, fieldToScoreMap);
	}
	
	@Test
	public void testDstMachineScoreHighest() throws Exception{
		Map<String, Double> fieldToModelScoreMap = new HashMap<String, Double>();
		Map<String, Double> fieldToScoreMap = new HashMap<String, Double>();
		fieldToModelScoreMap.put(DATE_TIME_OUTPUT_FIELD_NAME, 60d);
		fieldToScoreMap.put(DATE_TIME_OUTPUT_FIELD_NAME, 40d);
		fieldToModelScoreMap.put(NORMALIZE_DST_MACHINE_OUTPUT_FIELD_NAME, 90d);
		fieldToScoreMap.put(NORMALIZE_DST_MACHINE_OUTPUT_FIELD_NAME, 90d);
		fieldToModelScoreMap.put(NORMALIZE_SRC_MACHINE_OUTPUT_FIELD_NAME, 30d);
		fieldToScoreMap.put(NORMALIZE_SRC_MACHINE_OUTPUT_FIELD_NAME, 30d);
		fieldToModelScoreMap.put(AUTH_METHOD_OUTPUT_FIELD_NAME, 15d);
		fieldToScoreMap.put(AUTH_METHOD_OUTPUT_FIELD_NAME, 4d);

		runTest("config/ssh-prevalance-stats.properties", 88.71d, fieldToModelScoreMap, fieldToScoreMap);
	}
	
	private void runTest(String configFilePath, Double eventScore, Map<String, Double> fieldToModelScoreMap, Map<String, Double> fieldToScoreMap) throws Exception{
		Map<String, Scorer> scorers = buildScorersFromTaskConfig(configFilePath);
		Scorer scorer = scorers.values().iterator().next();
		
		EventMessage eventMessage = buildEventMessage(true, CONTEXT_NAME, CONTEXT);
		
		when(fieldModel.getNumOfSamples()).thenReturn(20l);
		when(model.calculateScore(featureExtractionService, eventMessage.getJsonObject(), DATE_TIME_FIELD_NAME)).thenReturn(fieldToModelScoreMap.get(DATE_TIME_OUTPUT_FIELD_NAME));
		when(model.getFieldModel(DATE_TIME_FIELD_NAME)).thenReturn(fieldModel);
		when(model.calculateScore(featureExtractionService, eventMessage.getJsonObject(), NORMALIZE_DST_MACHINE_FIELD_NAME)).thenReturn(fieldToModelScoreMap.get(NORMALIZE_DST_MACHINE_OUTPUT_FIELD_NAME));
		when(model.calculateScore(featureExtractionService, eventMessage.getJsonObject(), NORMALIZE_SRC_MACHINE_FIELD_NAME)).thenReturn(fieldToModelScoreMap.get(NORMALIZE_SRC_MACHINE_OUTPUT_FIELD_NAME));
		when(model.calculateScore(featureExtractionService, eventMessage.getJsonObject(), AUTH_METHOD_FIELD_NAME)).thenReturn(fieldToModelScoreMap.get(AUTH_METHOD_OUTPUT_FIELD_NAME));
		when(model.getFieldModel(AUTH_METHOD_FIELD_NAME)).thenReturn(fieldModel);
		
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(eventScore, score.getScore(), 0.01);
		Assert.assertEquals(fieldToScoreMap.size(), score.getFeatureScores().size());
		for(FeatureScore featureScore: score.getFeatureScores()){
			Assert.assertEquals(fieldToScoreMap.get(featureScore.getName()), featureScore.getScore(), 0.0);
		}
	}
}
