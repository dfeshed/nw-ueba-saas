package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.SMARTValuesModelScorer;
import fortscale.ml.scorer.algorithms.SMARTValuesModelScorerAlgorithm;
import org.springframework.util.Assert;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY)
public class SMARTValuesModelScorerConf extends ModelScorerConf{
    public static final String SCORER_TYPE = "smart-values-model-scorer";

    @JsonProperty("global-influence")
    private int globalInfluence;

    @JsonCreator
    public SMARTValuesModelScorerConf(@JsonProperty("name") String name,
                                      @JsonProperty("model") ModelInfo modelInfo,
                                      @JsonProperty("additional-models") List<ModelInfo> additionalModelInfos,
                                      @JsonProperty("global-influence") Integer globalInfluence) {
        super(name, modelInfo, additionalModelInfos);
        Assert.isTrue(additionalModelInfos.size() == 1, "one additional model info should be provided");
        SMARTValuesModelScorerAlgorithm.assertGlobalInfluence(globalInfluence);
        this.globalInfluence = globalInfluence;
    }

    public int getGlobalInfluence() {
        return globalInfluence;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
