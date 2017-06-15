package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.ml.model.Model;
import fortscale.ml.model.SMARTValuesModel;
import fortscale.ml.model.SMARTValuesPriorModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.algorithms.SMARTValuesModelScorerAlgorithm;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.recordreader.RecordReader;
import presidio.ade.domain.record.AdeRecord;

import java.util.List;


public class SMARTValuesModelScorer extends AbstractModelScorer {
    private SMARTValuesModelScorerAlgorithm algorithm;

    public SMARTValuesModelScorer(String scorerName,
                                  String modelName,
                                  List<String> additionalModelNames,
                                  List<String> contextFieldNames,
                                  List<List<String>> additionalContextFieldNames,
                                  String featureName,
                                  int minNumOfSamplesToInfluence,
                                  int enoughNumOfSamplesToInfluence,
                                  boolean isUseCertaintyToCalculateScore,
                                  int globalInfluence,
                                  FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService,
                                  EventModelsCacheService eventModelsCacheService) {

        super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames,
                featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore,
                recordReaderFactoryService, eventModelsCacheService);

        if (additionalModelNames.size() != 1) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " expects to get one additional model name");
        }
        algorithm = new SMARTValuesModelScorerAlgorithm(globalInfluence);
    }

    @Override
    protected double calculateScore(Model model, List<Model> additionalModels, Feature feature) {
        if (!(model instanceof SMARTValuesModel)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get a model of type " + SMARTValuesModel.class.getSimpleName());
        }

        if (additionalModels.size() != 1 || !(additionalModels.get(0) instanceof SMARTValuesPriorModel)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get one additional model of type " + SMARTValuesPriorModel.class.getSimpleName());
        }

        if (!(feature.getValue() instanceof FeatureNumericValue)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get a feature of type " + FeatureNumericValue.class.getSimpleName());
        }

        double value = (double) ((FeatureNumericValue) feature.getValue()).getValue();

        return algorithm.calculateScore(value, (SMARTValuesModel) model, (SMARTValuesPriorModel) additionalModels.get(0));
    }
}
