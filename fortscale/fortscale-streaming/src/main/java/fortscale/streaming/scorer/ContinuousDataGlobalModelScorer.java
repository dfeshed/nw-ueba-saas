package fortscale.streaming.scorer;

import fortscale.ml.model.prevalance.FieldModel;
import fortscale.ml.model.prevalance.PrevalanceModel;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.springframework.util.Assert;

public class ContinuousDataGlobalModelScorer extends ContiuousModelScorer {
	private String globalModelName;
	private String popQuantilesFieldModelName;
	private String medQuantilesFieldModelName;

	// Required algorithm parameters
	private double qEventDown;
	private double qEventUp;
	private double deltaQDown;
	private double deltaQUp;
	private double increaseRate;
	private double qStopReducing;

	public ContinuousDataGlobalModelScorer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config, context);

		// Extract model names
		globalModelName = config.get(String.format("fortscale.score.%s.global.model.name", scorerName), null);
		popQuantilesFieldModelName = config.get(String.format("fortscale.score.%s.population.quantiles.field.model.name", scorerName), null);
		medQuantilesFieldModelName = config.get(String.format("fortscale.score.%s.medians.quantiles.field.model.name", scorerName), null);

		// Validate model names
		Assert.isTrue(StringUtils.isNotBlank(globalModelName), "Missing valid global model name");
		Assert.isTrue(StringUtils.isNotBlank(popQuantilesFieldModelName), "Missing valid population quantiles field model name");
		Assert.isTrue(StringUtils.isNotBlank(medQuantilesFieldModelName), "Missing valid medians quantiles field model name");

		// Extract algorithm parameters
		qEventDown = config.getDouble(String.format("fortscale.score.%s.q.event.down", scorerName));
		qEventUp = config.getDouble(String.format("fortscale.score.%s.q.event.up", scorerName));
		deltaQDown = config.getDouble(String.format("fortscale.score.%s.delta.q.down", scorerName));
		deltaQUp = config.getDouble(String.format("fortscale.score.%s.delta.q.up", scorerName));
		increaseRate = config.getDouble(String.format("fortscale.score.%s.increase.rate", scorerName));
		qStopReducing = config.getDouble(String.format("fortscale.score.%s.q.stop.reducing", scorerName));
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		Double oldScore = super.calculateScore(eventMessage).getScore();
		PrevalanceModel globalModel = modelService.getModel("NO_CONTEXT", globalModelName);

		double qEvent = qEvent(oldScore, globalModel);
		double qContext = qContext(oldScore, globalModel);
		double factorEvent = factorEvent(qEvent);
		double factorDeltaQ = factorDeltaQ(qEvent - qContext, qEvent); // 1st parameter = deltaQ

		double newScore = Math.min(100, oldScore * factorEvent * factorDeltaQ);
		return new FeatureScore(outputFieldName, newScore);
	}

	private double qEvent(Double value, PrevalanceModel globalModel) {
		FieldModel popQuantiles = globalModel.getFieldModel(popQuantilesFieldModelName);
		return popQuantiles.calculateScore(value);
	}

	private double qContext(Double value, PrevalanceModel globalModel) {
		FieldModel medQuantiles = globalModel.getFieldModel(medQuantilesFieldModelName);
		return medQuantiles.calculateScore(value);
	}

	private double factorEvent(double qEvent) {
		if (qEvent >= qEventUp) {
			return 1;
		} else {
			return Math.max(0, (qEvent - qEventDown) / (qEventUp - qEventDown));
		}
	}

	private double factorDeltaQ(double deltaQ, double qEvent) {
		if (deltaQ > deltaQUp) {
			return Math.max(1, 1 + (deltaQ - deltaQUp) * increaseRate);
		} else if (deltaQ < deltaQDown && qEvent < qStopReducing) {
			return Math.max(0, deltaQ / deltaQDown);
		} else {
			return 1;
		}
	}
}
