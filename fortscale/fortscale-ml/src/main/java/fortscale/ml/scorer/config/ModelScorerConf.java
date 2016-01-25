package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ModelScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "model-scorer";

    @JsonProperty("number-of-samples-to-influence-enough")
    private int enoughNumOfSamplesToInfluence;
    @JsonProperty("use-certainty-to-calculate-score")
    private boolean isUseCertaintyToCalculateScore = false;
    @JsonProperty("model")
    private ModelInfo modelInfo;
    @JsonProperty("min-number-of-samples-to-influence")
    private int minNumOfSamplesToInfluence;


    public ModelScorerConf(@JsonProperty("name") String name,
                            @JsonProperty("number-of-samples-to-influence-enought") int enoughNumOfSamplesToInfluence,
                            @JsonProperty("use-certainty-to-calculate-score") boolean isUseCertaintyToCalculateScore,
                           @JsonProperty("min-number-of-samples-to-influence") int minNumOfSamplesToInfluence,
                            @JsonProperty("model") ModelInfo modelInfo) {
        super(name);
        this.enoughNumOfSamplesToInfluence = enoughNumOfSamplesToInfluence;
        this.isUseCertaintyToCalculateScore = isUseCertaintyToCalculateScore;
        this.minNumOfSamplesToInfluence = minNumOfSamplesToInfluence;
        this.modelInfo = modelInfo;
    }


    public int getEnoughNumOfSamplesToInfluence() {
        return enoughNumOfSamplesToInfluence;
    }

    public boolean isUseCertaintyToCalculateScore() {
        return isUseCertaintyToCalculateScore;
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    public int getMinNumOfSamplesToInfluence() {
        return minNumOfSamplesToInfluence;
    }
}
