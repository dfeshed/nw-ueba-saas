package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.ArrayList;
import java.util.List;

public class FieldValueScoreReducerScorer extends AbstractScorer {
	private static final double ABSOLUTE_MAX_SCORE = 100;
	private static final String NULL_SCORER_ERROR_MSG = "baseScorer cannot be null.";
	private static final String NULL_LIMITERS_ERROR_MSG = "limiters cannot be null.";

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
	public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
		FeatureScore featureScore = baseScorer.calculateScore(adeRecordReader);
		Double reducedScore = featureScore.getScore();
		Integer maxScore = getMaxScore(adeRecordReader);
		if (maxScore != null) reducedScore *= maxScore / ABSOLUTE_MAX_SCORE;
		List<FeatureScore> featureScores = new ArrayList<>();
		featureScores.add(featureScore);
		return new FeatureScore(getName(), reducedScore, featureScores);
	}

	private Integer getMaxScore(AdeRecordReader adeRecordReader) {
		if (limiters != null) {
			for (FieldValueScoreLimiter limiter : limiters) {
				Integer maxScore = getMaxScore(adeRecordReader, limiter);
				if (maxScore != null) return maxScore;
			}
		}

		return null;
	}

	private Integer getMaxScore(AdeRecordReader adeRecordReader, FieldValueScoreLimiter limiter) {
		String value = adeRecordReader.get(limiter.getFieldName()).toString();
		return (value != null ? limiter.getValueToMaxScoreMap().get(value) : null);
	}
}
