package fortscale.ml.scorer;

import fortscale.common.event.Event;

import java.util.ArrayList;
import java.util.List;

public class PriorityScorerContainer extends ScorerContainer{

	public PriorityScorerContainer(String name, List<Scorer> scorers) {
		super(name, scorers);
	}


	@Override
	public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
		FeatureScore featureScore = null;

		for(Scorer scorer: scorers) {
			featureScore = scorer.calculateScore(eventMessage, eventEpochTimeInSec);
			if(featureScore != null){
				break;
			}
		}

		if(featureScore == null){
			return new FeatureScore(getName(), 0d) ;
		} else {
			List<FeatureScore> featureScores = new ArrayList<>();
			featureScores.add(featureScore);
			return new FeatureScore(getName(), featureScore.getScore(), featureScores);
		}
	}
}
