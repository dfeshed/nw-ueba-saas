package fortscale.ml.model.builder.gaussian;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.builder.IModelBuilderConf;

/**
 * Created by YaronDL on 9/24/2017.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ContinuousMaxHistogramModelBuilderConf implements IModelBuilderConf {
    public static final String CONTINUOUS_MAX_HISTOGRAM_MODEL_BUILDER = "continuous_max_histogram_model_builder";

    public static final int NUM_OF_MAX_VALUES_SAMPLES = 30;
    public static final int MIN_NUM_OF_MAX_VALUES_SAMPLES = 20;
    public static final long DEFAULT_RESOLUTION = 86400;
    public static final int DEFAULT_RESOLUTION_STEP = 2;
    public static final int DEFAULT_MIN_RESOLUTION = 1;

    @JsonProperty("numOfMaxValuesSamples")
    private int numOfMaxValuesSamples = NUM_OF_MAX_VALUES_SAMPLES;

    @JsonProperty("minNumOfMaxValuesSamples")
    private int minNumOfMaxValuesSamples = MIN_NUM_OF_MAX_VALUES_SAMPLES;

    @JsonProperty("partitionsResolutionInSeconds")
    private long partitionsResolutionInSeconds = DEFAULT_RESOLUTION;

    @JsonProperty("resolutionStep")
    private int resolutionStep = DEFAULT_RESOLUTION_STEP;

    @JsonProperty("minResolution")
    private int minResolution = DEFAULT_MIN_RESOLUTION;

    public ContinuousMaxHistogramModelBuilderConf(){}

    public int getNumOfMaxValuesSamples() {
        return numOfMaxValuesSamples;
    }

    public void setNumOfMaxValuesSamples(int numOfMaxValuesSamples) {
        this.numOfMaxValuesSamples = numOfMaxValuesSamples;
    }

    public int getMinNumOfMaxValuesSamples() {
        return minNumOfMaxValuesSamples;
    }

    public void setMinNumOfMaxValuesSamples(int minNumOfMaxValuesSamples) {
        this.minNumOfMaxValuesSamples = minNumOfMaxValuesSamples;
    }

    public long getPartitionsResolutionInSeconds() {
        return partitionsResolutionInSeconds;
    }

    public int getResolutionStep() {
        return resolutionStep;
    }

    public void setPartitionsResolutionInSeconds(long partitionsResolutionInSeconds) {
        this.partitionsResolutionInSeconds = partitionsResolutionInSeconds;
    }

    public int getMinResolution() {
        return minResolution;
    }

    @Override
    public String getFactoryName() {
        return CONTINUOUS_MAX_HISTOGRAM_MODEL_BUILDER;
    }
}
