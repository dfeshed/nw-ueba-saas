package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ModelInfo {
    @JsonProperty("name")
    private String modelName;

    public ModelInfo(@JsonProperty("name") String modelName) {
        Assert.hasText(modelName, "model name must be provided and cannot be blank.");
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }
}
