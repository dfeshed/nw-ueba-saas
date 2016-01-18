package fortscale.ml.scorer.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ModelInfo {

    @JsonProperty("name")
    String modelName;

    public ModelInfo(@JsonProperty("name")String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }
}
