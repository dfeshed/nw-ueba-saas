package presidio.output.processor.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "featureBucketConfName",
        "featureName"
})
public class HistoricalDataConfig {

    @JsonProperty("type")
    private String type;

    @JsonProperty("featureBucketConfName")
    private String featureBucketConfName;

    @JsonProperty("featureName")
    private String featureName;

    @JsonProperty("contexts")
    private List<String> contexts;

    @JsonProperty("skipAnomaly")
    private Boolean skipAnomaly;

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("featureBucketConfName")
    public String getFeatureBucketConfName() {
        return featureBucketConfName;
    }

    @JsonProperty("featureBucketConfName")
    public void setFeatureBucketConfName(String featureBucketConfName) {
        this.featureBucketConfName = featureBucketConfName;
    }

    @JsonProperty("featureName")
    public String getFeatureName() {
        return featureName;
    }

    @JsonProperty("featureName")
    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    @JsonProperty("contexts")
    public List<String> getContexts() {
        return contexts;
    }

    @JsonProperty("contexts")
    public void setContexts(List<String> contexts) {
        this.contexts = contexts;
    }

    @JsonProperty("skipAnomaly")
    public Boolean getSkipAnomaly() {
        return skipAnomaly;
    }

    @JsonProperty("skipAnomaly")
    public void setSkipAnomaly(Boolean skipAnomaly) {
        this.skipAnomaly = skipAnomaly;
    }
}