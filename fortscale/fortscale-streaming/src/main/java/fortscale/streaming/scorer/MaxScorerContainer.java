package fortscale.streaming.scorer;

import org.apache.samza.config.Config;

public class MaxScorerContainer extends ScorerContainer {

	public MaxScorerContainer(String scoreName, Config config) {
		super(scoreName, config);
	}

	@Override
	public Double calculateScore(EventMessage eventMessage) throws Exception {
		double eventScore = 0;
		for(Scorer scorer: scorers) {
			Double score = scorer.calculateScore(eventMessage);
			if(score != null){
				eventScore = Math.max(eventScore, score);
			}
		}
		
		eventMessage.setScore(scorerContainerConfig.getScoreName(), eventScore);
		
		return eventScore;
	}

}
