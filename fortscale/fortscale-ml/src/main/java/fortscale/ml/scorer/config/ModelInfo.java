package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ModelInfo {
    @JsonProperty("name")
    private String modelName;

    public ModelInfo(@JsonProperty("name") String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }
}
