package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;

import java.util.ArrayList;
import java.util.List;

import org.apache.samza.config.Config;

public class ReductingScorer extends AbstractScorer {
	
	public static final double REDUCTING_ZERO_SCORE_WEIGHT_DEFAULT = 0.95;
	
	private Scorer mainScorer;
	private Scorer reductingScorer;
	private double reductingWeight;
	private double reductingZeroScoreWeight;

	public ReductingScorer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config, context);
		mainScorer = getScorer(String.format("fortscale.score.%s.main.scorer", scorerName), config, context);
		reductingScorer = getScorer(String.format("fortscale.score.%s.reducting.scorer", scorerName), config, context);
		reductingWeight = config.getDouble(String.format("fortscale.score.%s.reducting.weight", scorerName));
		reductingZeroScoreWeight = config.getDouble(String.format("fortscale.score.%s.reducting.zero.score.weight", scorerName), REDUCTING_ZERO_SCORE_WEIGHT_DEFAULT);
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
				// get the score from the main scorer, but replace the output field name
				featureScore = new FeatureScore(outputFieldName, mainScore.getScore());
			} else{
				FeatureScore reducingScore = reductingScorer.calculateScore(eventMessage);
				if(reducingScore == null){
					// get the score from the main scorer, but replace the output field name
					featureScore = new FeatureScore(outputFieldName, mainScore.getScore());
				} else{
					List<FeatureScore> featureScores = new ArrayList<>();
					featureScores.add(mainScore);
					featureScores.add(reducingScore);
					double score = mainScore.getScore();
					if(reducingScore.getScore() < score){
						// The weight of the reducting score depends on the certainty of the score.
						double reductingWeightMulitiplyCertainty = 0;
						if(reducingScore.getScore() == 0){
							reductingWeightMulitiplyCertainty = reductingZeroScoreWeight * reducingScore.getCertainty();
						} else{
							reductingWeightMulitiplyCertainty = reductingWeight * reducingScore.getCertainty();
						}
						score = reducingScore.getScore() * reductingWeightMulitiplyCertainty + mainScore.getScore() * (1-reductingWeightMulitiplyCertainty);
					}
					featureScore = new FeatureScore(outputFieldName, score, featureScores);
				}
			}
		}
		
		return featureScore;
	}

}
