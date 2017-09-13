package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.SMARTValuesModelScorer;
import fortscale.ml.scorer.algorithms.SMARTValuesModelScorerAlgorithm;
import org.springframework.util.Assert;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY)
public class SMARTValuesModelScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "smart-values-model-scorer";

    public static final int ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE = 1;
    public static final int MIN_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE = 1;
    public static final boolean IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEFAULT_VALUE = false;

    @JsonProperty("global-influence")
    private int globalInfluence;
    @JsonProperty("base-scorer")
    private IScorerConf baseScorerConf;

    @JsonProperty("number-of-samples-to-influence-enough")
    private int enoughNumOfSamplesToInfluence = ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE;

    private boolean isUseCertaintyToCalculateScore = IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEFAULT_VALUE;
    @JsonProperty("model")
    private ModelInfo modelInfo;
    @JsonProperty("global-model")
    private ModelInfo globalModelInfo;
    @JsonProperty("min-number-of-samples-to-influence")
    private int minNumOfSamplesToInfluence = MIN_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE;

    @JsonCreator
    public SMARTValuesModelScorerConf(@JsonProperty("name") String name,
                                      @JsonProperty("model") ModelInfo modelInfo,
                                      @JsonProperty("global-model") ModelInfo globalModelInfo,
                                      @JsonProperty("base-scorer") IScorerConf baseScorerConf,
                                      @JsonProperty("global-influence") Integer globalInfluence) {
        super(name);
        Assert.notNull(modelInfo, "model conf should not be null");
        Assert.notNull(globalModelInfo, "global model conf should not be null");
        Assert.notNull(baseScorerConf, "base score conf should not be null");

        SMARTValuesModelScorerAlgorithm.assertGlobalInfluence(globalInfluence);

        this.modelInfo = modelInfo;
        this.globalModelInfo = globalModelInfo;
        this.baseScorerConf = baseScorerConf;
        this.globalInfluence = globalInfluence;
    }

    public void setEnoughNumOfSamplesToInfluence(int enoughNumOfSamplesToInfluence) {
        SMARTValuesModelScorer.assertEnoughNumOfSamplesToInfluence(enoughNumOfSamplesToInfluence);
        this.enoughNumOfSamplesToInfluence = enoughNumOfSamplesToInfluence;
    }

    @JsonProperty("use-certainty-to-calculate-score")
    public void setUseCertaintyToCalculateScore(boolean useCertaintyToCalculateScore) {
        isUseCertaintyToCalculateScore = useCertaintyToCalculateScore;
    }

    public void setMinNumOfSamplesToInfluence(int minNumOfSamplesToInfluence) {
        SMARTValuesModelScorer.assertMinNumOfSamplesToInfluenceValue(minNumOfSamplesToInfluence);
        this.minNumOfSamplesToInfluence = minNumOfSamplesToInfluence;
    }

    public int getEnoughNumOfSamplesToInfluence() {
        return enoughNumOfSamplesToInfluence;
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

    public int getMinNumOfSamplesToInfluence() {
        return minNumOfSamplesToInfluence;
    }

    public int getGlobalInfluence() {
        return globalInfluence;
    }

    public IScorerConf getBaseScorerConf() {
        return baseScorerConf;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
