package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.data.annotation.Transient;

import java.util.Collections;
import java.util.List;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class FeatureScore implements IFeatureScore{
    private String name;
    private Double score;
    private List<FeatureScore> featureScores;


    public FeatureScore(){

    }

    public FeatureScore(String name, Double score){
        this(name, score, Collections.emptyList());
    }

    public FeatureScore(String name, Double score, List<FeatureScore> featureScores){
        this.name = name;
        this.score = score;
        this.featureScores = featureScores;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setFeatureScores(List<FeatureScore> featureScores) {
        this.featureScores = featureScores;
    }
    public List<FeatureScore> getFeatureScores() {
        return featureScores;
    }

    public FeatureScore getFeatureScore(String featureScorerName){
        for(FeatureScore featureScore: featureScores){
            if(featureScore.getName().equals(featureScorerName)){
                return featureScore;
            }
        }

        return null;
    }

    @Transient
    public double getCertainty() {
        return 1.0d;
    }


    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        FeatureScore other = (FeatureScore) obj;
        return new EqualsBuilder().append(this.name, other.name).append(this.score, other.score).append(this.featureScores, other.featureScores).append(this.getCertainty(), other.getCertainty()).isEquals();
    }

}
