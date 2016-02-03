package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.prevalance.field.TimeModel;
import fortscale.ml.scorer.algorithms.TimeModelScorerAlgorithm;

import java.util.List;

public class TimeModelScorer extends AbstractModelScorer {
    private TimeModelScorerAlgorithm algorithm;

    public TimeModelScorer(String scorerName, String modelName,
                           List<String> contextFieldNames,
                           String featureName,
                           int minNumOfSamplesToInfluence,
                           int enoughNumOfSamplesToInfluence,
                           boolean isUseCertaintyToCalculateScore,
                           int maxRareTimestampCount,
                           int maxNumOfRareTimestamps) {

        super(scorerName, modelName, contextFieldNames, featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore);
        init(maxRareTimestampCount, maxNumOfRareTimestamps);
    }

    public TimeModelScorer(String scorerName,
                           String featureName,
                           int minNumOfSamplesToInfluence,
                           int enoughNumOfSamplesToInfluence,
                           boolean isUseCertaintyToCalculateScore,
                           int maxRareTimestampCount,
                           int maxNumOfRareTimestamps) {

        super(scorerName, featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore);
        init(maxRareTimestampCount, maxNumOfRareTimestamps);
    }

    private void init(int maxRareTimestampCount, int maxNumOfRareTimestamps) {
        algorithm = new TimeModelScorerAlgorithm(maxRareTimestampCount, maxNumOfRareTimestamps);
    }

    @Override
    public double calculateScore(Model model, Feature feature) {
        if(!(model instanceof TimeModel)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get a model of type " + TimeModel.class.getSimpleName());
        }

        if(!(feature.getValue() instanceof FeatureNumericValue)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get a feature of type " + FeatureNumericValue.class.getSimpleName());
        }

        long time = (long) ((FeatureNumericValue) feature.getValue()).getValue();
        return algorithm.calculateScore(time, (TimeModel) model);
    }
}
