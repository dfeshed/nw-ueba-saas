package fortscale.streaming.scorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.streaming.scorer.ReductionConfigurations.ReductionConfiguration;
import fortscale.utils.ConversionUtils;
import groovy.lang.MissingPropertyException;
import org.apache.samza.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.isConfigContainKey;

public class LowValuesScoreReducer extends AbstractScorer {
	private static final Logger logger = LoggerFactory.getLogger(LowValuesScoreReducer.class);

	protected Scorer baseScorer = null;
	protected ReductionConfigurations reductionConfigs = null;

	public LowValuesScoreReducer(String name, Config config) {
		super(name, config);
	}

	public LowValuesScoreReducer(String name, Config config, ScorerContext context) throws Exception {
		super(name, config);

		// Get the base scorer
		String configKey = String.format("fortscale.score.%s.base.scorer", name);
		if (!isConfigContainKey(config, configKey)) {
			String errorMsg = String.format("Configuration does not contain key %s", configKey);
			logger.error(errorMsg);
			throw new MissingPropertyException(errorMsg);
		}

		String baseScorerName = getConfigString(config, configKey);
		baseScorer = (Scorer)context.resolve(Scorer.class, baseScorerName);
		if (baseScorer == null) {
			String errorMsg = String.format("Could not find class of scorer %s", baseScorerName);
			logger.error(errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}

		// Get the reduction configurations
		configKey = String.format("fortscale.score.%s.reduction.configs", name);
		if (!isConfigContainKey(config, configKey)) {
			String errorMsg = String.format("Configuration does not contain key %s", configKey);
			logger.error(errorMsg);
			throw new MissingPropertyException(errorMsg);
		}

		String jsonConfig = getConfigString(config, configKey);
		reductionConfigs = (new ObjectMapper()).readValue(jsonConfig, ReductionConfigurations.class);
		if (reductionConfigs == null) {
			String errorMsg = String.format("Failed to deserialize json %s", jsonConfig);
			logger.error(errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		FeatureScore featureScore = baseScorer.calculateScore(eventMessage);
		double reducedScore = reduceScore(eventMessage, featureScore.getScore());
		return new FeatureScore(featureScore.getName(), reducedScore, featureScore.getFeatureScores());
	}

	private double reduceScore(EventMessage eventMessage, double score) {
		for (ReductionConfiguration reductionConfig : reductionConfigs.getReductionConfigs())
			score = reduceScore(eventMessage, score, reductionConfig);
		return score;
	}

	private double reduceScore(EventMessage eventMessage, double score, ReductionConfiguration reductionConfig) {
		String valueAsString = eventMessage.getEventStringValue(reductionConfig.getReducingValueName());
		Double value = ConversionUtils.convertToDouble(valueAsString);
		double factor = 1;

		if (value != null) {
			if (value <= reductionConfig.getMaxValueForFullReduction())
				factor = reductionConfig.getReductionFactor();
			else if (value < reductionConfig.getMinValueForNoReduction()) {
				double numerator = value - reductionConfig.getMaxValueForFullReduction();
				double denominator = reductionConfig.getMinValueForNoReduction() - reductionConfig.getMaxValueForFullReduction();
				double partToAdd = numerator / denominator;
				factor = reductionConfig.getReductionFactor() + (1 - reductionConfig.getReductionFactor()) * partToAdd;
			}
		}

		return score * factor;
	}
}
