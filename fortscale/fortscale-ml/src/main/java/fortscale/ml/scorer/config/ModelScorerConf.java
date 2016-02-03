package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.AbstractModelScorer;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class ModelScorerConf extends AbstractScorerConf{

    public static final int ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE = 1;
    public static final int MIN_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE = 1;
    public static final boolean IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEAFEST_VALUE = false;

    @JsonProperty("number-of-samples-to-influence-enough")
    private int enoughNumOfSamplesToInfluence = ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE; //TODO: what of the names is better?
    @JsonProperty("use-certainty-to-calculate-score")
    private boolean isUseCertaintyToCalculateScore = IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEAFEST_VALUE;
    @JsonProperty("model")
    private ModelInfo modelInfo;
    @JsonProperty("min-number-of-samples-to-influence")
    private int minNumOfSamplesToInfluence = MIN_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE;

    @JsonCreator
    public ModelScorerConf(@JsonProperty("name") String name,
                           @JsonProperty("model") ModelInfo modelInfo) {

        super(name);
        Assert.notNull(modelInfo);
        Assert.isTrue(!StringUtils.isEmpty(modelInfo.getModelName()) && StringUtils.isNotBlank(modelInfo.getModelName()), "model name must be provided and cannot be blank.");
        this.modelInfo = modelInfo;

    }

    public void setEnoughNumOfSamplesToInfluence(int enoughNumOfSamplesToInfluence) {
        AbstractModelScorer.assertEnoughNumOfSamplesToInfluence(enoughNumOfSamplesToInfluence);
        this.enoughNumOfSamplesToInfluence = enoughNumOfSamplesToInfluence;
    }

    public void setUseCertaintyToCalculateScore(boolean useCertaintyToCalculateScore) {
        isUseCertaintyToCalculateScore = useCertaintyToCalculateScore;
    }

    public void setMinNumOfSamplesToInfluence(int minNumOfSamplesToInfluence) {
        AbstractModelScorer.assertMinNumOfSamplesToInfluenceValue(minNumOfSamplesToInfluence);
        this.minNumOfSamplesToInfluence = minNumOfSamplesToInfluence;
    }

    public int getEnoughNumOfSamplesToInfluence() {
        return enoughNumOfSamplesToInfluence;
    }

    public boolean isUseCertaintyToCalculateScore() {
        return isUseCertaintyToCalculateScore;
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    public int getMinNumOfSamplesToInfluence() {
        return minNumOfSamplesToInfluence;
    }

}
