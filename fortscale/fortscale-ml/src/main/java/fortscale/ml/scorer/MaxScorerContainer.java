package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.domain.core.FeatureScore;

import java.util.ArrayList;
import java.util.List;

public class MaxScorerContainer extends ScorerContainer {

	public MaxScorerContainer(String name, List<Scorer> scorers) {
		super(name, scorers);
	}

	@Override
	public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
		double maxScore = 0;
		List<FeatureScore> featureScores = new ArrayList<>();
		for(Scorer scorer: scorers) {
			FeatureScore featureScore = scorer.calculateScore(eventMessage, eventEpochTimeInSec);
			if(featureScore != null){
				featureScores.add(featureScore);
				maxScore = Math.max(maxScore, featureScore.getScore());
			}
		}
		
		return new FeatureScore(getName(), maxScore, featureScores);
	}

}
