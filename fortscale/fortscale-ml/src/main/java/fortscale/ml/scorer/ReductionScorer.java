package fortscale.ml.scorer;

import fortscale.common.event.EventMessage;
import org.eclipse.jdt.internal.core.Assert;

import java.util.ArrayList;
import java.util.List;

public class ReductionScorer extends AbstractScorer {
	
	public static final double REDUCTING_ZERO_SCORE_WEIGHT_DEFAULT = 0.95;
	
	private Scorer mainScorer;
	private Scorer reductingScorer;
	private double reductingWeight;
	private double reductingZeroScoreWeight = REDUCTING_ZERO_SCORE_WEIGHT_DEFAULT;

	public ReductionScorer(String scorerName, Scorer mainScorer, Scorer reductingScorer, double reductingWeight, double reductingZeroScoreWeight) {
		this(scorerName, mainScorer, reductingScorer, reductingWeight);
		Assert.isTrue(reductingZeroScoreWeight>0 && reductingZeroScoreWeight < 1.0, String.format("reductingZeroScoreWeight (%f) must be > 0 and < 1.0", reductingZeroScoreWeight));
		this.reductingZeroScoreWeight = reductingZeroScoreWeight;
	}

	public ReductionScorer(String scorerName, Scorer mainScorer, Scorer reductingScorer, double reductingWeight) {
		super(scorerName);
		Assert.isNotNull(mainScorer, "main scorer must not be null");
		Assert.isNotNull(reductingScorer, "reducting scorer must not be null");
		Assert.isTrue(reductingWeight>0 && reductingWeight < 1.0,String.format("reductingWeight (%f) must be > 0 and < 1.0", reductingWeight));
		this.mainScorer = mainScorer;
		this.reductingScorer = reductingScorer;
		this.reductingWeight = reductingWeight;
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
