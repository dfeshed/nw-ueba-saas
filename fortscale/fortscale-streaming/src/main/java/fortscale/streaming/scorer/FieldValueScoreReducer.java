package fortscale.streaming.scorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.samza.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.scorer.FieldValueScoreLimiters.FieldValueScoreLimiter;

public class FieldValueScoreReducer extends AbstractScorer {
	private static final double ABSOLUTE_MAX_SCORE = 100;
	private static final Logger logger = LoggerFactory.getLogger(FieldValueScoreReducer.class);

	private Scorer baseScorer;
	private FieldValueScoreLimiters limiters;

	public FieldValueScoreReducer(String name, Config config, ScorerContext context) {
		super(name, config, context);

		// Get the base scorer
		String configKey = String.format("fortscale.score.%s.base.scorer", name);
		String configVal = getConfigString(config, configKey);
		baseScorer = (Scorer)context.resolve(Scorer.class, configVal);
		if (baseScorer == null) {
			String errorMsg = String.format("Could not find class of scorer %s", configVal);
			logger.error(errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}

		// Get the limiters
		configKey = String.format("fortscale.score.%s.limiters", name);
		configVal = getConfigString(config, configKey);
		try {
			limiters = (new ObjectMapper()).readValue(configVal, FieldValueScoreLimiters.class);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to deserialize json %s", configVal);
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}
	}

	protected Scorer getBaseScorer() {
		return baseScorer;
	}

	protected void setBaseScorer(Scorer baseScorer) {
		this.baseScorer = baseScorer;
	}

	protected FieldValueScoreLimiters getLimiters() {
		return limiters;
	}

	protected void setLimiters(FieldValueScoreLimiters limiters) {
		this.limiters = limiters;
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		FeatureScore featureScore = baseScorer.calculateScore(eventMessage);

		Double reducedScore = featureScore.getScore();
		Integer maxScore = getMaxScore(eventMessage);
		if (maxScore != null)
			reducedScore *= maxScore / ABSOLUTE_MAX_SCORE;

		List<FeatureScore> featureScores = new ArrayList<>();
		featureScores.add(featureScore);

		return new FeatureScore(outputFieldName, reducedScore, featureScores);
	}

	private Integer getMaxScore(EventMessage eventMessage) {
		if (limiters != null && limiters.getLimiters() != null) {
			for (FieldValueScoreLimiter limiter : limiters.getLimiters()) {
				Integer maxScore = getMaxScore(eventMessage, limiter);
				if (maxScore != null)
					return maxScore;
			}
		}

		return null;
	}

	private Integer getMaxScore(EventMessage eventMessage, FieldValueScoreLimiter limiter) {
		String value = eventMessage.getEventStringValue(limiter.getFieldName());
		return (value != null ? limiter.getValueToMaxScoreMap().get(value) : null);
	}
}
