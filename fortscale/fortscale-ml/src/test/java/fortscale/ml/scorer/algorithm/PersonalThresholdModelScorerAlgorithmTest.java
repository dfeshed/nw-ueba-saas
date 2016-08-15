package fortscale.ml.scorer.algorithm;

import fortscale.ml.model.PersonalThresholdModel;
import fortscale.ml.scorer.algorithms.PersonalThresholdModelScorerAlgorithm;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class PersonalThresholdModelScorerAlgorithmTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenGivenNullAsModel() {
		PersonalThresholdModelScorerAlgorithm.calculateScore(0.9, 10, null, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenGivenNegativeAsHighScoreProbability() {
		PersonalThresholdModelScorerAlgorithm.calculateScore(-0.00001, 10, new PersonalThresholdModel(10, 100, 0.9), 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenGivenBiggerThanOneAsHighScoreProbability() {
		PersonalThresholdModelScorerAlgorithm.calculateScore(1.001, 10, new PersonalThresholdModel(10, 100, 0.9), 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenGivenZeroAsNumOfSamples() {
		PersonalThresholdModelScorerAlgorithm.calculateScore(0.9, 0, new PersonalThresholdModel(10, 100, 0.9), 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenGivenZeroAsMaxRatioFromUniformThreshold() {
		PersonalThresholdModelScorerAlgorithm.calculateScore(0.9, 10, new PersonalThresholdModel(10, 100, 0.9), 0);
	}

	@Test
	public void shouldGiveScore0To0() {
		double score = PersonalThresholdModelScorerAlgorithm.calculateScore(0, 10, new PersonalThresholdModel(10, 100, 0.9), 99999);

		Assert.assertEquals(0, score, 0.00001);
	}

	@Test
	public void shouldGiveScore100To1() {
		double score = PersonalThresholdModelScorerAlgorithm.calculateScore(1, 10, new PersonalThresholdModel(10, 100, 0.9), 99999);

		Assert.assertEquals(100, score, 0.00001);
	}

	@Test
	public void distanceFrom50ShouldHaveTheSameSignAsDistanceFromThreshold() {
		PersonalThresholdModel model = new PersonalThresholdModel(100, 1000, 0.9);
		int numOfSamples = 10;
		double threshold = model.calcThreshold(numOfSamples, 99999);

		Assert.assertEquals(50, PersonalThresholdModelScorerAlgorithm.calculateScore(threshold, numOfSamples, model, 99999), 0.00001);
		Assert.assertTrue(PersonalThresholdModelScorerAlgorithm.calculateScore(threshold - 0.01, numOfSamples, model, 99999) < 50);
		Assert.assertTrue(PersonalThresholdModelScorerAlgorithm.calculateScore(threshold + 0.01, numOfSamples, model, 99999) > 50);
	}

	@Test
	public void shouldScoreIncreasinglyAsHighScoreProbabilityIncreases() {
		PersonalThresholdModel model = new PersonalThresholdModel(100, 1000, 0.9);
		List<Double> scores = IntStream.range(0, 100)
				.mapToDouble(i -> i / 100.0)
				.map(highScoreProbability -> PersonalThresholdModelScorerAlgorithm.calculateScore(highScoreProbability, 10, model, 99999))
				.boxed()
				.collect(Collectors.toList());
		ScorerAlgorithmTestUtils.assertScoresIncrease(scores);
	}

	@Test
	public void shouldScoreDecreasinglyAsNumOfSamplesIncreases() {
		PersonalThresholdModel model = new PersonalThresholdModel(100, 1000, 0.9);
		List<Double> scores = IntStream.range(1, 100)
				.mapToDouble(numOfSamples -> PersonalThresholdModelScorerAlgorithm.calculateScore(0.9, numOfSamples, model, 99999))
				.boxed()
				.collect(Collectors.toList());
		ScorerAlgorithmTestUtils.assertScoresDecrease(scores);
	}

	@Test
	public void shouldScoreIncreasinglyAsMaxRatioFromUniformThresholdIncreases() {
		PersonalThresholdModel model = new PersonalThresholdModel(100, 1000, 0.9);
		List<Double> scores = IntStream.range(1, 10)
				.mapToDouble(maxRatioFromUniformThreshold -> PersonalThresholdModelScorerAlgorithm.calculateScore(0.9, 1, model, maxRatioFromUniformThreshold))
				.boxed()
				.collect(Collectors.toList());
		ScorerAlgorithmTestUtils.assertScoresIncrease(scores);
	}
}
