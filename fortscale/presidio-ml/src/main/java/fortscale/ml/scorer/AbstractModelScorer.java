package fortscale.ml.scorer;

import fortscale.domain.feature.score.CertaintyFeatureScore;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.PartitionedDataModel;
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
    private int minNumOfPartitionsToInfluence = ModelScorerConf.MIN_NUM_OF_PARTITIONS_TO_INFLUENCE_DEFAULT_VALUE;
    private int enoughNumOfPartitionsToInfluence = ModelScorerConf.ENOUGH_NUM_OF_PARTITIONS_TO_INFLUENCE_DEFAULT_VALUE;
    private boolean isUseCertaintyToCalculateScore = ModelScorerConf.IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEFAULT_VALUE;

    protected final EventModelsCacheService eventModelsCacheService;

    public AbstractModelScorer(String scorerName,
                               String modelName,
                               List<String> additionalModelNames,
                               List<String> contextFieldNames,
                               List<List<String>> additionalContextFieldNames,
                               int minNumOfPartitionsToInfluence,
                               int enoughNumOfPartitionsToInfluence,
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

        assertMinNumOfPartitionsToInfluenceValue(minNumOfPartitionsToInfluence);
        assertEnoughNumOfPartitionsToInfluence(enoughNumOfPartitionsToInfluence);


        setMinNumOfPartitionsToInfluence(minNumOfPartitionsToInfluence);
        setEnoughNumOfPartitionsToInfluence(enoughNumOfPartitionsToInfluence);
        setUseCertaintyToCalculateScore(isUseCertaintyToCalculateScore);
        this.eventModelsCacheService = eventModelsCacheService;
        this.enoughNumOfPartitionsToInfluence = Math.max(enoughNumOfPartitionsToInfluence, minNumOfPartitionsToInfluence);

        this.modelName = modelName;
        this.additionalModelNames = additionalModelNames;
        this.contextFieldNames = contextFieldNames;
        this.additionalContextFieldNames = additionalContextFieldNames;
    }

    static public void assertMinNumOfPartitionsToInfluenceValue(int minNumOfSamplesToInfluence) {
        Assert.isTrue(minNumOfSamplesToInfluence >= 1, String.format(
                "minNumOfPartitionsToInfluence (%d) must be >= 1", minNumOfSamplesToInfluence));
    }

    static public void assertEnoughNumOfPartitionsToInfluence(int enoughNumOfPartitionsToInfluence) {
        Assert.isTrue(enoughNumOfPartitionsToInfluence >= 1, String.format(
                "enoughNumOfPartitionsToInfluence (%d) must be >=1", enoughNumOfPartitionsToInfluence));
    }

    public AbstractModelScorer setMinNumOfPartitionsToInfluence(int minNumOfPartitionsToInfluence) {
        assertMinNumOfPartitionsToInfluenceValue(minNumOfPartitionsToInfluence);
        this.minNumOfPartitionsToInfluence = minNumOfPartitionsToInfluence;
        return this;
    }

    public AbstractModelScorer setEnoughNumOfPartitionsToInfluence(int enoughNumOfPartitionsToInfluence) {
        this.enoughNumOfPartitionsToInfluence = Math.max(enoughNumOfPartitionsToInfluence, minNumOfPartitionsToInfluence);
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
        if (enoughNumOfPartitionsToInfluence <= 1 || model == null) {
            return 1;
        }

        if(!(model instanceof PartitionedDataModel))
        {
            throw new RuntimeException(String.format("can calculate certainty only for models of type %s, got=%s instead ",PartitionedDataModel.class,model.getClass().toString()));
        }

        long numOfPartitions =((PartitionedDataModel) model).getNumOfPartitions();
        double certainty = 0;
        if (numOfPartitions >= enoughNumOfPartitionsToInfluence) {
            certainty = 1;
        } else if (numOfPartitions >= minNumOfPartitionsToInfluence) {
            certainty = ((double) (numOfPartitions - minNumOfPartitionsToInfluence + 1)) / (enoughNumOfPartitionsToInfluence - minNumOfPartitionsToInfluence + 1);
        }
        return certainty;
    }

    public String getModelName() {
        return modelName;
    }

    public List<String> getContextFieldNames() {
        return contextFieldNames;
    }

    public int getMinNumOfPartitionsToInfluence() {
        return minNumOfPartitionsToInfluence;
    }

    public int getEnoughNumOfPartitionsToInfluence() {
        return enoughNumOfPartitionsToInfluence;
    }

    public boolean isUseCertaintyToCalculateScore() {
        return isUseCertaintyToCalculateScore;
    }
}
