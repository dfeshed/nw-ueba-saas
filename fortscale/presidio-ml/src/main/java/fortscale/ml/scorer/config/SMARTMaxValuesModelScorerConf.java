package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.SMARTValuesModelScorer;
import fortscale.ml.scorer.algorithms.SMARTValuesModelScorerAlgorithm;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY)
public class SMARTMaxValuesModelScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "smart-max-values-model-scorer";

    public static final int ENOUGH_NUM_OF_PARTITIONS_TO_INFLUENCE_DEFAULT_VALUE = 1;
    public static final int MIN_NUM_OF_PARTITIONS_TO_INFLUENCE_DEFAULT_VALUE = 1;
    public static final boolean IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEFAULT_VALUE = false;

    @JsonProperty("global-influence")
    private int globalInfluence;
    @JsonProperty("max-user-influence")
    private int maxUserInfluence;
    @JsonProperty("num-of-partition-user-influence")
    private int numOfPartitionUserInfluence;
    @JsonProperty("min-num-of-user-values")
    private int minNumOfUserValues;
    @JsonProperty("base-scorer")
    private IScorerConf baseScorerConf;

    @JsonProperty("number-of-partitions-to-influence-enough")
    private int enoughNumOfPartitionsToInfluence = ENOUGH_NUM_OF_PARTITIONS_TO_INFLUENCE_DEFAULT_VALUE;

    private boolean isUseCertaintyToCalculateScore = IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEFAULT_VALUE;
    @JsonProperty("model")
    private ModelInfo modelInfo;
    @JsonProperty("global-model")
    private ModelInfo globalModelInfo;
    @JsonProperty("min-number-of-partitions-to-influence")
    private int minNumOfPartitionsToInfluence = MIN_NUM_OF_PARTITIONS_TO_INFLUENCE_DEFAULT_VALUE;

    @JsonCreator
    public SMARTMaxValuesModelScorerConf(@JsonProperty("name") String name,
                                         @JsonProperty("model") ModelInfo modelInfo,
                                         @JsonProperty("global-model") ModelInfo globalModelInfo,
                                         @JsonProperty("base-scorer") IScorerConf baseScorerConf,
                                         @JsonProperty("global-influence") Integer globalInfluence,
                                         @JsonProperty("max-user-influence") Integer maxUserInfluence,
                                         @JsonProperty("num-of-partition-user-influence") Integer numOfPartitionUserInfluence,
                                         @JsonProperty("min-num-of-user-values") Integer minNumOfUserValues) {
        super(name);
        Assert.notNull(modelInfo, "model conf should not be null");
        Assert.notNull(globalModelInfo, "global model conf should not be null");
        Assert.notNull(baseScorerConf, "base score conf should not be null");
        Assert.isTrue(globalInfluence >= 0, String.format("globalInfluence must be >= 0: %d", globalInfluence));
        Assert.isTrue(numOfPartitionUserInfluence > 0, String.format("numOfPartitionUserInfluence must be > 0: %d", numOfPartitionUserInfluence));
        Assert.isTrue(maxUserInfluence >= minNumOfUserValues, String.format("maxUserInfluence %d must be at least as minNumOfUserValues %d", maxUserInfluence, minNumOfUserValues));


        SMARTValuesModelScorerAlgorithm.assertGlobalInfluence(globalInfluence);

        this.modelInfo = modelInfo;
        this.globalModelInfo = globalModelInfo;
        this.baseScorerConf = baseScorerConf;
        this.globalInfluence = globalInfluence;
        this.maxUserInfluence = maxUserInfluence;
        this.numOfPartitionUserInfluence = numOfPartitionUserInfluence;
        this.minNumOfUserValues = minNumOfUserValues;
    }

    public void setEnoughNumOfPartitionsToInfluence(int enoughNumOfPartitionsToInfluence) {
        SMARTValuesModelScorer.assertEnoughNumOfPartitionsToInfluence(enoughNumOfPartitionsToInfluence);
        this.enoughNumOfPartitionsToInfluence = enoughNumOfPartitionsToInfluence;
    }

    @JsonProperty("use-certainty-to-calculate-score")
    public void setUseCertaintyToCalculateScore(boolean useCertaintyToCalculateScore) {
        isUseCertaintyToCalculateScore = useCertaintyToCalculateScore;
    }

    public void setMinNumOfPartitionsToInfluence(int minNumOfPartitionsToInfluence) {
        SMARTValuesModelScorer.assertMinNumOfPartitionsToInfluenceValue(minNumOfPartitionsToInfluence);
        this.minNumOfPartitionsToInfluence = minNumOfPartitionsToInfluence;
    }

    public int getEnoughNumOfPartitionsToInfluence() {
        return enoughNumOfPartitionsToInfluence;
    }

    @JsonProperty("use-certainty-to-calculate-score")
    public boolean isUseCertaintyToCalculateScore() {
        return isUseCertaintyToCalculateScore;
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    public ModelInfo getGlobalModelInfo() {
        return globalModelInfo;
    }

    public int getMinNumOfPartitionsToInfluence() {
        return minNumOfPartitionsToInfluence;
    }

    public int getGlobalInfluence() {
        return globalInfluence;
    }

    public IScorerConf getBaseScorerConf() {
        return baseScorerConf;
    }

    public int getMaxUserInfluence() {
        return maxUserInfluence;
    }

    public int getNumOfPartitionUserInfluence() {
        return numOfPartitionUserInfluence;
    }

    public int getMinNumOfUserValues() {
        return minNumOfUserValues;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
