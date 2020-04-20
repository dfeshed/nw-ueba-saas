package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;


import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class ScorerContainerConf extends AbstractScorerConf {
    static final public String NULL_SCORER_CONF_LIST_ERROR_MSG = "scorerConfList must not be null";
    static final public String EMPTY_SCORER_CONF_LIST_ERROR_MSG = "scorerConfList must hold at least one scorer configuration";

    @JsonProperty("scorers")
    List<IScorerConf> scorerConfList;

    public ScorerContainerConf(@JsonProperty("name") String name,
                               @JsonProperty("scorers")List<IScorerConf> scorerConfList) {
        super(name);
        Assert.notNull(scorerConfList, NULL_SCORER_CONF_LIST_ERROR_MSG);
        Assert.isTrue(!scorerConfList.isEmpty(), EMPTY_SCORER_CONF_LIST_ERROR_MSG);
        this.scorerConfList = scorerConfList;
    }

    public List<IScorerConf> getScorerConfList() {
        return scorerConfList;
    }

}
