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
public class LinearNoiseReductionScorerConf extends AbstractScorerConf {
    public static final String SCORER_TYPE = "linear-noise-reduction-scorer";
    public static final double X_WITH_VALUE_HALF_FACTOR = 0.25;
    public static final int MAX_RARE_COUNT = 8;
    //TODO: NOT SURE ABOUT THE VALUE
    private static final double EPSILON_VALUE_FOR_MAX_X = 0.0099999999;

    @JsonProperty("main-scorer")
    private IScorerConf mainScorerConf;
    @JsonProperty("reduction-scorer")
    private IScorerConf reductionScorerConf;
    @JsonProperty("main-scorer-model")
    private ModelInfo mainScorerModelInfo;
    @JsonProperty("category-rarity-global-model")
    private ModelInfo categoryRarityGlobalModelInfo;
    @JsonProperty("context-model")
    ModelInfo contextModelInfo;
    @JsonProperty("noise-reduction-weight")
    private ScoreMapping.ScoreMappingConf noiseReductionWeight;
    @JsonProperty("max-rare-count")
    int maxRareCount = MAX_RARE_COUNT;
    @JsonProperty("x-with-value-half-factor")
    double xWithValueHalfFactor = X_WITH_VALUE_HALF_FACTOR;
    double epsilonValueForMaxX = EPSILON_VALUE_FOR_MAX_X;

    @JsonCreator
    public LinearNoiseReductionScorerConf(@JsonProperty("name") String name,
                                          @JsonProperty("main-scorer") IScorerConf mainScorerConf,
                                          @JsonProperty("reduction-scorer") IScorerConf reductionScorerConf,
                                          @JsonProperty("main-scorer-model") ModelInfo mainScorerModelInfo,
                                          @JsonProperty("category-rarity-global-model") ModelInfo categoryRarityGlobalModelInfo,
                                          @JsonProperty("context-model") ModelInfo contextModelInfo,
                                          @JsonProperty("noise-reduction-weight") ScoreMapping.ScoreMappingConf noiseReductionWeight) {
        super(name);
        Assert.notNull(mainScorerConf, "mainScorerConf must not be null");
        Assert.notNull(reductionScorerConf, "reductionScorerConf must not be null");
        Assert.notNull(mainScorerModelInfo, "mainScorerModelInfo must not be null");
        Assert.notNull(categoryRarityGlobalModelInfo, "categoryRarityGlobalModelInfo must not be null");
        Assert.notNull(contextModelInfo, "contextModelInfo must not be null");
        Assert.notNull(noiseReductionWeight, "noiseReductionWeight must not be null");

        this.mainScorerConf = mainScorerConf;
        this.reductionScorerConf = reductionScorerConf;
        this.mainScorerModelInfo = mainScorerModelInfo;
        this.categoryRarityGlobalModelInfo = categoryRarityGlobalModelInfo;
        this.contextModelInfo = contextModelInfo;
        this.noiseReductionWeight = noiseReductionWeight;
    }

    public IScorerConf getMainScorerConf() {
        return mainScorerConf;
    }

    public IScorerConf getReductionScorerConf() {
        return reductionScorerConf;
    }

    public ModelInfo getMainScorerModelInfo() {
        return mainScorerModelInfo;
    }

    public ModelInfo getCategoryRarityGlobalModelInfo() {
        return categoryRarityGlobalModelInfo;
    }

    public ModelInfo getContextModelInfo() {
        return contextModelInfo;
    }

    public ScoreMapping.ScoreMappingConf getNoiseReductionWeight() {
        return noiseReductionWeight;
    }


    public int getMaxRareCount() {
        return maxRareCount;
    }

    public double getxWithValueHalfFactor() {
        return xWithValueHalfFactor;
    }

    public double getEpsilonValueForMaxX() {
        return epsilonValueForMaxX;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
