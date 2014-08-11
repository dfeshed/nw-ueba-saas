package fortscale.streaming.model.prevalance.calibration;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import fortscale.streaming.model.prevalance.field.DailyTimeModel;


@RunWith(JUnit4.class)
public class DailyTimeModelScoringTest {

	
	
	@Test
	public void elementarycheck() throws Exception {
		DailyTimeModel timeModel = new DailyTimeModel();
		
		long epochSeconds = 1000;
		for (int i = 0; i < 100; i++) {
			timeModel.update(epochSeconds);
		}
		
		double score = timeModel.score(epochSeconds);
		Assert.assertEquals(0.0, score);
	}
	
	@Test
	public void testUniformlyRandomDistribution() throws Exception{
		Random rnd = new Random(1);
		DailyTimeModel timeModel = new DailyTimeModel();
		for (int i = 0; i < 100; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * DailyTimeModel.TIME_RESOLUTION);
			timeModel.update(epochSeconds);
		}
		
		timeModel.update(10);
		double score = timeModel.score(10);
		Assert.assertEquals(0, score,1);
		timeModel.update(24600);
		score = timeModel.score(24600);
		Assert.assertEquals(0, score,1);
	}
	
	@Test
	public void testScoreOfIsolateTime() throws Exception{
		Random rnd = new Random(1);
		DailyTimeModel timeModel = new DailyTimeModel();
		for (int i = 0; i < 50; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 6000);
			timeModel.update(epochSeconds);
		}
		
		timeModel.update(30000);
		double score = timeModel.score(30000);
		Assert.assertEquals(99, score,1);
		timeModel.update(40000);
		score = timeModel.score(40000);
		Assert.assertEquals(93, score,1);
		timeModel.update(50000);
		score = timeModel.score(50000);
		Assert.assertEquals(74, score,1);
		timeModel.update(60000);
		score = timeModel.score(60000);
		Assert.assertEquals(43, score,1);
		score = timeModel.score(500);
		Assert.assertEquals(0, score,1);
	}
	
	@Test
	public void testScoresInDifferentDistancesFromTheCluster() throws Exception{
		DailyTimeModel timeModel = new DailyTimeModel();
		initDailyTimeModelForTestScoresInDifferentDistancesFromTheCluster(timeModel);
		
		timeModel.update(14000);
		double score = timeModel.score(14000);
		Assert.assertEquals(99, score,1);
	}
	
	private void initDailyTimeModelForTestScoresInDifferentDistancesFromTheCluster(DailyTimeModel timeModel) throws Exception{
		Random rnd = new Random(1);
		for (int i = 0; i < 2; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 600);
			timeModel.update(epochSeconds);
		}
		
		for (int i = 0; i < 2; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 600 + 6600);
			timeModel.update(epochSeconds);
		}
		
		for (int i = 0; i < 46; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 2400 + 2400);
			timeModel.update(epochSeconds);
		}
	}
	
	@Test
	public void testScoresInDifferentDistancesFromTheCluster1() throws Exception{
		DailyTimeModel timeModel = new DailyTimeModel();
		initDailyTimeModelForTestScoresInDifferentDistancesFromTheCluster(timeModel);
		
		timeModel.update(11000);
		double score = timeModel.score(11000);
		Assert.assertEquals(93, score,1);
	}
	
	@Test
	public void testScoresInDifferentDistancesFromTheCluster2() throws Exception{
		DailyTimeModel timeModel = new DailyTimeModel();
		initDailyTimeModelForTestScoresInDifferentDistancesFromTheCluster(timeModel);
		
		timeModel.update(10000);
		double score = timeModel.score(10000);
		Assert.assertEquals(65, score,1);
	}
	
	@Test
	public void testScoresInDifferentDistancesFromTheCluster3() throws Exception{
		DailyTimeModel timeModel = new DailyTimeModel();
		initDailyTimeModelForTestScoresInDifferentDistancesFromTheCluster(timeModel);
				
		timeModel.update(9000);
		double score = timeModel.score(9000);
		Assert.assertEquals(13, score,1);
		
		
	}
	
	@Test
	public void testScoresOfOneBigClusterAndManyDispersedTimes() throws Exception{
		Random rnd = new Random(1);
		DailyTimeModel timeModel = new DailyTimeModel();
		for (int i = 0; i < 50; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 3000);
			timeModel.update(epochSeconds);
		}
		
		long dispersedTimes[] = new long[4];
		int i = 0;
		
		long epochSeconds = 3000 + ((i+1) * 6000);
		timeModel.update(epochSeconds);
		dispersedTimes[i] = epochSeconds;
		double score = timeModel.score(dispersedTimes[i]);
		Assert.assertEquals(99, score, 1);
		i++;
		
		epochSeconds = 3000 + ((i+1) * 6000);
		timeModel.update(epochSeconds);
		dispersedTimes[i] = epochSeconds;
		score = timeModel.score(dispersedTimes[i]);
		Assert.assertEquals(93, score, 1);
		i++;
		
		epochSeconds = 3000 + ((i+1) * 6000);
		timeModel.update(epochSeconds);
		dispersedTimes[i] = epochSeconds;
		score = timeModel.score(dispersedTimes[i]);
		Assert.assertEquals(74, score, 1);
		i++;
		
		epochSeconds = 3000 + ((i+1) * 6000);
		timeModel.update(epochSeconds);
		dispersedTimes[i] = epochSeconds;
		i++;
		
		for (int j = 0; j < 4; j++) {
			score = timeModel.score(dispersedTimes[j]);
			Assert.assertEquals(44, score, 1);
		}
		
	}
	
	@Test
	//This test is built on the scenario of issue FV-3738.
	public void testNewWorkingTimeScore() throws Exception{
		DailyTimeModel timeModel = new DailyTimeModel();
		int step = 200;
		for (int i = 0; i < 5; i++) {
			long epochSeconds = 0; //12AM UTC
			for (int j = 0; j < 18; j++) {
				timeModel.update(epochSeconds);
				double score = timeModel.score(epochSeconds);
				Assert.assertEquals(0, score, 0.1);
				epochSeconds += step;
			}
		}
		
		step = 450;
		int numberOfSteps = 16;
		double prevCycleScores[] = new double[numberOfSteps];
		long epochSeconds = 43200; //12PM UTC
		double score = 100;
		double prevScore = 100;
		for (int j = 0; j < numberOfSteps; j++) {
			timeModel.update(epochSeconds);
			score = timeModel.score(epochSeconds);
			Assert.assertTrue(prevScore >= score);
			prevCycleScores[j] = score;
			prevScore = score;
			epochSeconds += step;
		}
		
		for (int i = 0; i < 4; i++) {
			epochSeconds = 43200; //12PM UTC
			score = 100;
			int j = 0;
			for (; j < numberOfSteps-2; j++) {
				timeModel.update(epochSeconds);
				score = timeModel.score(epochSeconds);
				Assert.assertTrue(prevCycleScores[j] >= score);
				prevCycleScores[j] = score;
				epochSeconds += step;
			}
			for (; j < numberOfSteps; j++) {
				timeModel.update(epochSeconds);
				score = timeModel.score(epochSeconds);
				Assert.assertTrue(score <= 6);
				prevCycleScores[j] = score;
				epochSeconds += step;
			}
		}
	}
	
	@Test
	//This test is built on the scenario of issue FV-3738.
	public void testNewWorkingTimeScore1() throws Exception{
		DailyTimeModel timeModel = new DailyTimeModel();
		int step = 200;
		for (int i = 0; i < 5; i++) {
			long epochSeconds = 0; //12AM UTC
			for (int j = 0; j < 18; j++) {
				timeModel.update(epochSeconds);
				double score = timeModel.score(epochSeconds);
				Assert.assertEquals(0, score, 0.1);
				epochSeconds += step;
			}
		}
		
		step = 900;
		int numberOfSteps = 8;
		double prevCycleScores[] = new double[numberOfSteps];
		long epochSeconds = 43200; //12PM UTC
		double score = 100;
		double prevScore = 100;
		for (int j = 0; j < numberOfSteps; j++) {
			timeModel.update(epochSeconds);
			score = timeModel.score(epochSeconds);
			Assert.assertTrue(prevScore >= score);
			prevCycleScores[j] = score;
			prevScore = score;
			epochSeconds += step;
		}
		
		for (int i = 0; i < 4; i++) {
			epochSeconds = 43200; //12PM UTC
			score = 100;
			int j = 0;
			for (; j < numberOfSteps-2; j++) {
				timeModel.update(epochSeconds);
				score = timeModel.score(epochSeconds);
				Assert.assertTrue(prevCycleScores[j] >= score);
				prevCycleScores[j] = score;
				epochSeconds += step;
			}
			for (; j < numberOfSteps; j++) {
				timeModel.update(epochSeconds);
				score = timeModel.score(epochSeconds);
				Assert.assertTrue(score <= 6);
				prevCycleScores[j] = score;
				epochSeconds += step;
			}
		}
	}
	
	@Test
	//This test is built on the scenario of issue FV-3738.
	public void testNewWorkingTimeScore2() throws Exception{
		DailyTimeModel timeModel = new DailyTimeModel();
		int step = 200;
		for (int i = 0; i < 5; i++) {
			long epochSeconds = 0; //12AM UTC
			for (int j = 0; j < 18; j++) {
				timeModel.update(epochSeconds);
				double score = timeModel.score(epochSeconds);
				Assert.assertEquals(0, score, 0.1);
				epochSeconds += step;
			}
		}
		
		step = 900;
		int numberOfSteps = 4;
		double prevCycleScores[] = new double[numberOfSteps];
		long epochSeconds = 43200; //12PM UTC
		double score = 100;
		double prevScore = 100;
		for (int j = 0; j < numberOfSteps; j++) {
			timeModel.update(epochSeconds);
			score = timeModel.score(epochSeconds);
			Assert.assertTrue(prevScore >= score);
			prevCycleScores[j] = score;
			prevScore = score;
			epochSeconds += step;
		}
		
		for (int i = 0; i < 4; i++) {
			epochSeconds = 43200; //12PM UTC
			score = 100;
			int j = 0;
			for (; j < numberOfSteps; j++) {
				timeModel.update(epochSeconds);
				score = timeModel.score(epochSeconds);
				Assert.assertTrue(prevCycleScores[j] >= score);
				prevCycleScores[j] = score;
				epochSeconds += step;
			}
		}
	}
}
