package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import presidio.ade.domain.record.AdeRecord;

import java.util.ArrayList;
import java.util.List;

public class MaxScorerContainer extends ScorerContainer {

	public MaxScorerContainer(String name, List<Scorer> scorers) {
		super(name, scorers);
	}

	@Override
	public FeatureScore calculateScore(AdeRecord record) {
		double maxScore = 0;
		List<FeatureScore> featureScores = new ArrayList<>();
		for(Scorer scorer: scorers) {
			FeatureScore featureScore = scorer.calculateScore(record);
			if(featureScore != null){
				featureScores.add(featureScore);
				maxScore = Math.max(maxScore, featureScore.getScore());
			}
		}

		return new FeatureScore(getName(), maxScore, featureScores);
	}

}
