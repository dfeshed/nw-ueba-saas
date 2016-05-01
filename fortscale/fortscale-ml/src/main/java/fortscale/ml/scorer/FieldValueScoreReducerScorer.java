package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.common.event.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class FieldValueScoreReducerScorer extends AbstractScorer {
	private static final double ABSOLUTE_MAX_SCORE = 100;
	private static final Logger logger = LoggerFactory.getLogger(FieldValueScoreReducerScorer.class);
	private static final String NULL_SCORER_ERROR_MSG = "baseScorer cannot be null.";
	private static final String NULL_LIMITERS_ERROR_MSG = "limiters cannot be null";

	private Scorer baseScorer;
	private List<FieldValueScoreLimiter> limiters;

	public FieldValueScoreReducerScorer(String name, Scorer baseScorer, List<FieldValueScoreLimiter> limiters) {
		super(name);
		Assert.notNull(baseScorer, NULL_SCORER_ERROR_MSG);
		Assert.notNull(limiters, NULL_LIMITERS_ERROR_MSG);

		this.baseScorer = baseScorer;
		this.limiters = limiters;
	}

	public Scorer getBaseScorer() {
		return baseScorer;
	}

	public void setBaseScorer(Scorer baseScorer) {
		this.baseScorer = baseScorer;
	}

	public List<FieldValueScoreLimiter> getLimiters() {
		return limiters;
	}

	public void setLimiters(List<FieldValueScoreLimiter> limiters) {
		this.limiters = limiters;
	}

	@Override
	public FeatureScore calculateScore(Event event, long eventEpochTimeInSec) throws Exception {
		FeatureScore featureScore = baseScorer.calculateScore(event, eventEpochTimeInSec);

		Double reducedScore = featureScore.getScore();
		EventMessage eventMessage = new EventMessage(event.getJSONObject());

		Integer maxScore = getMaxScore(eventMessage);
		if (maxScore != null)
			reducedScore *= maxScore / ABSOLUTE_MAX_SCORE;

		List<FeatureScore> featureScores = new ArrayList<>();
		featureScores.add(featureScore);

		return new FeatureScore(getName(), reducedScore, featureScores);
	}

	private Integer getMaxScore(EventMessage eventMessage) {
		if (limiters != null) {
			for (FieldValueScoreLimiter limiter : limiters) {
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
