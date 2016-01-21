package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ContinuousModelScorerConf extends ModelScorerConf{
    public static final String SCORER_TYPE = "continuous-model-scorer";

    @JsonCreator
    public ContinuousModelScorerConf(@JsonProperty("name") String name, @JsonProperty("model") ModelInfo modelInfo) {
        super(name, modelInfo);
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
