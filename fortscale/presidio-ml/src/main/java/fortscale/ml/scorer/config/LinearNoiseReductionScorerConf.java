package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.ReductionScorer;
import fortscale.ml.scorer.ScoreMapping;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY)
public class LinearNoiseReductionScorerConf extends ModelScorerConf{
    public static final String SCORER_TYPE = "linear-noise-reduction-scorer";

    @JsonProperty("main-scorer")
    private IScorerConf mainScorerConf;
    @JsonProperty("reduction-scorer")
    private IScorerConf reductionScorerConf;
    @JsonProperty("noise-reduction-weight")
    private ScoreMapping.ScoreMappingConf noiseReductionWeight;
    @JsonProperty("reduction-model")
    ModelInfo reductionModelInfo;


    @JsonCreator
    public LinearNoiseReductionScorerConf(@JsonProperty("name") String name,
                                          @JsonProperty("main-scorer") IScorerConf mainScorerConf,
                                          @JsonProperty("reduction-scorer") IScorerConf reductionScorerConf,
                                          @JsonProperty("model") ModelInfo modelInfo,
                                          @JsonProperty("additional-models") List<ModelInfo> additionalModelInfos,
                                          @JsonProperty("reduction-models") List<ModelInfo> reductionModelInfo,
                                          @JsonProperty("noise-reduction-weight") ScoreMapping.ScoreMappingConf noiseReductionWeight
                                          ) {
        super(name, modelInfo, additionalModelInfos);
        Assert.notNull(mainScorerConf, "mainScorerConf must not be null");
        Assert.notNull(reductionScorerConf, "reductionScorerConf must not be null");
        Assert.notNull(noiseReductionWeight, "noiseReductionWeight must not be null");

        this.mainScorerConf = mainScorerConf;
        this.reductionScorerConf = reductionScorerConf;
        this.noiseReductionWeight = noiseReductionWeight;
    }

    public IScorerConf getMainScorerConf() {
        return mainScorerConf;
    }

    public IScorerConf getReductionScorerConf() {
        return reductionScorerConf;
    }

    public ModelInfo getReductionModelInfo() {
        return reductionModelInfo;
    }

    public ScoreMapping.ScoreMappingConf getNoiseReductionWeight() {
        return noiseReductionWeight;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
