package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.recordreader.RecordReader;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecord;

import java.util.ArrayList;
import java.util.List;

public class FieldValueScoreReducerScorer extends AbstractScorer {
	private static final double ABSOLUTE_MAX_SCORE = 100;
	private static final String NULL_SCORER_ERROR_MSG = "baseScorer cannot be null.";
	private static final String NULL_LIMITERS_ERROR_MSG = "limiters cannot be null";

	private Scorer baseScorer;
	private List<FieldValueScoreLimiter> limiters;
	private FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService;

	public FieldValueScoreReducerScorer(String name, Scorer baseScorer, List<FieldValueScoreLimiter> limiters,
										FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService) {

		super(name);
		Assert.notNull(baseScorer, NULL_SCORER_ERROR_MSG);
		Assert.notNull(limiters, NULL_LIMITERS_ERROR_MSG);

		this.baseScorer = baseScorer;
		this.limiters = limiters;
		this.recordReaderFactoryService = recordReaderFactoryService;
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
	public FeatureScore calculateScore(AdeRecord record) {
		FeatureScore featureScore = baseScorer.calculateScore(record);
		Double reducedScore = featureScore.getScore();
		Integer maxScore = getMaxScore(record);
		if (maxScore != null)
			reducedScore *= maxScore / ABSOLUTE_MAX_SCORE;

		List<FeatureScore> featureScores = new ArrayList<>();
		featureScores.add(featureScore);

		return new FeatureScore(getName(), reducedScore, featureScores);
	}

	private Integer getMaxScore(AdeRecord record) {
		if (limiters != null) {
			for (FieldValueScoreLimiter limiter : limiters) {
				Integer maxScore = getMaxScore(record, limiter);
				if (maxScore != null)
					return maxScore;
			}
		}

		return null;
	}

	private Integer getMaxScore(AdeRecord record, FieldValueScoreLimiter limiter) {
		String value = recordReaderFactoryService
				.getDefaultProduct(record.getAdeRecordType())
				.get(record, limiter.getFieldName())
				.toString();
		return (value != null ? limiter.getValueToMaxScoreMap().get(value) : null);
	}

}
