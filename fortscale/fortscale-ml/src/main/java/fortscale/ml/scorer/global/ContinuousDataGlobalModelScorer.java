//package fortscale.streaming.scorer;
//
//import fortscale.common.event.EventMessage;
//import fortscale.ml.model.prevalance.FieldModel;
//import fortscale.ml.model.prevalance.PrevalanceModel;
//import fortscale.ml.service.ModelService;
//import fortscale.streaming.service.GlobalModelStreamTaskService;
//import org.apache.commons.lang.StringUtils;
//import org.apache.samza.config.Config;
//import org.springframework.util.Assert;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ContinuousDataGlobalModelScorer extends AbstractScorer {
//	private Scorer continuousDataLocalModelScorer;
//	private ModelService modelService;
//
//	private String popQuantilesFieldModelName;
//	private String medQuantilesFieldModelName;
//
//	// Required algorithm parameters
//	private double qEventDown;
//	private double qEventUp;
//	private double deltaQDown;
//	private double deltaQUp;
//	private double increaseRate;
//	private double qStopReducing;
//
//	public ContinuousDataGlobalModelScorer(String scorerName, Config config, ScorerContext context) {
//		super(scorerName, config, context);
//
//		// Get continuous data local model scorer
//		String localModelScorerName = config.get(String.format("fortscale.score.%s.continuous.data.local.model.scorer", scorerName), null);
//		Assert.isTrue(StringUtils.isNotBlank(localModelScorerName), "Missing valid name of continuous data local model scorer");
//		continuousDataLocalModelScorer = (Scorer)context.resolve(Scorer.class, localModelScorerName);
//		Assert.notNull(continuousDataLocalModelScorer, String.format("Could not find class of scorer %s", localModelScorerName));
//
//		// Get model service
//		modelService = (ModelService)context.resolve(ModelService.class, "modelService");
//		Assert.notNull(modelService, "Could not resolve model service");
//
//		// Extract model names
//		popQuantilesFieldModelName = config.get(String.format("fortscale.score.%s.population.quantiles.field.model.name", scorerName), null);
//		Assert.isTrue(StringUtils.isNotBlank(popQuantilesFieldModelName), "Missing valid population quantiles field model name");
//		medQuantilesFieldModelName = config.get(String.format("fortscale.score.%s.medians.quantiles.field.model.name", scorerName), null);
//		Assert.isTrue(StringUtils.isNotBlank(medQuantilesFieldModelName), "Missing valid medians quantiles field model name");
//
//		// Extract algorithm parameters
//		qEventDown = config.getDouble(String.format("fortscale.score.%s.q.event.down", scorerName));
//		qEventUp = config.getDouble(String.format("fortscale.score.%s.q.event.up", scorerName));
//		deltaQDown = config.getDouble(String.format("fortscale.score.%s.delta.q.down", scorerName));
//		deltaQUp = config.getDouble(String.format("fortscale.score.%s.delta.q.up", scorerName));
//		increaseRate = config.getDouble(String.format("fortscale.score.%s.increase.rate", scorerName));
//		qStopReducing = config.getDouble(String.format("fortscale.score.%s.q.stop.reducing", scorerName));
//	}
//
//	@Override
//	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
//		// Get score according to continuous data local model
//		FeatureScore oldFeatureScore = continuousDataLocalModelScorer.calculateScore(eventMessage);
//
//		// If global prevalence model is not found, score according to local one
//		if (!modelService.modelExists(
//				GlobalModelStreamTaskService.GLOBAL_CONTEXT_CONSTANT,
//				GlobalModelStreamTaskService.GLOBAL_MODEL_NAME)) {
//			return oldFeatureScore;
//		}
//
//		Double oldScore = oldFeatureScore.getScore();
//
//		// Get global prevalence model
//		PrevalanceModel globalModel = modelService.getModel(
//				GlobalModelStreamTaskService.GLOBAL_CONTEXT_CONSTANT,
//				GlobalModelStreamTaskService.GLOBAL_MODEL_NAME);
//
//		// Calculate scoring factors
//		double qEvent = qEvent(oldScore, globalModel);
//		double qContext = qContext(oldScore, globalModel);
//		double factorEvent = factorEvent(qEvent);
//		double factorDeltaQ = factorDeltaQ(qEvent - qContext, qEvent); // 1st parameter = deltaQ
//
//		// Calculate new score
//		double newScore = Math.min(100, oldScore * factorEvent * factorDeltaQ);
//
//		// Return new feature score
//		List<FeatureScore> featureScores = new ArrayList<>();
//		featureScores.add(oldFeatureScore);
//		return new FeatureScore(outputFieldName, newScore, featureScores);
//	}
//
//	private double qEvent(Double value, PrevalanceModel globalModel) {
//		FieldModel popQuantiles = globalModel.getFieldModel(popQuantilesFieldModelName);
//		return popQuantiles.calculateScore(value);
//	}
//
//	private double qContext(Double value, PrevalanceModel globalModel) {
//		FieldModel medQuantiles = globalModel.getFieldModel(medQuantilesFieldModelName);
//		return medQuantiles.calculateScore(value);
//	}
//
//	private double factorEvent(double qEvent) {
//		if (qEvent >= qEventUp) {
//			return 1;
//		} else {
//			return Math.max(0, (qEvent - qEventDown) / (qEventUp - qEventDown));
//		}
//	}
//
//	private double factorDeltaQ(double deltaQ, double qEvent) {
//		if (deltaQ > deltaQUp) {
//			return 1 + (deltaQ - deltaQUp) * increaseRate;
//		} else if (deltaQ < deltaQDown && qEvent < qStopReducing) {
//			return Math.max(0, deltaQ / deltaQDown);
//		} else {
//			return 1;
//		}
//	}
//}
