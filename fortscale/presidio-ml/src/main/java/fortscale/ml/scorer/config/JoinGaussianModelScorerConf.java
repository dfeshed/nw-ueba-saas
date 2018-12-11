package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.algorithms.SMARTValuesModelScorerAlgorithm;
import org.springframework.util.Assert;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY)
public class JoinGaussianModelScorerConf extends GaussianModelScorerConf {
    public static final String SCORER_TYPE = "join-gaussian-model-scorer";
    public static final int NUM_OF_MAX_VALUES_SAMPLES = 90;
    public static final int MIN_NUM_OF_MAX_VALUES_SAMPLES = 20;
    public static final long DEFAULT_RESOLUTION = 86400;
    public static final int DEFAULT_RESOLUTION_STEP = 2;


    @JsonProperty("secondary-model")
    private ModelInfo secondaryModelInfo;
    @JsonProperty("numOfMaxValuesSamples")
    private int numOfMaxValuesSamples = NUM_OF_MAX_VALUES_SAMPLES;
    @JsonProperty("minNumOfMaxValuesSamples")
    private int minNumOfMaxValuesSamples = MIN_NUM_OF_MAX_VALUES_SAMPLES;
    @JsonProperty("partitionsResolutionInSeconds")
    private long partitionsResolutionInSeconds = DEFAULT_RESOLUTION;
    @JsonProperty("resolutionStep")
    private int resolutionStep = DEFAULT_RESOLUTION_STEP;

    @JsonCreator
    public JoinGaussianModelScorerConf(@JsonProperty("name") String name,
                                       @JsonProperty("model") ModelInfo mainModelsInfo,
                                       @JsonProperty("secondary-model") ModelInfo secondaryModelInfo,
                                       @JsonProperty("additional-models") List<ModelInfo> additionalModelInfos,
                                       @JsonProperty("global-influence") Integer globalInfluence) {
        super(name, mainModelsInfo, additionalModelInfos, globalInfluence);
        Assert.isTrue(additionalModelInfos.size() == 1, "one additional model info should be provided");
        SMARTValuesModelScorerAlgorithm.assertGlobalInfluence(globalInfluence);
        this.secondaryModelInfo = secondaryModelInfo;
    }

    public ModelInfo getSecondaryModelInfo() {
        return secondaryModelInfo;
    }

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

    public void setPartitionsResolutionInSeconds(long partitionsResolutionInSeconds) {
        this.partitionsResolutionInSeconds = partitionsResolutionInSeconds;
    }

    public int getResolutionStep() {
        return resolutionStep;
    }

    public void setResolutionStep(int resolutionStep) {
        this.resolutionStep = resolutionStep;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
