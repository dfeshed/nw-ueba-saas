package fortscale.aggregation.feature.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureRetentionStrategy implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private long retentionInSeconds;

    @JsonCreator
    public AggrFeatureRetentionStrategy(@JsonProperty("name") String name, @JsonProperty("retentionInSeconds") long retentionInSeconds) {
        this.name = name;
        this.retentionInSeconds = retentionInSeconds;
    }

    public String getName() {
        return name;
    }

    public long getRetentionInSeconds() {
        return retentionInSeconds;
    }
}
