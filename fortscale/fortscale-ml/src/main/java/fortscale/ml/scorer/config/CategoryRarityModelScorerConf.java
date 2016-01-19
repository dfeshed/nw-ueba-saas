package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class CategoryRarityModelScorerConf extends ModelScorerConf{
    public static final String SCORER_TYPE = "category-rarity-model-scorer";

    @JsonProperty("minumum-number-of-discreet-values-to-influence")
    private int minNumOfDiscreetValuesToInfluence;
    @JsonProperty("enough-number-of-discreet-values-to-influence")
    private int enoughNumOfDiscreetValuesToInfluence;
    @JsonProperty("max-rare-count")
    private int maxRareCount;
    @JsonProperty("max-num-of-rare-features")
    private int maxNumOfRareFeatures;

    public CategoryRarityModelScorerConf(@JsonProperty("name") String name,
                                         @JsonProperty("number-of-samples-to-influence-enought") int enoughNumOfSamplesToInfluence,
                                         @JsonProperty("use-certainty-to-calculate-score") boolean isUseCertaintyToCalculateScore,
                                         @JsonProperty("min-number-of-samples-to-influence") int minNumOfSamplesToInfluence,
                                         @JsonProperty("model") ModelInfo modelInfo,
                                         @JsonProperty("minumum-number-of-discreet-values-to-influence") int minNumOfDiscreetValuesToInfluence,
                                         @JsonProperty("enough-number-of-discreet-values-to-influence") int enoughNumOfDiscreetValuesToInfluence,
                                         @JsonProperty("max-rare-count")int maxRareCount,
                                         @JsonProperty("max-num-of-rare-features") int maxNumOfRareFeatures) {
        super(name, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore, minNumOfSamplesToInfluence, modelInfo);
        this.minNumOfDiscreetValuesToInfluence = minNumOfDiscreetValuesToInfluence;
        this.enoughNumOfDiscreetValuesToInfluence = enoughNumOfDiscreetValuesToInfluence;
        this.maxRareCount = maxRareCount;
        this.maxNumOfRareFeatures = maxNumOfRareFeatures;
    }

    public int getMinNumOfDiscreetValuesToInfluence() {
        return minNumOfDiscreetValuesToInfluence;
    }

    public int getEnoughNumOfDiscreetValuesToInfluence() {
        return enoughNumOfDiscreetValuesToInfluence;
    }

    public int getMaxRareCount() {
        return maxRareCount;
    }

    public int getMaxNumOfRareFeatures() {
        return maxNumOfRareFeatures;
    }
}
