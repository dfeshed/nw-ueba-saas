package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

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
        this.highestScoreWeight = highestScoreWeight;
    }

    public Double getHighestScoreWeight() {
        return highestScoreWeight;
    }
}
