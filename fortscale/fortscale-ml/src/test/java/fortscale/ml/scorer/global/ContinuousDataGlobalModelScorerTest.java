//package fortscale.streaming.scorer;
//
//import fortscale.common.event.EventMessage;
//import fortscale.ml.model.prevalance.FieldModel;
//import fortscale.ml.model.prevalance.PrevalanceModel;
//import fortscale.ml.service.ModelService;
//import fortscale.streaming.service.GlobalModelStreamTaskService;
//import org.apache.samza.config.Config;
//import org.junit.Assert;
//import org.junit.Test;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class ContinuousDataGlobalModelScorerTest {
//	private static ContinuousDataGlobalModelScorer createScorer(
//			Scorer continuousDataLocalModelScorer,
//			FieldModel popQuantilesFieldModel,
//			FieldModel medQuantilesFieldModel,
//			double qEventDown, double qEventUp,
//			double deltaQDown, double deltaQUp,
//			double increaseRate, double qStopReducing) throws Exception {
//
//		String scorerName = "scorerName";
//		String localModelScorerName = "localModelScorerName";
//		String popQuantilesFieldModelName = "popQuantilesFieldModelName";
//		String medQuantilesFieldModelName = "medQuantilesFieldModelName";
//
//		Config config = mock(Config.class);
//		ScorerContext context = mock(ScorerContext.class);
//		ModelService modelService = mock(ModelService.class);
//		PrevalanceModel prevalanceModel = mock(PrevalanceModel.class);
//
//		when(config.get(String.format("fortscale.score.%s.output.field.name", scorerName))).thenReturn("outputFieldName");
//		when(config.get(String.format("fortscale.score.%s.continuous.data.local.model.scorer", scorerName), null)).thenReturn(localModelScorerName);
//		when(config.get(String.format("fortscale.score.%s.population.quantiles.field.model.name", scorerName), null)).thenReturn(popQuantilesFieldModelName);
//		when(config.get(String.format("fortscale.score.%s.medians.quantiles.field.model.name", scorerName), null)).thenReturn(medQuantilesFieldModelName);
//
//		when(config.getDouble(String.format("fortscale.score.%s.q.event.down", scorerName))).thenReturn(qEventDown);
//		when(config.getDouble(String.format("fortscale.score.%s.q.event.up", scorerName))).thenReturn(qEventUp);
//		when(config.getDouble(String.format("fortscale.score.%s.delta.q.down", scorerName))).thenReturn(deltaQDown);
//		when(config.getDouble(String.format("fortscale.score.%s.delta.q.up", scorerName))).thenReturn(deltaQUp);
//		when(config.getDouble(String.format("fortscale.score.%s.increase.rate", scorerName))).thenReturn(increaseRate);
//		when(config.getDouble(String.format("fortscale.score.%s.q.stop.reducing", scorerName))).thenReturn(qStopReducing);
//
//		when(context.resolve(Scorer.class, localModelScorerName)).thenReturn(continuousDataLocalModelScorer);
//		when(context.resolve(ModelService.class, "modelService")).thenReturn(modelService);
//		when(modelService.modelExists(GlobalModelStreamTaskService.GLOBAL_CONTEXT_CONSTANT, GlobalModelStreamTaskService.GLOBAL_MODEL_NAME)).thenReturn(true);
//		when(modelService.getModel(GlobalModelStreamTaskService.GLOBAL_CONTEXT_CONSTANT, GlobalModelStreamTaskService.GLOBAL_MODEL_NAME)).thenReturn(prevalanceModel);
//		when(prevalanceModel.getFieldModel(popQuantilesFieldModelName)).thenReturn(popQuantilesFieldModel);
//		when(prevalanceModel.getFieldModel(medQuantilesFieldModelName)).thenReturn(medQuantilesFieldModel);
//
//		return new ContinuousDataGlobalModelScorer(scorerName, config, context);
//	}
//
//	private static void testScenario(
//			double oldScore, double qEvent, double qContext,
//			double qEventDown, double qEventUp,
//			double deltaQDown, double deltaQUp,
//			double increaseRate, double qStopReducing,
//			double expectedScore) throws Exception {
//
//		Scorer continuousDataLocalModelScorer = mock(Scorer.class);
//		FieldModel popQuantilesFieldModel = mock(FieldModel.class);
//		FieldModel medQuantilesFieldModel = mock(FieldModel.class);
//		EventMessage eventMessage = mock(EventMessage.class);
//
//		ContinuousDataGlobalModelScorer scorer = createScorer(
//				continuousDataLocalModelScorer,
//				popQuantilesFieldModel,
//				medQuantilesFieldModel,
//				qEventDown, qEventUp,
//				deltaQDown, deltaQUp,
//				increaseRate, qStopReducing);
//
//		when(continuousDataLocalModelScorer.calculateScore(eventMessage)).thenReturn(new FeatureScore(null, oldScore, null));
//		when(popQuantilesFieldModel.calculateScore(oldScore)).thenReturn(qEvent);
//		when(medQuantilesFieldModel.calculateScore(oldScore)).thenReturn(qContext);
//
//		FeatureScore featureScore = scorer.calculateScore(eventMessage);
//		Assert.assertEquals(expectedScore, featureScore.getScore(), 0.01);
//	}
//
//	@Test
//	public void continuous_data_global_model_scorer_should_return_a_correct_score_when_qEvent_is_larger_than_qEventUp_and_qEvent_is_larger_than_qStopReducing() throws Exception {
//		testScenario(60, 0.99, 0.88, 0.8, 0.95, 0.3, 0.8, 5, 0.97, 60);
//	}
//
//	@Test
//	public void continuous_data_global_model_scorer_should_return_a_correct_score_when_qEvent_is_larger_than_qEventUp_and_deltaQ_is_smaller_than_deltaQDown() throws Exception {
//		testScenario(100, 0.95, 0.15, 0.85, 0.95, 0.85, 0.9, 5, 0.97, 94.12);
//	}
//
//	@Test
//	public void continuous_data_global_model_scorer_should_return_a_correct_score_when_qEvent_is_larger_than_qEventUp_and_deltaQ_is_larger_than_deltaQUp() throws Exception {
//		testScenario(50, 0.95, 0.15, 0.4, 0.7, 0.3, 0.7, 5, 0.45, 75);
//	}
//
//	@Test
//	public void continuous_data_global_model_scorer_should_return_a_correct_score_when_qEvent_is_smaller_than_qEventUp_and_qEvent_is_larger_than_qStopReducing() throws Exception {
//		testScenario(80, 0.5, 0.3, 0.4, 0.9, 0.3, 0.8, 5, 0.45, 16);
//	}
//
//	@Test
//	public void continuous_data_global_model_scorer_should_return_a_correct_score_when_qEvent_is_smaller_than_qEventUp_and_deltaQ_is_smaller_than_deltaQDown() throws Exception {
//		testScenario(80, 0.5, 0.3, 0.4, 0.9, 0.3, 0.8, 5, 0.97, 10.67);
//	}
//
//	@Test
//	public void continuous_data_global_model_scorer_should_return_a_correct_score_when_qEvent_is_smaller_than_qEventUp_and_and_deltaQ_is_larger_than_deltaQUp() throws Exception {
//		testScenario(90, 0.9, 0.05, 0.8, 0.95, 0.3, 0.8, 5, 0.97, 75);
//	}
//}
