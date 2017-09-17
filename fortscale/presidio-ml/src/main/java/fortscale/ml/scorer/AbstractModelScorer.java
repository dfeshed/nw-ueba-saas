package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.domain.feature.score.CertaintyFeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.config.ModelScorerConf;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractModelScorer extends AbstractScorer {
    private String modelName;
    private List<String> additionalModelNames;
    private List<String> contextFieldNames;
    private List<List<String>> additionalContextFieldNames;
    private int minNumOfSamplesToInfluence = ModelScorerConf.MIN_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE;
    private int enoughNumOfSamplesToInfluence = ModelScorerConf.ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE;
    private boolean isUseCertaintyToCalculateScore = ModelScorerConf.IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEFAULT_VALUE;

    protected final EventModelsCacheService eventModelsCacheService;

    public AbstractModelScorer(String scorerName,
                               String modelName,
                               List<String> additionalModelNames,
                               List<String> contextFieldNames,
                               List<List<String>> additionalContextFieldNames,
                               int minNumOfSamplesToInfluence,
                               int enoughNumOfSamplesToInfluence,
                               boolean isUseCertaintyToCalculateScore,
                               EventModelsCacheService eventModelsCacheService) {

        super(scorerName);
        // Assertions
        if (additionalModelNames == null) {
            additionalModelNames = Collections.emptyList();
        }

        if (additionalContextFieldNames == null) {
            additionalContextFieldNames = Collections.emptyList();
        }
        Assert.hasText(modelName, "model name must be provided and cannot be empty or blank.");

        Assert.isTrue(additionalModelNames.size() == additionalContextFieldNames.size(),
                "additionalModelNames and additionalContextFieldNames must have the same size");

        for (String additionalModelName : additionalModelNames) {
            Assert.hasText(additionalModelName, "additional model names cannot be empty or blank.");
        }

        Assert.notEmpty(contextFieldNames, "List of context field names cannot be empty.");
        for (String contextFieldName : contextFieldNames) {
            Assert.hasText(contextFieldName, "context field name cannot be null, empty or blank.");
        }
        for (List<String> c : additionalContextFieldNames) {
            for (String contextFieldName : c) {
                Assert.hasText(contextFieldName, "context field name cannot be null, empty or blank.");
            }
        }

        assertMinNumOfSamplesToInfluenceValue(minNumOfSamplesToInfluence);
        assertEnoughNumOfSamplesToInfluence(enoughNumOfSamplesToInfluence);


        setMinNumOfSamplesToInfluence(minNumOfSamplesToInfluence);
        setEnoughNumOfSamplesToInfluence(enoughNumOfSamplesToInfluence);
        setUseCertaintyToCalculateScore(isUseCertaintyToCalculateScore);
        this.eventModelsCacheService = eventModelsCacheService;
        this.enoughNumOfSamplesToInfluence = Math.max(enoughNumOfSamplesToInfluence, minNumOfSamplesToInfluence);

        this.modelName = modelName;
        this.additionalModelNames = additionalModelNames;
        this.contextFieldNames = contextFieldNames;
        this.additionalContextFieldNames = additionalContextFieldNames;
    }

    static public void assertMinNumOfSamplesToInfluenceValue(int minNumOfSamplesToInfluence) {
        Assert.isTrue(minNumOfSamplesToInfluence >= 1, String.format(
                "minNumOfSamplesToInfluence (%d) must be >= 1", minNumOfSamplesToInfluence));
    }

    static public void assertEnoughNumOfSamplesToInfluence(int enoughNumOfSamplesToInfluence) {
        Assert.isTrue(enoughNumOfSamplesToInfluence >= 1, String.format(
                "enoughNumOfSamplesToInfluence (%d) must be >=1", enoughNumOfSamplesToInfluence));
    }

    public AbstractModelScorer setMinNumOfSamplesToInfluence(int minNumOfSamplesToInfluence) {
        assertMinNumOfSamplesToInfluenceValue(minNumOfSamplesToInfluence);
        this.minNumOfSamplesToInfluence = minNumOfSamplesToInfluence;
        return this;
    }

    public AbstractModelScorer setEnoughNumOfSamplesToInfluence(int enoughNumOfSamplesToInfluence) {
        this.enoughNumOfSamplesToInfluence = Math.max(enoughNumOfSamplesToInfluence, minNumOfSamplesToInfluence);
        return this;
    }

    public AbstractModelScorer setUseCertaintyToCalculateScore(boolean useCertaintyToCalculateScore) {
        isUseCertaintyToCalculateScore = useCertaintyToCalculateScore;
        return this;
    }

    protected Model getMainModel(AdeRecordReader adeRecordReader) {
        return getModel(adeRecordReader, modelName, contextFieldNames);
    }

    protected List<Model> getAdditionalModels(AdeRecordReader adeRecordReader) {
        return IntStream.range(0, additionalModelNames.size())
                .mapToObj(i -> getModel(
                        adeRecordReader,
                        additionalModelNames.get(i),
                        additionalContextFieldNames.get(i)
                ))
                .collect(Collectors.toList());
    }

    protected Model getModel(AdeRecordReader adeRecordReader, String modelName, List<String> contextFieldNames) {
        return eventModelsCacheService.getModel(adeRecordReader, modelName, contextFieldNames);
    }

    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader){
        Model model = getMainModel(adeRecordReader);
        List<Model> additionalModels = getAdditionalModels(adeRecordReader);
        FeatureScore featureScore = calculateScore(model, additionalModels, adeRecordReader);
        if (featureScore == null) {
            return new CertaintyFeatureScore(getName(), 0d, 0d);
        }
        double certainty = calculateCertainty(model);
        if (isUseCertaintyToCalculateScore) {
            featureScore.setScore(featureScore.getScore() * certainty);
        } else {
            featureScore = new CertaintyFeatureScore(
                    featureScore.getName(),
                    featureScore.getScore(),
                    featureScore.getFeatureScores(),
                    certainty
            );
        }
        return featureScore;
    }

    protected abstract FeatureScore calculateScore(Model model,
                                                   List<Model> additionalModels,
                                                   AdeRecordReader adeRecordReader);

    protected double calculateCertainty(Model model){
        if (enoughNumOfSamplesToInfluence <= 1 || model == null) {
            return 1;
        }

        long numOfSamples = model.getNumOfSamples();
        double certainty = 0;
        if (numOfSamples >= enoughNumOfSamplesToInfluence) {
            certainty = 1;
        } else if (numOfSamples >= minNumOfSamplesToInfluence) {
            certainty = ((double) (numOfSamples - minNumOfSamplesToInfluence + 1)) / (enoughNumOfSamplesToInfluence - minNumOfSamplesToInfluence + 1);
        }
        return certainty;
    }

    public String getModelName() {
        return modelName;
    }

    public List<String> getContextFieldNames() {
        return contextFieldNames;
    }

    public int getMinNumOfSamplesToInfluence() {
        return minNumOfSamplesToInfluence;
    }

    public int getEnoughNumOfSamplesToInfluence() {
        return enoughNumOfSamplesToInfluence;
    }

    public boolean isUseCertaintyToCalculateScore() {
        return isUseCertaintyToCalculateScore;
    }
}
