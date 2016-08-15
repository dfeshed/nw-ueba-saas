package fortscale.ml.scorer.algorithm;

import fortscale.ml.model.SMARTValuesModel;
import fortscale.ml.scorer.algorithms.SMARTValuesModelScorerAlgorithm;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SMARTValuesModelScorerAlgorithmTest {

    private double calcScore(int globalInfluence,
                             double globalPositiveValueMean,
                             List<Double> oldValues,
                             double valueToScore) {
        SMARTValuesModel model = new SMARTValuesModel();
        long numOfZeroValues = oldValues.stream().filter(value -> value == 0).count();
        double sumOfValues = oldValues.stream().mapToDouble(Double::valueOf).sum();
        model.init(numOfZeroValues, oldValues.size() - numOfZeroValues, sumOfValues);
        SMARTValuesModelScorerAlgorithm scorerAlgorithm = new SMARTValuesModelScorerAlgorithm(globalInfluence);

        SMARTValuesModel globalModel = new SMARTValuesModel();
        globalModel.init(0, 1, globalPositiveValueMean);

        return scorerAlgorithm.calculateScore(valueToScore, model, globalModel);
    }

    private void assertScoreRange(int globalInfluence,
                                  double globalPositiveValueMean,
                                  List<Double> oldValues,
                                  double valueToScore,
                                  double expectedRangeMin,
                                  double expectedRangeMax) {
        double score = calcScore(globalInfluence, globalPositiveValueMean, oldValues, valueToScore);
        Assert.assertTrue(String.format("score (%e) >= expectedRangeMin (%e) does not hold", score, expectedRangeMin), score >= expectedRangeMin);
        Assert.assertTrue(String.format("score (%e) <= expectedRangeMax (%e) does not hold", score, expectedRangeMax), score <= expectedRangeMax);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenScoringNegativeValue() {
        calcScore(0, 0, Collections.emptyList(), -1);
    }

    @Test
    public void shouldScore0To0() {
        Assert.assertEquals(0, calcScore(0, 0, Collections.emptyList(), 0), 0.0001);
    }

    @Test
    public void shouldScorePositiveToPositiveGivenHistoryIsZeros() {
        double value = 0.1;
        List<Double> oldValues = Arrays.asList(0.0, 0.0, 0.0);
        int globalInfluence = 5;
        Assert.assertTrue(calcScore(globalInfluence, 0.5, oldValues, value) > 0);
    }

    @Test
    public void shouldScore100ToReallyBigValueIfItIsTheFirstPositiveValue() {
        Assert.assertEquals(100, calcScore(1, 0, Collections.emptyList(), 1), 0.0001);
    }

    @Test
    public void shouldScoreLowToReallyBigValueIfItHasBeenAlreadySeen() {
        assertScoreRange(1, 0, Collections.singletonList(1D), 1, 0, 80);
    }

    @Test
    public void shouldScore100ToReallyBigValueGivenFewLowValues() {
        assertScoreRange(1, 0, Arrays.asList(0.01, 0.05, 0.1, 0.1), 0.3, 0, 100);
    }

    @Test
    public void shouldScoreIncreasinglyAsValueIncreases() {
        List<Double> scores = IntStream.range(0, 100)
                .mapToDouble(i -> i / 100.)
                .map(value -> calcScore(1, 0, Arrays.asList(0.01, 0.03, 0.05, 0.1), value))
                .boxed()
                .collect(Collectors.toList());
        ScorerAlgorithmTestUtils.assertScoresIncrease(scores);
    }

    @Test
    public void zerosShouldNotAffectTheScore() {
        double value = 0.4;
        List<Double> oldValues = new ArrayList<>();
        oldValues.add(0.05);
        double score = calcScore(1, 0, oldValues, value);
        oldValues.add(0D);
        Assert.assertEquals(score, calcScore(1, 0, oldValues, value), 0.0000001);
    }

    @Test
    public void globalPositiveValueMeanShouldNotAffectTheScoreWhenGlobalInfluenceIsZero() {
        double value = 0.4;
        List<Double> oldValues = Arrays.asList(0.01, 0.3);
        int globalInfluence = 0;
        double score = calcScore(globalInfluence, 0, oldValues, value);
        Assert.assertEquals(score, calcScore(globalInfluence, 1, oldValues, value), 0.0000001);
    }

    @Test
    public void shouldScoreDecreasinglyAsGlobalInfluenceIncreasesWhenGlobalPositiveValueMeanIsHigherThanUserHistory() {
        List<Double> scores = IntStream.range(0, 100)
                .mapToDouble(globalInfluence -> calcScore(globalInfluence, 0.5, Arrays.asList(0.01, 0.03, 0.05, 0.1), 0.5))
                .boxed()
                .collect(Collectors.toList());
        ScorerAlgorithmTestUtils.assertScoresDecrease(scores);
    }

    @Test
    public void shouldScoreTheSameAsGlobalInfluenceIncreasesWhenGlobalPositiveValueMeanIsLowerThanUserHistory() {
        double[] scores = IntStream.range(0, 100)
                .mapToDouble(globalInfluence -> calcScore(globalInfluence, 0.01, Arrays.asList(0.01, 0.03, 0.05, 0.1), 0.5))
                .toArray();
        Assert.assertTrue(IntStream.range(0, scores.length - 1)
                .allMatch(i -> scores[i] == scores[i + 1]));
    }

    @Test
    public void shouldScoreDecreasinglyAsGlobalPositiveValueMeanIncreases() {
        List<Double> scores = IntStream.range(0, 100)
                .mapToDouble(globalPositiveValueMean -> calcScore(10, globalPositiveValueMean, Arrays.asList(0.01, 0.03, 0.05, 0.1), 0.5))
                .boxed()
                .collect(Collectors.toList());
        ScorerAlgorithmTestUtils.assertScoresDecrease(scores);
    }

    @Test
    public void shouldScore100InCaseThatSumOfGlobalValuesIsZeroAndSumOfValuesIsZero() {
        double value = 0.001;
        List<Double> oldValues = Arrays.asList(0.0, 0.0);
        int globalInfluence = 1;
        double score = calcScore(globalInfluence, 0, oldValues, value);
        Assert.assertEquals(100, score, 0.0000001);
    }

    @Test
    //This test if for the case that there is no global model.
    public void shouldHaveSmallScoreInCaseThatSumOfGlobalValuesIsZeroButNewValueIsNotExceptionalForTheEntity() {
        double value = 0.001;
        List<Double> oldValues = Arrays.asList(0.1,0.1);
        int globalInfluence = 1;
        double score = calcScore(globalInfluence, 0, oldValues, value);
        Assert.assertEquals(1, score, 1);
    }
}
