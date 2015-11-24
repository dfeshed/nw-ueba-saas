package fortscale.ml.model.prevalance.field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(JUnit4.class)
public class TimeModelTest {
	public static final int DAILY_TIME_RESOLUTION = 60 * 60 * 24;
	public static final int DAILY_BUCKET_SIZE = 60 * 10;

	private double getScore(List<Long> times, long timeToScore) {
		return new TimeModel(DAILY_TIME_RESOLUTION, DAILY_BUCKET_SIZE, times).calculateScore(timeToScore);
	}

	private void assertScore(List<Long> times, long timeToScore, double expected) {
		Assert.assertEquals(expected, getScore(times, timeToScore), 0.00001);
	}

	@Test
	public void elementaryCheck() {
		List<Long> times = new ArrayList<>();
		long epochSeconds = 1000;
		for (int i = 0; i < 100; i++) {
			times.add(epochSeconds);
		}
		assertScore(times, epochSeconds, 0);
	}
	
	@Test
	public void elementaryCheckWithOneOutlier() {
		List<Long> times = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			times.add(1000L);
		}
		long epochSeconds = 6600;
		times.add(epochSeconds);
		assertScore(times, epochSeconds, 44);
	}

	@Test
	public void testUniformlyRandomDistribution() {
		Random rnd = new Random(1);
		List<Long> times = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			times.add((long)(rnd.nextDouble( ) * DAILY_TIME_RESOLUTION));
		}

		for (int i = 0; i < times.size(); i++) {
			assertScore(times, times.get(i), 0);
		}
	}

	@Test
	public void testScoreOfIsolatedTimes() {
		Random rnd = new Random(1);
		List<Long> times = new ArrayList<>();
		for (int i = 0; i < 50; i++) {
			times.add((long)(rnd.nextDouble( ) * 6000));
		}
		long isolatedTime = 30000L;
		times.add(isolatedTime);
		assertScore(times, isolatedTime, 99);
		isolatedTime = 40000L;
		times.add(isolatedTime);
		assertScore(times, isolatedTime, 93);
		isolatedTime = 50000L;
		times.add(isolatedTime);
		assertScore(times, isolatedTime, 74);
		isolatedTime = 60000L;
		times.add(isolatedTime);
		assertScore(times, isolatedTime, 43);
		assertScore(times, 500, 0);
	}

	@Test
	public void testScoresInDifferentDistancesFromTheClusters() {
		Random rnd = new Random(1);
		List<Long> timesClustered = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 600);
			timesClustered.add(epochSeconds);
		}

		for (int i = 0; i < 2; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 600 + 6600);
			timesClustered.add(epochSeconds);
		}

		for (int i = 0; i < 46; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 2400 + 2400);
			timesClustered.add(epochSeconds);
		}

		double[] scores = new double[]{99, 93, 65, 13};
		long[] timesToScore = new long[]{14000, 11000, 10000, 9000};
		for (int i = 0; i < scores.length; i++) {
			List<Long> times = new ArrayList<>(timesClustered);
			times.add(timesToScore[i]);
			assertScore(times, timesToScore[i], scores[i]);
		}
	}

	@Test
	public void testScoresOfOneBigClusterAndManyDispersedTimes() {
		Random rnd = new Random(1);
		List<Long> times = new ArrayList<>();
		for (int i = 0; i < 50; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 3000);
			times.add(epochSeconds);
		}

		double scores[] = new double[]{99, 92, 74, 44};
		long dispersedTimes[] = new long[scores.length];
		for (int i = 0; i < scores.length; i++) {
			dispersedTimes[i] = 3000 + (i + 1) * 6000;
			times.add(dispersedTimes[i]);
			assertScore(times, dispersedTimes[i], scores[i]);
		}

		for (int i = 0; i < scores.length; i++) {
			assertScore(times, dispersedTimes[i], scores[scores.length - 1]);
		}
	}

	@Test
	// this test is built on the scenario of issue FV-3738.
	public void testNewWorkingTimeScore() {
		int scenarioSteps[] = new int[]{450, 900, 900};
		int scenarioNumberOfSteps[] = new int[]{16, 8, 4};
		int scenarioScoreThresholds[] = new int[]{0, 0, 16};
		for (int scenario = 0; scenario < scenarioSteps.length; scenario++) {
			List<Long> times = new ArrayList<>();
			int step = 200;
			for (int i = 0; i < 5; i++) {
				long epochSeconds = 0; //12AM UTC
				for (int j = 0; j < 18; j++) {
					times.add(epochSeconds);
					assertScore(times, epochSeconds, 0);
					epochSeconds += step;
				}
			}

			step = scenarioSteps[scenario];
			int numberOfSteps = scenarioNumberOfSteps[scenario];
			double prevCycleScores[] = new double[numberOfSteps];
			long epochSeconds = 43200; //12PM UTC
			double score;
			double prevScore = 100;
			for (int j = 0; j < numberOfSteps; j++) {
				times.add(epochSeconds);
				score = getScore(times, epochSeconds);
				Assert.assertTrue(prevScore >= score);
				prevCycleScores[j] = score;
				prevScore = score;
				epochSeconds += step;
			}

			for (int i = 0; i < 4; i++) {
				epochSeconds = 43200; //12PM UTC
				for (int j = 0; j < numberOfSteps; j++) {
					times.add(epochSeconds);
					score = getScore(times, epochSeconds);
					Assert.assertTrue(prevCycleScores[j] >= score);
					prevCycleScores[j] = score;
					epochSeconds += step;
				}
				for (int j = numberOfSteps - 2; j < numberOfSteps; j++) {
					times.add(epochSeconds);
					score = getScore(times, epochSeconds);
					Assert.assertTrue(score <= scenarioScoreThresholds[scenario]);
					prevCycleScores[j] = score;
					epochSeconds += step;
				}
			}
		}
	}
}
