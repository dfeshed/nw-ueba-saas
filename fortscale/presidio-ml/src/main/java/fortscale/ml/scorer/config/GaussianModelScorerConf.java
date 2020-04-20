package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.algorithms.SMARTValuesModelScorerAlgorithm;
import org.springframework.util.Assert;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY)
public class GaussianModelScorerConf extends ModelScorerConf{
    public static final String SCORER_TYPE = "gaussian-model-scorer";

    @JsonProperty("global-influence")
    private int globalInfluence;

    @JsonCreator
    public GaussianModelScorerConf(@JsonProperty("name") String name,
								   @JsonProperty("model") ModelInfo modelInfo,
								   @JsonProperty("additional-models") List<ModelInfo> additionalModelInfos,
								   @JsonProperty("global-influence") Integer globalInfluence) {
        super(name, modelInfo, additionalModelInfos);
        Assert.isTrue(additionalModelInfos == null || additionalModelInfos.size() <= 1,
                "additional model infos should not contain more than 1 model.");
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
