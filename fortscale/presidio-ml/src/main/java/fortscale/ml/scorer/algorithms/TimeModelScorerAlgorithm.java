package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.TimeModel;

public class TimeModelScorerAlgorithm {
    private CategoryRarityModelScorerAlgorithm algorithm;

    public TimeModelScorerAlgorithm(Integer maxRareTimestampCount, Integer maxNumOfRareTimestamps) {
        algorithm = new CategoryRarityModelScorerAlgorithm(maxRareTimestampCount, maxNumOfRareTimestamps);
    }

    public double calculateScore(long time, TimeModel model) {
        Double smoothedTimeCounter = model.getDoubleSmoothedTimeCounter(time);

        Double floorSmoothedTimeCounter =  Math.floor(smoothedTimeCounter);
        double floorScore = algorithm.calculateScore(floorSmoothedTimeCounter.longValue() + 1, model.getCategoryRarityModel());

        Double ceilSmoothedTimeCounter =  Math.ceil(smoothedTimeCounter);
        double ceilScore = algorithm.calculateScore(ceilSmoothedTimeCounter.longValue() + 1, model.getCategoryRarityModel());

        double score = floorScore;
        if(!floorSmoothedTimeCounter.equals(ceilSmoothedTimeCounter)) {
             score = (ceilSmoothedTimeCounter - smoothedTimeCounter) * floorScore + (smoothedTimeCounter - floorSmoothedTimeCounter) * ceilScore;
        }

        return Math.floor(score);
    }
}
