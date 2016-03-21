package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.CategoryRarityModelScorer;
import fortscale.ml.scorer.algorithms.CategoryRarityModelScorerAlgorithm;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY)
public class CategoryRarityModelScorerConf extends ModelScorerConf{
    public static final String SCORER_TYPE = "category-rarity-model-scorer";

    @JsonProperty("minimum-number-of-distinct-values-to-influence")
    private int minNumOfDistinctValuesToInfluence;
    @JsonProperty("enough-number-of-distinct-values-to-influence")
    private int enoughNumOfDistinctValuesToInfluence;
    @JsonProperty("max-rare-count")
    private Integer maxRareCount = null;
    @JsonProperty("max-num-of-rare-features")
    private Integer maxNumOfRareFeatures = null;

    @JsonCreator
    public CategoryRarityModelScorerConf(@JsonProperty("name") String name,
                                         @JsonProperty("model") ModelInfo modelInfo,
                                         @JsonProperty("additional-models") List<ModelInfo> additionalModelInfos,
                                         @JsonProperty("max-rare-count")Integer maxRareCount,
                                         @JsonProperty("max-num-of-rare-features") Integer maxNumOfRareFeatures) {
        super(name, modelInfo, additionalModelInfos);
        CategoryRarityModelScorerAlgorithm.assertMaxNumOfRareFeaturesValue(maxNumOfRareFeatures);
        CategoryRarityModelScorerAlgorithm.assertMaxRareCountValue(maxRareCount);
        this.maxRareCount = maxRareCount;
        this.maxNumOfRareFeatures = maxNumOfRareFeatures;
    }

    public CategoryRarityModelScorerConf setMinNumOfDistinctValuesToInfluence(Integer minNumOfDistinctValuesToInfluence) {
        CategoryRarityModelScorer.assertMinNumOfDistinctValuesToInfluenceValue(minNumOfDistinctValuesToInfluence);
        this.minNumOfDistinctValuesToInfluence = minNumOfDistinctValuesToInfluence;
        return this;
    }

    public CategoryRarityModelScorerConf setEnoughNumOfDistinctValuesToInfluence(Integer enoughNumOfDistinctValuesToInfluence) {
        CategoryRarityModelScorer.assertEnoughNumOfDistinctValuesToInfluenceValue(enoughNumOfDistinctValuesToInfluence);
        this.enoughNumOfDistinctValuesToInfluence = enoughNumOfDistinctValuesToInfluence;
        return this;
    }

    public int getMinNumOfDistinctValuesToInfluence() {
        return minNumOfDistinctValuesToInfluence;
    }

    public int getEnoughNumOfDistinctValuesToInfluence() {
        return enoughNumOfDistinctValuesToInfluence;
    }

    public int getMaxRareCount() {
        return maxRareCount;
    }

    public int getMaxNumOfRareFeatures() {
        return maxNumOfRareFeatures;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
