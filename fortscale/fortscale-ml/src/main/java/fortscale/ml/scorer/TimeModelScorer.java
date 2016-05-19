package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.ml.model.Model;
import fortscale.ml.model.TimeModel;
import fortscale.ml.scorer.algorithms.TimeModelScorerAlgorithm;

import java.util.List;

public class TimeModelScorer extends AbstractModelScorer {
    private TimeModelScorerAlgorithm algorithm;

    public TimeModelScorer(String scorerName,
                           String modelName,
                           List<String> additionalModelNames,
                           List<String> contextFieldNames,
                           List<List<String>> additionalContextFieldNames,
                           String featureName,
                           int minNumOfSamplesToInfluence,
                           int enoughNumOfSamplesToInfluence,
                           boolean isUseCertaintyToCalculateScore,
                           int maxRareTimestampCount,
                           int maxNumOfRareTimestamps) {

        super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames, featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore);
        algorithm = new TimeModelScorerAlgorithm(maxRareTimestampCount, maxNumOfRareTimestamps);
    }

    @Override
    protected double calculateScore(Model model, List<Model> additionalModels, Feature feature) {
        if(!(model instanceof TimeModel)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get a model of type " + TimeModel.class.getSimpleName());
        }

        if (additionalModels.size() > 0) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " doesn't expect to get additional models");
        }

        if(!(feature.getValue() instanceof FeatureNumericValue)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get a feature of type " + FeatureNumericValue.class.getSimpleName());
        }

        long time = (long) ((FeatureNumericValue) feature.getValue()).getValue();
        return algorithm.calculateScore(time, (TimeModel) model);
    }
}
