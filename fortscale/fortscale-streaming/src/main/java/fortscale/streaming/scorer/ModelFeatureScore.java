package fortscale.streaming.scorer;

import java.util.List;

public class ModelFeatureScore extends FeatureScore {
	
	private double certainty;

	public ModelFeatureScore(String name, Double score,	List<FeatureScore> featureScores, double certainty) {
		super(name, score, featureScores);
		this.certainty = certainty;
	}

	@Override
	public double getCertainty(){
		return certainty;
	}
}
