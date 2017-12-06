package fortscale.ml.scorer;

import fortscale.domain.feature.score.CertaintyFeatureScore;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.*;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.scorer.algorithms.SMARTValuesModelScorerAlgorithm;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ModelScorerConf;
import fortscale.utils.factory.FactoryService;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class SMARTValuesModelScorer extends AbstractScorer {
    private SMARTValuesModelScorerAlgorithm algorithm;
    protected SmartWeightsModelScorer smartWeightsModelScorer;
    private String modelName;
    private String globalModelName;
    private int minNumOfPartitionsToInfluence = ModelScorerConf.MIN_NUM_OF_PARTITIONS_TO_INFLUENCE_DEFAULT_VALUE;
    private int enoughNumOfPartitionsToInfluence = ModelScorerConf.ENOUGH_NUM_OF_PARTITIONS_TO_INFLUENCE_DEFAULT_VALUE;
    private boolean isUseCertaintyToCalculateScore = ModelScorerConf.IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEFAULT_VALUE;
    protected final EventModelsCacheService eventModelsCacheService;

    public SMARTValuesModelScorer(String scorerName,
                                  String modelName,
                                  String globalModelName,
                                  int minNumOfPartitionsToInfluence,
                                  int enoughNumOfPartitionsToInfluence,
                                  boolean isUseCertaintyToCalculateScore,
                                  IScorerConf baseScorerConf,
                                  int globalInfluence,
                                  FactoryService<Scorer> factoryService,
                                  EventModelsCacheService eventModelsCacheService) {

        super(scorerName);
        Assert.hasText(modelName, "model name must be provided and cannot be empty or blank.");
        Assert.hasText(globalModelName, "global model name must be provided and cannot be empty or blank.");

        Assert.notNull(baseScorerConf, "base scorer should not be null");
        Assert.notNull(factoryService, "factory service should not be null");
        Scorer tmp = factoryService.getProduct(baseScorerConf);
        Assert.isTrue(tmp instanceof SmartWeightsModelScorer, "SMARTValuesModelScorer expecting to get configuration of SmartWeightsModelScorer");
        smartWeightsModelScorer = (SmartWeightsModelScorer) tmp;

        assertMinNumOfPartitionsToInfluenceValue(minNumOfPartitionsToInfluence);
        assertEnoughNumOfPartitionsToInfluence(enoughNumOfPartitionsToInfluence);


        setMinNumOfPartitionsToInfluence(minNumOfPartitionsToInfluence);
        setEnoughNumOfPartitionsToInfluence(enoughNumOfPartitionsToInfluence);
        setUseCertaintyToCalculateScore(isUseCertaintyToCalculateScore);
        this.eventModelsCacheService = eventModelsCacheService;

        this.modelName = modelName;
        this.globalModelName = globalModelName;

        algorithm = new SMARTValuesModelScorerAlgorithm(globalInfluence);
    }

    protected List<ModelDAO> getMainModel(AdeRecordReader adeRecordReader) {
        return getModel(adeRecordReader, modelName, adeRecordReader.getContext(SmartRecord.CONTEXT_ID_FIELD));
    }

    protected List<ModelDAO> getGlobalModel(AdeRecordReader adeRecordReader) {
        return getModel(adeRecordReader, globalModelName, null);
    }

    protected List<ModelDAO> getModel(AdeRecordReader adeRecordReader, String modelName, String contextId) {
        return eventModelsCacheService.getModelDAOsSortedByEndTimeDesc(adeRecordReader, modelName, contextId);
    }

    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader){
        List<ModelDAO> mainModelDAOs = getMainModel(adeRecordReader);
        List<ModelDAO> globalModelDAOs = getGlobalModel(adeRecordReader);
        SMARTValuesModel mainModel = null;
        Model globalModel = null;
        Instant weightModelEndTime = null;

        for (ModelDAO globalModelDAO : globalModelDAOs) {
            SMARTValuesPriorModel smartValuesPriorModel = (SMARTValuesPriorModel) globalModelDAO.getModel();
            if (smartValuesPriorModel == null) {
                continue;
            }
            Instant smartValuePriorWightsModelEndTime = smartValuesPriorModel.getWeightsModelEndTime();
            boolean foundMatchingModels = false;
            for (ModelDAO mainModelDAO : mainModelDAOs) {
                SMARTValuesModel mainModelDAOModel = (SMARTValuesModel) mainModelDAO.getModel();
                if (mainModelDAOModel == null) {
                    continue;
                }
                Instant smartValueWeightsModelEndTime = mainModelDAOModel.getWeightsModelEndTime();

                if (smartValuePriorWightsModelEndTime.equals(smartValueWeightsModelEndTime)) {
                    mainModel = mainModelDAOModel;
                    globalModel = smartValuesPriorModel;
                    weightModelEndTime = smartValueWeightsModelEndTime;
                    foundMatchingModels = true;
                    break;
                }
            }
            if(foundMatchingModels)
            {
                break;
            }
        }
        FeatureScore featureScore = calculateScore(mainModel, globalModel, adeRecordReader, weightModelEndTime);
        if (featureScore == null) {
            return new CertaintyFeatureScore(getName(), 0d, 0d);
        }
        double certainty = calculateCertainty(mainModel);
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

    final protected FeatureScore calculateScore(Model model,
                                                Model globalModel,
                                                AdeRecordReader adeRecordReader,
                                                Instant weightModelEndTime){
        if (model == null || globalModel == null) {
            FeatureScore baseScore = smartWeightsModelScorer.calculateScore(adeRecordReader);
            List<FeatureScore> baseFeatureScores = Collections.singletonList(baseScore);
            return new CertaintyFeatureScore(getName(), 0.0, baseFeatureScores, 0.0);
        } else {
            FeatureScore baseScore = smartWeightsModelScorer.calculateScore(adeRecordReader, weightModelEndTime);
            List<FeatureScore> baseFeatureScores = Collections.singletonList(baseScore);
            FeatureScore featureScore = calculateScore(baseScore.getScore(), model, globalModel);
            featureScore.setFeatureScores(baseFeatureScores);
            return featureScore;
        }
    }

    private FeatureScore calculateScore(double baseScore,
                                          Model model,
                                          Model globalModel) {
        if (!(model instanceof SMARTValuesModel)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get a model of type " + SMARTValuesModel.class.getSimpleName());
        }

        if (!(globalModel instanceof SMARTValuesPriorModel)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get global model of type " + SMARTValuesPriorModel.class.getSimpleName());
        }

        return new FeatureScore(getName(), algorithm.calculateScore(
                baseScore,
                (SMARTValuesModel) model,
                (SMARTValuesPriorModel) globalModel
        ));
    }

    protected double calculateCertainty(Model model){
        if (enoughNumOfPartitionsToInfluence <= 1 || model == null) {
            return 1;
        }

        if(!(model instanceof PartitionedDataModel ))
        {
            throw new RuntimeException(String.format("can calculate certainty only for models of type %s, got=%s instead ",PartitionedDataModel.class,model.getClass().toString()));
        }

        long numOfPartitions = (( PartitionedDataModel )model).getNumOfPartitions();
        double certainty = 0;
        if (numOfPartitions >= enoughNumOfPartitionsToInfluence) {
            certainty = 1;
        } else if (numOfPartitions >= minNumOfPartitionsToInfluence) {
            certainty = ((double) (numOfPartitions - minNumOfPartitionsToInfluence + 1)) / (enoughNumOfPartitionsToInfluence - minNumOfPartitionsToInfluence + 1);
        }
        return certainty;
    }


    static public void assertMinNumOfPartitionsToInfluenceValue(int minNumOfPartitionsToInfluence) {
        Assert.isTrue(minNumOfPartitionsToInfluence >= 1, String.format(
                "minNumOfPartitionsToInfluence (%d) must be >= 1", minNumOfPartitionsToInfluence));
    }

    static public void assertEnoughNumOfPartitionsToInfluence(int enoughNumOfPartitionsToInfluence) {
        Assert.isTrue(enoughNumOfPartitionsToInfluence >= 1, String.format(
                "enoughNumOfPartitionsToInfluence (%d) must be >=1", enoughNumOfPartitionsToInfluence));
    }

    public SMARTValuesModelScorer setMinNumOfPartitionsToInfluence(int minNumOfPartitionsToInfluence) {
        assertMinNumOfPartitionsToInfluenceValue(minNumOfPartitionsToInfluence);
        this.minNumOfPartitionsToInfluence = minNumOfPartitionsToInfluence;
        return this;
    }

    public SMARTValuesModelScorer setEnoughNumOfPartitionsToInfluence(int enoughNumOfPartitionsToInfluence) {
        this.enoughNumOfPartitionsToInfluence = Math.max(enoughNumOfPartitionsToInfluence, minNumOfPartitionsToInfluence);
        return this;
    }

    public SMARTValuesModelScorer setUseCertaintyToCalculateScore(boolean useCertaintyToCalculateScore) {
        isUseCertaintyToCalculateScore = useCertaintyToCalculateScore;
        return this;
    }
}
