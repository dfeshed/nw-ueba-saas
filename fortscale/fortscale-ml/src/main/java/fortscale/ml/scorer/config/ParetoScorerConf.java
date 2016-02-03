package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;


import java.util.List;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ParetoScorerConf extends ScorerContainerConf{
    public static final String SCORER_TYPE = "pareto-scorer";

    @JsonProperty("highest-score-weight")
    private Double highestScoreWeight;

    public ParetoScorerConf(@JsonProperty("name") String name,
                            @JsonProperty("highest-score-weight") Double highestScoreWeight,
                            @JsonProperty("scorers")List<IScorerConf> scorerConfList) {
        super(name, scorerConfList);
        Assert.notNull(highestScoreWeight, "highestScoreWeight must not be null");
        Assert.isTrue(highestScoreWeight < 1.0 && highestScoreWeight > 0.0, String.format("highestScoreWeight (%f) must be > 0 AND < 1.0", highestScoreWeight));
        this.highestScoreWeight = highestScoreWeight;
    }

    public Double getHighestScoreWeight() {
        return highestScoreWeight;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
