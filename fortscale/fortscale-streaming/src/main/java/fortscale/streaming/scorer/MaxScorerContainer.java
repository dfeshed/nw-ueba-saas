package fortscale.streaming.scorer;

import org.apache.samza.config.Config;

public class MaxScorerContainer extends ScorerContainer {

	public MaxScorerContainer(String scorerName, Config config) {
		super(scorerName, config);
	}

	@Override
	public Double calculateScore(EventMessage eventMessage) throws Exception {
		double maxScore = 0;
		for(Scorer scorer: scorers) {
			Double score = scorer.calculateScore(eventMessage);
			if(score != null){
				maxScore = Math.max(maxScore, score);
			}
		}
		
		eventMessage.setScore(outputFieldName, maxScore);
		
		return maxScore;
	}

}
