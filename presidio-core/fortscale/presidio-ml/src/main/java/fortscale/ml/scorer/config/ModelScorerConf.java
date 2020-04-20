package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.AbstractModelScorer;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY, isGetterVisibility = Visibility.ANY)
public abstract class ModelScorerConf extends AbstractScorerConf {
    public static final int ENOUGH_NUM_OF_PARTITIONS_TO_INFLUENCE_DEFAULT_VALUE = 1;
    public static final int MIN_NUM_OF_PARTITIONS_TO_INFLUENCE_DEFAULT_VALUE = 1;
    public static final boolean IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEFAULT_VALUE = false;

    @JsonProperty("number-of-partitions-to-influence-enough")
    private int enoughNumOfSamplesToInfluence = ENOUGH_NUM_OF_PARTITIONS_TO_INFLUENCE_DEFAULT_VALUE;

    private boolean isUseCertaintyToCalculateScore = IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEFAULT_VALUE;
    @JsonProperty("model")
    private ModelInfo modelInfo;
    @JsonProperty("additional-models")
    private List<ModelInfo> additionalModelInfos;
    @JsonProperty("min-number-of-partitions-to-influence")
    private int minNumOfSamplesToInfluence = MIN_NUM_OF_PARTITIONS_TO_INFLUENCE_DEFAULT_VALUE;

    @JsonCreator
    public ModelScorerConf(@JsonProperty("name") String name,
                           @JsonProperty("model") ModelInfo modelInfo,
                           @JsonProperty("additional-models") List<ModelInfo> additionalModelInfos) {

        super(name);
        Assert.notNull(modelInfo);
        if (additionalModelInfos == null) {
            additionalModelInfos = Collections.emptyList();
        }
        for (ModelInfo additionalModelInfo : additionalModelInfos) {
            Assert.hasText(additionalModelInfo.getModelName(), "additional model name must be provided and cannot be blank.");
        }
        this.modelInfo = modelInfo;
        this.additionalModelInfos = additionalModelInfos;
    }

    public void setEnoughNumOfSamplesToInfluence(int enoughNumOfSamplesToInfluence) {
        AbstractModelScorer.assertEnoughNumOfPartitionsToInfluence(enoughNumOfSamplesToInfluence);
        this.enoughNumOfSamplesToInfluence = enoughNumOfSamplesToInfluence;
    }

    @JsonProperty("use-certainty-to-calculate-score")
    public void setUseCertaintyToCalculateScore(boolean useCertaintyToCalculateScore) {
        isUseCertaintyToCalculateScore = useCertaintyToCalculateScore;
    }

    public void setMinNumOfSamplesToInfluence(int minNumOfSamplesToInfluence) {
        AbstractModelScorer.assertMinNumOfPartitionsToInfluenceValue(minNumOfSamplesToInfluence);
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

    public List<ModelInfo> getAdditionalModelInfos() {
        return additionalModelInfos;
    }

    public int getMinNumOfSamplesToInfluence() {
        return minNumOfSamplesToInfluence;
    }
}
