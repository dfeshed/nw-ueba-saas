package fortscale.ml.scorer;

import fortscale.common.event.EventMessage;
import java.util.ArrayList;
import java.util.List;

public class ReductingScorer extends AbstractScorer {
	
	public static final double REDUCTING_ZERO_SCORE_WEIGHT_DEFAULT = 0.95;
	
	private Scorer mainScorer;
	private Scorer reductingScorer;
	private double reductingWeight;
	private double reductingZeroScoreWeight;

	public ReductingScorer(String scorerName, Scorer mainScorer, Scorer reductingScorer, double reductingWeight, double reductingZeroScoreWeight) {
		super(scorerName);
		this.mainScorer = mainScorer;
		this.reductingScorer = reductingScorer;
		this.reductingWeight = reductingWeight;
		this.reductingZeroScoreWeight = reductingZeroScoreWeight;
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage, long eventEpochTimeInSec) throws Exception {
		FeatureScore featureScore = null;
		FeatureScore mainScore = mainScorer.calculateScore(eventMessage, eventEpochTimeInSec);
		if(mainScore != null){
			if(mainScore.getScore() == 0){
				// get the score from the main scorer, but replace the output field name
				featureScore = new FeatureScore(getName(), mainScore.getScore());
			} else{
				FeatureScore reducingScore = reductingScorer.calculateScore(eventMessage, eventEpochTimeInSec);
				if(reducingScore == null){
					// get the score from the main scorer, but replace the output field name
					featureScore = new FeatureScore(getName(), mainScore.getScore());
				} else{
					List<FeatureScore> featureScores = new ArrayList<>();
					featureScores.add(mainScore);
					featureScores.add(reducingScore);
					double score = mainScore.getScore();
					if(reducingScore.getScore() < score){
						// The weight of the reducting score depends on the certainty of the score.
						double reductingWeightMulitiplyCertainty;
						if(reducingScore.getScore() == 0){
							reductingWeightMulitiplyCertainty = reductingZeroScoreWeight * reducingScore.getCertainty();
						} else{
							reductingWeightMulitiplyCertainty = reductingWeight * reducingScore.getCertainty();
						}
						score = reducingScore.getScore() * reductingWeightMulitiplyCertainty + mainScore.getScore() * (1-reductingWeightMulitiplyCertainty);
					}
					featureScore = new FeatureScore(getName(), score, featureScores);
				}
			}
		}
		
		return featureScore;
	}

}
