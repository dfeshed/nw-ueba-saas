package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY)
public class SmartWeightsModelScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "smart-weights-model-scorer";

    @JsonProperty("model")
    private ModelInfo modelInfo;

    @JsonCreator
    public SmartWeightsModelScorerConf(@JsonProperty("name") String name,
                                       @JsonProperty("model") ModelInfo modelInfo) {
        super(name);
        Assert.notNull(modelInfo, "modelInfo should not be null");

        this.modelInfo = modelInfo;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }
}
