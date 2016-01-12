package fortscale.ml.model.prevalance.field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@RunWith(JUnit4.class)
public class TimeModelTest extends AbstractModelTest {
	public static final int DAILY_TIME_RESOLUTION = 60 * 60 * 24;
	public static final int DAILY_BUCKET_SIZE = 60 * 10;

	private double calcScore(List<Long> times, long timeToScore) {
		Map<Long, Double> timeToCounter = new HashMap<>();
		for (long time : times) {
			timeToCounter.put(time, timeToCounter.getOrDefault(time, 0D) + 1);
		}
		return calcScore(timeToCounter, timeToScore);
	}

	private double calcScore(Map<Long, Double> timeToCounter, long timeToScore) {
		return new TimeModel(DAILY_TIME_RESOLUTION, DAILY_BUCKET_SIZE, timeToCounter).calculateScore(timeToScore);
	}

	private void assertScore(List<Long> times, long timeToScore, double expected) {
		Assert.assertEquals(expected, calcScore(times, timeToScore), 0.00001);
	}

	/*************************************************************************************
	 *************************************************************************************
	 ****************** TEST VARIOUS SCENARIOS - FROM BASIC TO ADVANCED ******************
	 *************************************************************************************
	 *************************************************************************************/

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
		long isolatedTimes[] = new long[]{30000, 40000, 50000, 60000};
		double scores[] = new double[]{99, 93, 74, 43};
		for (int i = 0; i < scores.length; i++) {
			times.add(isolatedTimes[i]);
			assertScore(times, isolatedTimes[i], scores[i]);
		}
		assertScore(times, 500, 0);
	}

	@Test
	public void testScoresInDifferentDistancesFromTheClusters() {
		Random rnd = new Random(1);
		List<Long> timesClustered = new ArrayList<>();
		int clusterSizes[] = new int[]{2, 2, 46};
		int clusterSpans[] = new int[]{600, 600, 2400};
		int clusterOffsets[] = new int[]{0, 6600, 2400};
		for (int cluster = 0; cluster < clusterSizes.length; cluster++) {
			for (int i = 0; i < clusterSizes[cluster]; i++) {
				long epochSeconds = (long)(rnd.nextDouble( ) * clusterSpans[cluster] + clusterOffsets[cluster]);
				timesClustered.add(epochSeconds);
			}
		}

		long[] timesToScore = new long[]{14000, 11000, 10000, 9000};
		double[] scores = new double[]{99, 93, 65, 13};
		for (int i = 0; i < timesToScore.length; i++) {
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
				score = calcScore(times, epochSeconds);
				Assert.assertTrue(prevScore >= score);
				prevCycleScores[j] = score;
				prevScore = score;
				epochSeconds += step;
			}

			for (int i = 0; i < 4; i++) {
				epochSeconds = 43200; //12PM UTC
				for (int j = 0; j < numberOfSteps; j++) {
					times.add(epochSeconds);
					score = calcScore(times, epochSeconds);
					Assert.assertTrue(prevCycleScores[j] >= score);
					prevCycleScores[j] = score;
					epochSeconds += step;
				}
				for (int j = numberOfSteps - 2; j < numberOfSteps; j++) {
					times.add(epochSeconds);
					score = calcScore(times, epochSeconds);
					Assert.assertTrue(score <= scenarioScoreThresholds[scenario]);
					prevCycleScores[j] = score;
					epochSeconds += step;
				}
			}
		}
	}

	/*************************************************************************************
	 *************************************************************************************
	 ***************************** TEST REAL DATA SCENARIOS ******************************
	 ***************** THESE TESTS ARE MORE OF RESEARCH SCRIPTS THAN TESTS ***************
	 ********* THEY ARE MEANT FOR RUNNING REAL DATA SCENARIOS AND THEN INSPECTING ********
	 ************ THE RESULTS BY HANDS (ALTHOUGH ASSERTS COULD BE USED AS WELL) **********
	 ************** READ AbstractModelTest.java'S DOCUMENTATION FOR MORE INFO ************
	 *************************************************************************************
	 *************************************************************************************/

	private class TimeModelScenarioCallbacks implements ScenarioCallbacks {
		private Map<Long, Double> timeToCounter;

		@Override
		public void onScenarioRunStart() {
			timeToCounter = new HashMap<>();
		}

		@Override
		public Double onScore(TestEventsBatch eventsBatch) {
			return calcScore(timeToCounter, eventsBatch.time_bucket);
		}

		@Override
		public void onPrintEvent(TestEventsBatch eventsBatch, Double score) {
			println(eventsBatch + " -> " + score);
		}

		@Override
		public void onFinishProcessEvent(TestEventsBatch eventsBatch) {
			// note: the reason we calc timeInDay (and not use time_bucket) is that big scenarios
			// take ridiculous amount of time otherwise. This of course should be fixed in the model
			long timeInDay = eventsBatch.time_bucket % DAILY_TIME_RESOLUTION;
			timeToCounter.put(timeInDay, timeToCounter.getOrDefault(timeInDay, 0D) + 1);
		}
	}

	@Test
	public void testRealScenarioSshSrcMachineUsername_278997272() throws IOException {
		try {
			runAndPrintRealScenario(new TimeModelScenarioCallbacks(), "username_278997272.csv", 0);
		} catch (FileNotFoundException e) {
			println("file not found");
		}
	}

	@Test
	public void testRealScenariosHowManyAnomalousUsers() throws IOException {
		testRealScenariosHowManyAnomalousUsers(new TimeModelScenarioCallbacks(), 0.055, 50, 920);
	}
}
