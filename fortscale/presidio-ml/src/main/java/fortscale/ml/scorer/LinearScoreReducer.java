package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecord;

import java.util.ArrayList;
import java.util.List;

@Configurable(preConstruction = true)
public class LinearScoreReducer extends AbstractScorer {
	private static final Logger logger = Logger.getLogger(LinearScoreReducer.class);

	private Scorer reducedScorer;
	private double reducingWeight;

	public LinearScoreReducer(String name, Scorer reducedScorer, double reducingWeight) {
		super(name);
		Assert.notNull(reducedScorer, "Reduced scorer cannot be null.");
		this.reducedScorer = reducedScorer;
		this.reducingWeight = reducingWeight;
	}

	@Override
	public FeatureScore calculateScore(AdeRecord record) {
		FeatureScore featureScore = reducedScorer.calculateScore(record);

		if (featureScore != null) {
			List<FeatureScore> featureScores = new ArrayList<>();
			featureScores.add(featureScore);
			double reducedScore = featureScore.getScore() * reducingWeight;
			return new FeatureScore(getName(), reducedScore, featureScores);
		} else {
			logger.error("Reduced scorer {} returned a null feature score.", reducedScorer.getName());
			return new FeatureScore(getName(), 0d, new ArrayList<>());
		}
	}
}
