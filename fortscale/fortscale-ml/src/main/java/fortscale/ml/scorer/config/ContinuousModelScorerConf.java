package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ContinuousModelScorerConf extends ModelScorerConf{
    public static final String SCORER_TYPE = "continuous-model-scorer";

    public ContinuousModelScorerConf(@JsonProperty("name") String name, @JsonProperty("number-of-samples-to-influence-enought") int enoughNumOfSamplesToInfluence, @JsonProperty("use-certainty-to-calculate-score") boolean isUseCertaintyToCalculateScore, @JsonProperty("min-number-of-samples-to-influence") int minNumOfSamplesToInfluence, @JsonProperty("model") ModelInfo modelInfo) {
        super(name, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore, minNumOfSamplesToInfluence, modelInfo);
    }

}
