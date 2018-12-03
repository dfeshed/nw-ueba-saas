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

    public static final int NUM_RARE_EVENTS_FACTOR = 1;
    public static final double X_WITH_VALUE_HALF_FACTOR = 0.25;
    public static final double MIN_PROBABILITY = 0.7;

    @JsonProperty("minimum-number-of-distinct-values-to-influence")
    private int minNumOfDistinctValuesToInfluence;
    @JsonProperty("enough-number-of-distinct-values-to-influence")
    private int enoughNumOfDistinctValuesToInfluence;
    @JsonProperty("max-rare-count")
    private Integer maxRareCount = null;
    @JsonProperty("max-num-of-rare-partitions")
    private Integer maxNumOfRarePartitions = null;
    @JsonProperty("x-with-value-half-factor")
    private double xWithValueHalfFactor = X_WITH_VALUE_HALF_FACTOR;
    @JsonProperty("min-probability")
    private double minProbability = MIN_PROBABILITY;

    @JsonCreator
    public CategoryRarityModelScorerConf(@JsonProperty("name") String name,
                                         @JsonProperty("model") ModelInfo modelInfo,
                                         @JsonProperty("additional-models") List<ModelInfo> additionalModelInfos,
                                         @JsonProperty("max-rare-count")Integer maxRareCount,
                                         @JsonProperty("max-num-of-rare-partitions") Integer maxNumOfRarePartitions) {
        super(name, modelInfo, additionalModelInfos);
        CategoryRarityModelScorerAlgorithm.assertMaxNumOfRarePartitionsValue(maxNumOfRarePartitions);
        CategoryRarityModelScorerAlgorithm.assertMaxRareCountValue(maxRareCount);
        this.maxRareCount = maxRareCount;
        this.maxNumOfRarePartitions = maxNumOfRarePartitions;
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

    public int getMaxNumOfRarePartitions() {
        return maxNumOfRarePartitions;
    }

    public double getXWithValueHalfFactor() {
        return xWithValueHalfFactor;
    }

    public void setXWithValueHalfFactor(double xWithValueHalfFactor) {
        this.xWithValueHalfFactor = xWithValueHalfFactor;
    }

    public double getMinProbability() {
        return minProbability;
    }

    public void setMinProbability(double minProbability) {
        this.minProbability = minProbability;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
