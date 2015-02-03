package fortscale.streaming.scorer;

import java.util.ArrayList;
import java.util.List;

import org.apache.samza.config.Config;

public class PriorityScorerContainer extends ScorerContainer{

	public PriorityScorerContainer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config, context);
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		FeatureScore featureScore = null;
		
		for(Scorer scorer: scorers) {
			featureScore = scorer.calculateScore(eventMessage);
			if(featureScore != null){
				break;
			}
		}
		
		if(featureScore == null){
			return new FeatureScore(outputFieldName, 0d) ;
		} else{
			List<FeatureScore> featureScores = new ArrayList<>();
			featureScores.add(featureScore);
			return new FeatureScore(outputFieldName, featureScore.getScore(), featureScores);
		}				
	}
	
}
