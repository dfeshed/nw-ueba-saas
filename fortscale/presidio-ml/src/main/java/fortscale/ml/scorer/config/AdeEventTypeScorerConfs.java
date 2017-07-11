package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AdeEventTypeScorerConfs {

    @JsonProperty("ade-event-type")
    private String adeEventType;

    @JsonProperty("scorers")
    private List<IScorerConf> scorerConfs;

    public AdeEventTypeScorerConfs(@JsonProperty("ade-event-type") String adeEventType, @JsonProperty("scorers") List<IScorerConf> scorerConfs) {
        this.adeEventType = adeEventType;
        this.scorerConfs = scorerConfs;
    }

    public String getAdeEventType() {
        return adeEventType;
    }

    public List<IScorerConf> getScorerConfs() {
        return scorerConfs;
    }
}
