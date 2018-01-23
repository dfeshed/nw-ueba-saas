package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.TimeModel;

public class TimeModelScorerAlgorithm {
    private CategoryRarityModelScorerAlgorithm algorithm;

    public TimeModelScorerAlgorithm(Integer maxRareTimestampCount, Integer maxNumOfRareTimestamps, double xWithValueHalfFactor) {
        algorithm = new CategoryRarityModelScorerAlgorithm(maxRareTimestampCount, maxNumOfRareTimestamps, xWithValueHalfFactor);
    }

    public double calculateScore(long time, TimeModel model) {
        long smoothedTimeCounter = model.getSmoothedTimeCounter(time);
        return algorithm.calculateScore(smoothedTimeCounter + 1, model.getCategoryRarityModel());
    }
}
