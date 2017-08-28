package fortscale.ml.scorer;

import fortscale.common.feature.FeatureNumericValue;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.SMARTValuesModel;
import fortscale.ml.model.SMARTValuesPriorModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.algorithms.SMARTValuesModelScorerAlgorithm;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.utils.factory.FactoryService;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.List;

public class SMARTValuesModelScorer extends AbstractModelInternalUniScorer {
    private SMARTValuesModelScorerAlgorithm algorithm;

    public SMARTValuesModelScorer(String scorerName,
                                  String modelName,
                                  List<String> additionalModelNames,
                                  List<String> contextFieldNames,
                                  List<List<String>> additionalContextFieldNames,
                                  int minNumOfSamplesToInfluence,
                                  int enoughNumOfSamplesToInfluence,
                                  boolean isUseCertaintyToCalculateScore,
                                  IScorerConf baseScorerConf,
                                  int globalInfluence,
                                  FactoryService<Scorer> factoryService,
                                  EventModelsCacheService eventModelsCacheService) {

        super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames, baseScorerConf,
                minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore, factoryService, eventModelsCacheService);

        if (additionalModelNames.size() != 1) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " expects to get one additional model name");
        }

        algorithm = new SMARTValuesModelScorerAlgorithm(globalInfluence);
    }

    @Override
    protected FeatureScore calculateScore(double baseScore,
                                          Model model,
                                          List<Model> additionalModels,
                                          AdeRecordReader adeRecordReader) {
        if (!(model instanceof SMARTValuesModel)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get a model of type " + SMARTValuesModel.class.getSimpleName());
        }

        if (additionalModels.size() != 1 || !(additionalModels.get(0) instanceof SMARTValuesPriorModel)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get one additional model of type " + SMARTValuesPriorModel.class.getSimpleName());
        }

        return new FeatureScore(getName(), algorithm.calculateScore(
                baseScore,
                (SMARTValuesModel) model,
                (SMARTValuesPriorModel) additionalModels.get(0)
        ));
    }
}
