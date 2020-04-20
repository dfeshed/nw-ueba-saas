package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureStringValue;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.config.ReductionConfiguration;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Collections;
import java.util.List;

public class LowValuesScoreReducer extends AbstractScorer {
	private static final Logger logger = Logger.getLogger(LowValuesScoreReducer.class);

	private Scorer baseScorer;
	private List<ReductionConfiguration> reductionConfigs;

	public LowValuesScoreReducer(String name, Scorer baseScorer, List<ReductionConfiguration> reductionConfigs) {
		super(name);
		Assert.notNull(baseScorer, "Base scorer cannot be null.");
		Assert.notNull(reductionConfigs, "Reduction configs cannot be null.");
		this.baseScorer = baseScorer;
		this.reductionConfigs = reductionConfigs;
	}

	@Override
	public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
		FeatureScore baseFeatureScore = baseScorer.calculateScore(adeRecordReader);
		double reducedScore = reduceScore(adeRecordReader, baseFeatureScore.getScore());
		return new FeatureScore(getName(), reducedScore, Collections.singletonList(baseFeatureScore));
	}

	private double reduceScore(AdeRecordReader adeRecordReader, double score) {
		for (ReductionConfiguration reductionConfig : reductionConfigs) {
			score = reduceScore(adeRecordReader, score, reductionConfig);
		}

		return score;
	}

	private double reduceScore(AdeRecordReader adeRecordReader, double score, ReductionConfiguration reductionConfig) {
		String reducingFeatureName = reductionConfig.getReducingFeatureName();
		Feature feature = Feature.toFeature(reducingFeatureName, adeRecordReader.get(reducingFeatureName));

		if (feature == null || feature.getValue() == null) {
			return score;
		} else if (feature.getValue() instanceof FeatureNumericValue) {
			FeatureNumericValue value = (FeatureNumericValue)feature.getValue();
			double reducingFeature = value.getValue().doubleValue();
			return reduceScore(reducingFeature, score, reductionConfig);
		} else if (feature.getValue() instanceof FeatureStringValue) {
			FeatureStringValue value = (FeatureStringValue)feature.getValue();
			double reducingFeature;

			try {
				reducingFeature = Double.parseDouble(value.getValue());
			} catch (Exception e) {
				logger.error("Extracted feature {} is a string that does not represent a number. Score isn't reduced.",
						reductionConfig.getReducingFeatureName(), e);
				return score;
			}

			return reduceScore(reducingFeature, score, reductionConfig);
		} else {
			logger.error("Extracted feature {} is of type {}, but should be a number or a string. Score isn't reduced.",
					reductionConfig.getReducingFeatureName(), feature.getValue().getClass().getSimpleName());
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
