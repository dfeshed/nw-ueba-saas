package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class PartitionsHistogramModelBuilderConf implements IModelBuilderConf {
    public static final String INSTANT_TO_VALUE_HISTOGRAM_MODEL_BUILDER = "partitions_histogram_model_builder";

    public static final int MIN_NUM_OF_MAX_VALUES_SAMPLES = 20;
    public static final long DEFAULT_RESOLUTION = 86400;
    public static final int DEFAULT_RESOLUTION_STEP = 2;


    @JsonProperty("minNumOfMaxValuesSamples")
    private int minNumOfMaxValuesSamples = MIN_NUM_OF_MAX_VALUES_SAMPLES;

    @JsonProperty("partitionsResolutionInSeconds")
    private long partitionsResolutionInSeconds = DEFAULT_RESOLUTION;

    @JsonProperty("resolutionStep")
    private int resolutionStep = DEFAULT_RESOLUTION_STEP;

    public PartitionsHistogramModelBuilderConf(){}

    public int getMinNumOfMaxValuesSamples() {
        return minNumOfMaxValuesSamples;
    }

    public void setMinNumOfMaxValuesSamples(int minNumOfMaxValuesSamples) {
        this.minNumOfMaxValuesSamples = minNumOfMaxValuesSamples;
    }

    public long getPartitionsResolutionInSeconds() {
        return partitionsResolutionInSeconds;
    }

    public void setPartitionsResolutionInSeconds(long partitionsResolutionInSeconds) {
        this.partitionsResolutionInSeconds = partitionsResolutionInSeconds;
    }

    public int getResolutionStep() {
        return resolutionStep;
    }

    @Override
    public String getFactoryName() {
        return INSTANT_TO_VALUE_HISTOGRAM_MODEL_BUILDER;
    }
}
