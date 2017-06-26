package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Collections;

public class ScoreAndCertaintyMultiplierScorer extends AbstractScorer {
	private Scorer baseScorer;

	public ScoreAndCertaintyMultiplierScorer(String scorerName, Scorer baseScorer) {
		super(scorerName);
		Assert.notNull(baseScorer, "Base scorer must not be null");
		this.baseScorer = baseScorer;
	}

	@Override
	public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
		FeatureScore featureScore = baseScorer.calculateScore(adeRecordReader);
		return new FeatureScore(getName(),
				featureScore.getScore() * featureScore.getCertainty(),
				Collections.singletonList(featureScore));
	}
}
