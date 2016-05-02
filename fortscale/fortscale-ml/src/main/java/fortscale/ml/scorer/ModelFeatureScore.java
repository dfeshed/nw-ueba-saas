package fortscale.ml.scorer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fortscale.domain.core.FeatureScore;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ModelFeatureScore extends FeatureScore {

    private double certainty;

    public ModelFeatureScore(){}

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

    public void setCertainty(double certainty) {
        this.certainty = certainty;
    }
}
