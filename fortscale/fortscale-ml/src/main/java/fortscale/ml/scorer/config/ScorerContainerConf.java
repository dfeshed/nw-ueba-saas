package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ScorerContainerConf extends AbstractScorerConf {
    public static final String SCORER_TYPE = "scorer-container";

    @JsonProperty("scorers")
    List<IScorerConf> scorerConfList;

    public ScorerContainerConf(@JsonProperty("name") String name,
                               @JsonProperty("scorers")List<IScorerConf> scorerConfList) {
        super(name);
        this.scorerConfList = scorerConfList;
    }

    public List<IScorerConf> getScorerConfList() {
        return scorerConfList;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
