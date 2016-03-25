package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.ml.scorer.config.ReductionConfiguration;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

@Configurable(preConstruction = true)
public class LowValuesScoreReducer extends AbstractScorer {
	private static final Logger logger = Logger.getLogger(LowValuesScoreReducer.class);

	private Scorer baseScorer;
	private List<ReductionConfiguration> reductionConfigs;

	public LowValuesScoreReducer(String name, Scorer baseScorer, List<ReductionConfiguration> reductionConfigs) {
		super(name);
		Assert.notNull(baseScorer);
		Assert.notNull(reductionConfigs);
		this.baseScorer = baseScorer;
		this.reductionConfigs = reductionConfigs;
	}

	@Override
	public FeatureScore calculateScore(Event eventMessage, long eventEpochtime) throws Exception {
		FeatureScore baseFeatureScore = baseScorer.calculateScore(eventMessage, eventEpochtime);
		double reducedScore = reduceScore(eventMessage, baseFeatureScore.getScore());
		return new FeatureScore(getName(), reducedScore, Collections.singletonList(baseFeatureScore));
	}

	private double reduceScore(Event eventMessage, double score) {
		for (ReductionConfiguration reductionConfig : reductionConfigs) {
			score = reduceScore(eventMessage, score, reductionConfig);
		}

		return score;
	}

	private double reduceScore(Event eventMessage, double score, ReductionConfiguration reductionConfig) {
		Feature feature = featureExtractService.extract(reductionConfig.getReducingFeatureName(), eventMessage);

		if (feature == null || feature.getValue() == null) {
			return score;
		} else if (feature.getValue() instanceof FeatureNumericValue) {
			double reducingFeature = ((FeatureNumericValue)feature.getValue()).getValue().doubleValue();
			return reduceScore(reducingFeature, score, reductionConfig);
		} else {
			logger.error("Extracted feature {} is of type {}, but should be of type {}.",
					reductionConfig.getReducingFeatureName(),
					feature.getValue().getClass().getSimpleName(),
					FeatureNumericValue.class.getSimpleName());
			return score;
		}
	}

	private static double reduceScore(double reducingFeature, double score, ReductionConfiguration reductionConfig) {
		double factor = 1;

		if (reducingFeature <= reductionConfig.getMaxValueForFullyReduce()) {
			factor = reductionConfig.getReducingFactor();
		} else if (reducingFeature < reductionConfig.getMinValueForNotReduce()) {
			double n = reducingFeature - reductionConfig.getMaxValueForFullyReduce();
			double d = reductionConfig.getMinValueForNotReduce() - reductionConfig.getMaxValueForFullyReduce();
			factor = reductionConfig.getReducingFactor() + (1 - reductionConfig.getReducingFactor()) * (n / d);
		}

		return score * factor;
	}
}
