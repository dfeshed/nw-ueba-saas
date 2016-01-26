package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;


import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class ScorerContainerConf extends AbstractScorerConf {

    @JsonProperty("scorers")
    List<IScorerConf> scorerConfList;

    public ScorerContainerConf(@JsonProperty("name") String name,
                               @JsonProperty("scorers")List<IScorerConf> scorerConfList) {
        super(name);
        Assert.notNull(scorerConfList, "scorerConfList must not be null");
        Assert.isTrue(!scorerConfList.isEmpty(), "scorerConfList must hold at least one scorer configuration");
        this.scorerConfList = scorerConfList;
    }

    public List<IScorerConf> getScorerConfList() {
        return scorerConfList;
    }

}
