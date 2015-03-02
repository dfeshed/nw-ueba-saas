package fortscale.streaming.scorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.streaming.scorer.ReductionConfigurations.ReductionConfiguration;
import fortscale.utils.ConversionUtils;
import org.apache.samza.config.Config;

import java.io.IOException;

import static fortscale.streaming.ConfigUtils.getConfigString;

public class LowValuesScoreReducer extends AbstractScorer {
	protected Scorer baseScorer = null;
	protected ReductionConfigurations reductionConfigs = null;

	public LowValuesScoreReducer(String name, Config config, ScorerContext context) {
		super(name, config);

		// Get the base scorer
		String baseScorerName = getConfigString(config, String.format("fortscale.score.%s.base.scorer", name));
		baseScorer = (Scorer)context.resolve(Scorer.class, baseScorerName);
		if (baseScorer == null)
			throw new NullPointerException();

		// Get the reduction configurations
		ObjectMapper mapper = new ObjectMapper();
		String jsonConfig = getConfigString(config, String.format("fortscale.score.%s.reduction.configs", name));
		try {
			reductionConfigs = mapper.readValue(jsonConfig, ReductionConfigurations.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		FeatureScore featureScore = calculateBaseScore(eventMessage);
		double reducedScore = reduceScore(eventMessage, featureScore.getScore());
		return new FeatureScore(featureScore.getName(), reducedScore, featureScore.getFeatureScores());
	}

	private FeatureScore calculateBaseScore(EventMessage eventMessage) throws Exception {
		return baseScorer.calculateScore(eventMessage);
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
