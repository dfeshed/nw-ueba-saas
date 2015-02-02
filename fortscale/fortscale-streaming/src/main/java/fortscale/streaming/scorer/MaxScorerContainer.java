package fortscale.streaming.scorer;

import java.util.ArrayList;
import java.util.List;

import org.apache.samza.config.Config;

public class MaxScorerContainer extends ScorerContainer {

	public MaxScorerContainer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config, context);
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		double maxScore = 0;
		List<FeatureScore> featureScores = new ArrayList<>();
		for(Scorer scorer: scorers) {
			FeatureScore featureScore = scorer.calculateScore(eventMessage);
			if(featureScore != null){
				featureScores.add(featureScore);
				maxScore = Math.max(maxScore, featureScore.getScore());
			}
		}
		
		return new FeatureScore(outputFieldName, maxScore, featureScores);
	}

}
