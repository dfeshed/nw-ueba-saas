package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import fortscale.ml.model.Model;
import fortscale.ml.scorer.algorithms.GaussianModelScorerAlgorithm;

import java.util.List;


public class GaussianModelScorer extends RawNumOfSamplesCetaintyModelScorer {
    private GaussianModelScorerAlgorithm algorithm;

    public GaussianModelScorer(String scorerName,
							   String modelName,
							   List<String> additionalModelNames,
							   List<String> contextFieldNames,
							   List<List<String>> additionalContextFieldNames,
							   String featureName,
							   int minNumOfSamplesToInfluence,
							   int enoughNumOfSamplesToInfluence,
							   boolean isUseCertaintyToCalculateScore,
							   int globalInfluence) {

        super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames,
                featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore);

        if (additionalModelNames.size() != 1) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " expects to get one additional model name");
        }
        algorithm = new GaussianModelScorerAlgorithm(globalInfluence);
    }

    @Override
    protected double calculateScore(Model model, List<Model> additionalModels, Feature feature) {
        if (!(model instanceof ContinuousDataModel)) {
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

        double value = (double) ((FeatureNumericValue) feature.getValue()).getValue();

        return algorithm.calculateScore(value, (ContinuousDataModel) model, (GaussianPriorModel) additionalModels.get(0));
    }
}
