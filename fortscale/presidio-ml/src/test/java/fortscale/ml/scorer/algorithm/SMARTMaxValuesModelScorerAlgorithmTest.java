package fortscale.ml.scorer.algorithm;

import fortscale.ml.model.SMARTMaxValuesModel;
import fortscale.ml.model.SMARTValuesModel;
import fortscale.ml.model.SMARTValuesPriorModel;
import fortscale.ml.scorer.algorithms.SMARTMaxValuesModelScorerAlgorithm;
import fortscale.ml.scorer.algorithms.SMARTValuesModelScorerAlgorithm;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

public class SMARTMaxValuesModelScorerAlgorithmTest {

    private double calcScore(int globalInfluence,
                             int maxUserInfluence,
                             int numOfPartitionUserInfluence,
                             int minNumOfUserValues,
                             double prior,
                             Map<Long, Double> startInstantToMaxSmartValue,
                             double valueToScore,
                             Instant weightsModelEndTime,
                             long modelNumOfPartition) {
        SMARTMaxValuesModel model = new SMARTMaxValuesModel();
        model.init(startInstantToMaxSmartValue,  modelNumOfPartition, weightsModelEndTime);
        SMARTMaxValuesModelScorerAlgorithm scorerAlgorithm = new SMARTMaxValuesModelScorerAlgorithm(globalInfluence,  maxUserInfluence, numOfPartitionUserInfluence, minNumOfUserValues);
        SMARTValuesPriorModel priorModel = new SMARTValuesPriorModel();
        priorModel.init(prior);

        return scorerAlgorithm.calculateScore(valueToScore, model, priorModel);
    }

    private void assertScoreRange(int globalInfluence,
                                  double prior,
                                  Map<Long, Double> startInstantToMaxSmartValue,
                                  double valueToScore,
                                  double expectedRangeMin,
                                  double expectedRangeMax,
                                  Instant weightsModelEndTime) {

        double score = calcScore(globalInfluence, 10, 5, 5, prior, startInstantToMaxSmartValue, valueToScore, weightsModelEndTime, startInstantToMaxSmartValue.size());
        Assert.assertTrue(String.format("score (%e) >= expectedRangeMin (%e) does not hold", score, expectedRangeMin), score >= expectedRangeMin);
        Assert.assertTrue(String.format("score (%e) <= expectedRangeMax (%e) does not hold", score, expectedRangeMax), score <= expectedRangeMax);
    }

    private void assertScoresMonotonicity(double[] scores, boolean isIncreasing) {
        int sign = isIncreasing ? 1 : -1;
        Assert.assertTrue(IntStream.range(0, scores.length - 1)
                .allMatch(i -> sign * scores[i] <= sign * scores[i + 1]));
        Assert.assertTrue(sign * scores[0] < sign * scores[scores.length - 1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenScoringNegativeValue() {
        calcScore(0, 10, 5, 5, 0, Collections.emptyMap(), -1, Instant.now(), 0);
    }

    @Test
    public void shouldScore0To0() {
        Assert.assertEquals(0, calcScore(0, 10, 5, 5, 0, Collections.emptyMap(), 0, Instant.now(), 0), 0.0001);
    }

    @Test
    public void shouldScorePositiveToPositiveGivenHistoryIsZeros() {
        double value = 0.1;
        List<Double> oldValues = Arrays.asList(0.0, 0.0, 0.0);
        int globalInfluence = 5;
        Instant weightsModelEndTime = Instant.now();
        long numOfPartitions = 25;

        Map<Long, Double> startInstantToMaxSmartValue = new HashMap<>();
        Duration duration = Duration.ofDays(1);
        Instant start = weightsModelEndTime.minus(duration);

        for(int i=0;i<numOfPartitions;i++){
            startInstantToMaxSmartValue.put(start.getEpochSecond(), 0.0);
            start = start.minus(duration);
        }

        Assert.assertTrue(calcScore(globalInfluence,10, 5, 5, 0.5, startInstantToMaxSmartValue, value, weightsModelEndTime, numOfPartitions) > 0);
    }

    @Test
    public void shouldScore100ToReallyBigValueIfItIsTheFirstPositiveValue() {
        Assert.assertEquals(100, calcScore(1, 10, 5, 5, 0, Collections.emptyMap(), 1, Instant.now(), 0), 0.0001);
    }

    @Test
    public void shouldScoreLowToReallyBigValueIfItHasBeenAlreadySeen() {

        Map<Long, Double> startInstantToMaxSmartValue = new HashMap<>();
        Instant weightsModelEndTime = Instant.now();
        Instant start = weightsModelEndTime.minus(Duration.ofDays(1));
        startInstantToMaxSmartValue.put(start.getEpochSecond(), 1.0);

        assertScoreRange(1, 0, startInstantToMaxSmartValue, 1, 0, 80, weightsModelEndTime);
    }

    @Test
    public void shouldScore100ToReallyBigValueGivenFewLowValues() {

        Map<Long, Double> startInstantToMaxSmartValue = new HashMap<>();
        Instant weightsModelEndTime = Instant.now();
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(1)).getEpochSecond(), 0.01);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(2)).getEpochSecond(), 0.05);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(3)).getEpochSecond(), 0.1);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(4)).getEpochSecond(), 0.1);


        assertScoreRange(1, 0, startInstantToMaxSmartValue, 0.3, 0, 100, weightsModelEndTime);
    }

    @Test
    public void shouldScoreIncreasinglyAsValueIncreases() {



        Map<Long, Double> startInstantToMaxSmartValue = new HashMap<>();
        Instant weightsModelEndTime = Instant.now();
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(1)).getEpochSecond(), 0.01);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(2)).getEpochSecond(), 0.03);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(3)).getEpochSecond(), 0.05);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(4)).getEpochSecond(), 0.1);

        double[] scores = IntStream.range(0, 100)
                .mapToDouble(i -> i / 100.)
                .map(value -> calcScore(1, 10, 5, 5, 0, startInstantToMaxSmartValue, value, Instant.now(), startInstantToMaxSmartValue.size()))
                .toArray();
        assertScoresMonotonicity(scores, true);
    }

    @Test
    public void priorShouldNotAffectTheScoreWhenGlobalInfluenceIsZero() {
        double value = 0.4;
        Map<Long, Double> startInstantToMaxSmartValue = new HashMap<>();
        Instant weightsModelEndTime = Instant.now();
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(1)).getEpochSecond(), 0.01);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(2)).getEpochSecond(), 0.03);

        int globalInfluence = 0;
        double score = calcScore(globalInfluence, 0,10, 5 ,5,  startInstantToMaxSmartValue, value, Instant.now(), startInstantToMaxSmartValue.size());
        Assert.assertEquals(score, calcScore(globalInfluence, 10, 5 ,5, 1, startInstantToMaxSmartValue, value, Instant.now(), startInstantToMaxSmartValue.size()), 0.0000001);
    }

    @Test
    public void shouldScoreDecreasinglyAsGlobalInfluenceIncreasesWhenPriorIsHigherThanUserHistory() {
        Map<Long, Double> startInstantToMaxSmartValue = new HashMap<>();
        Instant weightsModelEndTime = Instant.now();
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(1)).getEpochSecond(), 0.01);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(2)).getEpochSecond(), 0.03);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(3)).getEpochSecond(), 0.05);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(4)).getEpochSecond(), 0.1);



        double[] scores = IntStream.range(0, 100)
                .mapToDouble(globalInfluence -> calcScore(globalInfluence, 10, 5, 5, 0.5, startInstantToMaxSmartValue, 0.5, Instant.now(), startInstantToMaxSmartValue.size()))
                .toArray();
        assertScoresMonotonicity(scores, false);
    }

    @Test
    public void shouldScoreTheSameAsGlobalInfluenceIncreasesWhenPriorIsLowerThanUserHistory() {

        Map<Long, Double> startInstantToMaxSmartValue = new HashMap<>();
        Instant weightsModelEndTime = Instant.now();
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(1)).getEpochSecond(), 0.01);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(2)).getEpochSecond(), 0.03);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(3)).getEpochSecond(), 0.05);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(4)).getEpochSecond(), 0.5);

        double[] scores = IntStream.range(0, 100)
                .mapToDouble(globalInfluence -> calcScore(globalInfluence, 10, 5, 5,  0.01, startInstantToMaxSmartValue, 0.5, Instant.now(), startInstantToMaxSmartValue.size()))
                .toArray();
        Assert.assertTrue(IntStream.range(0, scores.length - 1)
                .allMatch(i -> scores[i] == scores[i + 1]));
    }

    @Test
    public void shouldScoreDecreasinglyAsPriorIncreases() {

        Map<Long, Double> startInstantToMaxSmartValue = new HashMap<>();
        Instant weightsModelEndTime = Instant.now();
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(1)).getEpochSecond(), 0.01);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(2)).getEpochSecond(), 0.03);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(3)).getEpochSecond(), 0.05);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(4)).getEpochSecond(), 0.1);

        double[] scores = IntStream.range(0, 100)
                .mapToDouble(prior -> calcScore(10,10, 5, 5, prior, startInstantToMaxSmartValue, 0.5, weightsModelEndTime, startInstantToMaxSmartValue.size()))
                .toArray();
        assertScoresMonotonicity(scores, false);
    }

    @Test
    public void shouldScore100InCaseThatSumOfGlobalValuesIsZeroAndSumOfValuesIsZero() {

        Map<Long, Double> startInstantToMaxSmartValue = new HashMap<>();
        Instant weightsModelEndTime = Instant.now();
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(1)).getEpochSecond(), 0.0);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(2)).getEpochSecond(), 0.0);
        double value = 0.001;
        int globalInfluence = 1;
        double score = calcScore(globalInfluence, 10, 5, 5,0, startInstantToMaxSmartValue, value, weightsModelEndTime, startInstantToMaxSmartValue.size());
        Assert.assertEquals(100, score, 0.0000001);
    }

    @Test
    //This test if for the case that there is no global model.
    public void shouldHaveSmallScoreInCaseThatSumOfGlobalValuesIsZeroButNewValueIsNotExceptionalForTheEntity() {

        Map<Long, Double> startInstantToMaxSmartValue = new HashMap<>();
        Instant weightsModelEndTime = Instant.now();
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(1)).getEpochSecond(), 0.1);
        startInstantToMaxSmartValue.put(weightsModelEndTime.minus(Duration.ofDays(2)).getEpochSecond(), 0.1);

        double value = 0.001;
        int globalInfluence = 1;
        double score = calcScore(globalInfluence, 10, 5, 5,0, startInstantToMaxSmartValue, value, weightsModelEndTime, startInstantToMaxSmartValue.size());
        Assert.assertEquals(1, score, 1);
    }
}
