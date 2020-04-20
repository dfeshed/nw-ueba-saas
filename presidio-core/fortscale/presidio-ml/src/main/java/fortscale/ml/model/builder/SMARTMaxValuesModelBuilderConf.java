package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class SMARTMaxValuesModelBuilderConf implements IModelBuilderConf {

    public static final String SMART_MAX_VALUES_MODEL_BUILDER = "smart_max_values_model_builder";
    public static final long DEFAULT_RESOLUTION = 86400;

    @JsonProperty("partitionsResolutionInSeconds")
    private long partitionsResolutionInSeconds = DEFAULT_RESOLUTION;

    public long getPartitionsResolutionInSeconds() {
        return partitionsResolutionInSeconds;
    }

    public void setPartitionsResolutionInSeconds(long partitionsResolutionInSeconds) {
        this.partitionsResolutionInSeconds = partitionsResolutionInSeconds;
    }

    @Override
    public String getFactoryName() {
        return SMART_MAX_VALUES_MODEL_BUILDER;
    }

}
