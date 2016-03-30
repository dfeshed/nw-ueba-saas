package fortscale.streaming.scorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.event.EventMessage;
import fortscale.ml.scorer.config.ReductionConfiguration;
import fortscale.utils.ConversionUtils;
import org.apache.samza.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fortscale.streaming.ConfigUtils.getConfigString;

public class LowValuesScoreReducer extends AbstractScorer {
	private static final Logger logger = LoggerFactory.getLogger(LowValuesScoreReducer.class);

	private Scorer baseScorer;
	private List<ReductionConfiguration> reductionConfigs;

	public LowValuesScoreReducer(String name, Config config, ScorerContext context) {
		super(name, config, context);

		// Get the base scorer
		String configKey = String.format("fortscale.score.%s.base.scorer", name);
		String baseScorerName = getConfigString(config, configKey);
		baseScorer = (Scorer)context.resolve(Scorer.class, baseScorerName);
		if (baseScorer == null) {
			String errorMsg = String.format("Could not find class of scorer %s", baseScorerName);
			logger.error(errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}

		// Get the reduction configurations
		configKey = String.format("fortscale.score.%s.reduction.configs", name);
		String jsonConfig = getConfigString(config, configKey);
		try {
			reductionConfigs = Arrays.asList(new ObjectMapper().readValue(jsonConfig, ReductionConfiguration[].class));
		} catch (Exception e) {
			String errorMsg = String.format("Failed to deserialize json %s", jsonConfig);
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		FeatureScore featureScore = baseScorer.calculateScore(eventMessage);
		double reducedScore = reduceScore(eventMessage, featureScore.getScore());
		List<FeatureScore> featureScores = new ArrayList<>();
		featureScores.add(featureScore);
		return new FeatureScore(outputFieldName, reducedScore, featureScores);
	}

	private double reduceScore(EventMessage eventMessage, double score) {
		for (ReductionConfiguration reductionConfig : reductionConfigs)
			score = reduceScore(eventMessage, score, reductionConfig);
		return score;
	}

	private double reduceScore(EventMessage eventMessage, double score, ReductionConfiguration reductionConfig) {
		String valueAsString = ConversionUtils.convertToString(featureExtractionService.extract(
				reductionConfig.getReducingFeatureName(), eventMessage.getJsonObject()));
		Double value = ConversionUtils.convertToDouble(valueAsString);
		double factor = 1;

		if (value != null) {
			if (value <= reductionConfig.getMaxValueForFullyReduce())
				factor = reductionConfig.getReducingFactor();
			else if (value < reductionConfig.getMinValueForNotReduce()) {
				double numerator = value - reductionConfig.getMaxValueForFullyReduce();
				double denominator = reductionConfig.getMinValueForNotReduce() - reductionConfig.getMaxValueForFullyReduce();
				double partToAdd = numerator / denominator;
				factor = reductionConfig.getReducingFactor() + (1 - reductionConfig.getReducingFactor()) * partToAdd;
			}
		}

		return score * factor;
	}
}
