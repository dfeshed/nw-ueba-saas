package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecord;

import java.util.ArrayList;
import java.util.List;

@Configurable(preConstruction = true)
public class ReductionScorer extends AbstractScorer {
	public static final double REDUCTION_ZERO_SCORE_WEIGHT_DEFAULT = 0.95;

	private Scorer mainScorer;
	private Scorer reductionScorer;
	private double reductionWeight;
	private double reductionZeroScoreWeight = REDUCTION_ZERO_SCORE_WEIGHT_DEFAULT;

	public ReductionScorer(String scorerName, Scorer mainScorer, Scorer reductionScorer, Double reductingWeight, Double reductingZeroScoreWeight) {
		this(scorerName, mainScorer, reductionScorer, reductingWeight);
		Assert.notNull(reductingZeroScoreWeight, "Reducting zero score weight cannot be null");
		Assert.isTrue(reductingZeroScoreWeight>0 && reductingZeroScoreWeight < 1.0, String.format("reductionZeroScoreWeight (%f) must be > 0 and < 1.0", reductingZeroScoreWeight));
		this.reductionZeroScoreWeight = reductingZeroScoreWeight;
	}

	public ReductionScorer(String scorerName, Scorer mainScorer, Scorer reductionScorer, Double reductionWeight) {
		super(scorerName);
		Assert.notNull(mainScorer, "Main scorer must not be null");
		Assert.notNull(reductionScorer, "Reduction scorer must not be null");
		Assert.notNull(reductionWeight, "Reduction weight cannot be null");
		Assert.isTrue(reductionWeight>0 && reductionWeight < 1.0,String.format("reductionWeight (%f) must be > 0 and < 1.0", reductionWeight));
		this.mainScorer = mainScorer;
		this.reductionScorer = reductionScorer;
		this.reductionWeight = reductionWeight;
	}

	@Override
	public FeatureScore calculateScore(AdeRecord record) {
		FeatureScore featureScore = null;
		FeatureScore mainScore = mainScorer.calculateScore(record);
		if(mainScore != null){
			if(mainScore.getScore() == 0){
				// get the score from the main scorer, but replace the output field name
				featureScore = new FeatureScore(getName(), mainScore.getScore());
			} else{
				FeatureScore reducingScore = reductionScorer.calculateScore(record);
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
							reductingWeightMulitiplyCertainty = reductionZeroScoreWeight * reducingScore.getCertainty();
						} else{
							reductingWeightMulitiplyCertainty = reductionWeight * reducingScore.getCertainty();
						}
						score = reducingScore.getScore() * reductingWeightMulitiplyCertainty + mainScore.getScore() * (1-reductingWeightMulitiplyCertainty);
					}
					featureScore = new FeatureScore(getName(), score, featureScores);
				}
			}
		}

		return featureScore;
	}

	public Scorer getMainScorer() {
		return mainScorer;
	}

	public Scorer getReductionScorer() {
		return reductionScorer;
	}

	public double getReductionWeight() {
		return reductionWeight;
	}

	public double getReductionZeroScoreWeight() {
		return reductionZeroScoreWeight;
	}
}
