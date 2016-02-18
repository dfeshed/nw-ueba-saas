package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class MaxScorerContainerConf extends ScorerContainerConf{
    public static final String SCORER_TYPE = "max-scorer-container";

    public MaxScorerContainerConf(@JsonProperty("name") String name, @JsonProperty("scorers") List<IScorerConf> scorerConfList) {
        super(name, scorerConfList);
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
