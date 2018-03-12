package presidio.output.processor.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
}