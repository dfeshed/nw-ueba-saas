package fortscale.streaming.scorer;

import org.apache.samza.config.Config;

public class PriorityScorerContainer extends ScorerContainer{

	public PriorityScorerContainer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config, context);
	}

	@Override
	public Double calculateScore(EventMessage eventMessage) throws Exception {
		Double score = null;
		for(Scorer scorer: scorers) {
			score = scorer.calculateScore(eventMessage);
			if(score != null){
				break;
			}
		}
		
		eventMessage.setScore(outputFieldName, score);
		
		return score;
	}
	
}
