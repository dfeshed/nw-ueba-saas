package fortscale.ml.scorer;

import java.util.List;

public class ModelFeatureScore extends FeatureScore {

    private double certainty;

    public ModelFeatureScore(String name, Double score, List<FeatureScore> featureScores, double certainty) {
        super(name, score, featureScores);
        this.certainty = certainty;
    }

    public ModelFeatureScore(String name, Double score,	double certainty) {
        super(name, score);
        this.certainty = certainty;
    }

    @Override
    public double getCertainty(){
        return certainty;
    }

}
