package fortscale.domain.core;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FeatureScore.class, name = "feature-score"),
        @JsonSubTypes.Type(value = ModelFeatureScore.class, name = "model-feature-score")
})
public interface IFeatureScore {

    public String getName();

    public Double getScore();

    public List<FeatureScore> getFeatureScores();
    public double getCertainty();

}


