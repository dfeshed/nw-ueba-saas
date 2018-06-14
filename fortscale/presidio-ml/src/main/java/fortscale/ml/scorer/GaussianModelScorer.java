package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import fortscale.ml.model.IContinuousDataModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.algorithms.GaussianModelScorerAlgorithm;

import java.util.List;

public class GaussianModelScorer extends AbstractModelTerminalScorer {
    private GaussianModelScorerAlgorithm algorithm;

    public GaussianModelScorer(String scorerName,
                               String modelName,
                               List<String> additionalModelNames,
                               List<String> contextFieldNames,
                               List<List<String>> additionalContextFieldNames,
                               String featureName,
                               int minNumOfPartitionsToInfluence,
                               int enoughNumOfPartitionsToInfluence,
                               boolean isUseCertaintyToCalculateScore,
                               int globalInfluence,
                               EventModelsCacheService eventModelsCacheService) {

        super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames, featureName,
                minNumOfPartitionsToInfluence, enoughNumOfPartitionsToInfluence, isUseCertaintyToCalculateScore, eventModelsCacheService);

        if (additionalModelNames.size() != 1) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " expects to get one additional model name");
        }

        algorithm = new GaussianModelScorerAlgorithm(globalInfluence);
    }

    @Override
    protected double calculateScore(Model model, List<Model> additionalModels, Feature feature) {
        if (!(model instanceof IContinuousDataModel)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get a model of type " + ContinuousDataModel.class.getSimpleName());
        }

        if (additionalModels.size() != 1 || !(additionalModels.get(0) instanceof GaussianPriorModel)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get one additional model of type " + GaussianPriorModel.class.getSimpleName());
        }

        if (!(feature.getValue() instanceof FeatureNumericValue)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get a feature of type " + FeatureNumericValue.class.getSimpleName());
        }

        double value = ((FeatureNumericValue)feature.getValue()).getValue().doubleValue();
        return algorithm.calculateScore(value, (IContinuousDataModel)model, (GaussianPriorModel)additionalModels.get(0));
    }
}
