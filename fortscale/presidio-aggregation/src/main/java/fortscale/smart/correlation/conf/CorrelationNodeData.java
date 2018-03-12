package fortscale.smart.correlation.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class CorrelationNodeData {

    private String feature;
    private Double correlationFactor;

    @JsonCreator
    public CorrelationNodeData(@JsonProperty("feature") String feature,
                               @JsonProperty("correlationFactor") Double correlationFactor) {
        this.feature = feature;
        this.correlationFactor = correlationFactor;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Double getCorrelationFactor() {
        return correlationFactor;
    }

    public void setCorrelationFactor(Double correlationFactor) {
        this.correlationFactor = correlationFactor;
    }
}
