package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.domain.feature.score.FeatureScore;
import org.springframework.util.Assert;

import java.util.Collections;

public class ScoreAndCertaintyMultiplierScorer extends AbstractScorer {
	private Scorer baseScorer;

	public ScoreAndCertaintyMultiplierScorer(String scorerName, Scorer baseScorer) {
		super(scorerName);
		Assert.notNull(baseScorer, "Base scorer must not be null");
		this.baseScorer = baseScorer;
	}

	@Override
	public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
		FeatureScore featureScore = baseScorer.calculateScore(eventMessage, eventEpochTimeInSec);
		return new FeatureScore(getName(),
				featureScore.getScore() * featureScore.getCertainty(),
				Collections.singletonList(featureScore));
	}
}
