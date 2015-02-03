package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;

import java.util.ArrayList;
import java.util.List;

import org.apache.samza.config.Config;

public class ReductingScorer extends AbstractScorer {
	
	private Scorer mainScorer;
	private Scorer reductingScorer;
	private double reductingWeight;

	public ReductingScorer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config);
		mainScorer = getScorer(String.format("fortscale.score.%s.main.scorer", scorerName), config, context);
		reductingScorer = getScorer(String.format("fortscale.score.%s.reducting.scorer", scorerName), config, context);
		reductingWeight = config.getDouble(String.format("fortscale.score.%s.reducting.weight", scorerName));
	}
	
	private Scorer getScorer(String scorerNamePath, Config config, ScorerContext context){
		String scorerName = getConfigString(config, scorerNamePath);
		Scorer scorer = (Scorer) context.resolve(Scorer.class, scorerName);
		checkNotNull(scorer);
		return scorer;
	}
	
	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		FeatureScore featureScore = null;
		FeatureScore mainScore = mainScorer.calculateScore(eventMessage);
		if(mainScore != null){
			if(mainScore.getScore() == 0){
				featureScore = mainScore;
			} else{
				FeatureScore reducingScore = reductingScorer.calculateScore(eventMessage);
				if(reducingScore == null){
					featureScore = mainScore;
				} else{
					List<FeatureScore> featureScores = new ArrayList<>();
					featureScores.add(mainScore);
					featureScores.add(reducingScore);
					double score = mainScore.getScore();
					if(reducingScore.getScore() < score){
						double reductingWeightMulitiplyCertainty = reductingWeight * reducingScore.getCertainty(); // The weight of the reducting score depands on the certainty of the score.
						score = reducingScore.getScore() * reductingWeightMulitiplyCertainty + mainScore.getScore() * (1-reductingWeightMulitiplyCertainty);
					}
					featureScore = new FeatureScore(outputFieldName, score, featureScores);
				}
			}
		}
		
		return featureScore;
	}

}
