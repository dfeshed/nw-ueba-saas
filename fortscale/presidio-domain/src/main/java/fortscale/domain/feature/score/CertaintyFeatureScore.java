package fortscale.domain.feature.score;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class CertaintyFeatureScore extends FeatureScore {

    private double certainty;

    public CertaintyFeatureScore(){}

    public CertaintyFeatureScore(String name, Double score, List<FeatureScore> featureScores, double certainty) {
        super(name, score, featureScores);
        this.certainty = certainty;
    }

    public CertaintyFeatureScore(String name, Double score, double certainty) {
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
