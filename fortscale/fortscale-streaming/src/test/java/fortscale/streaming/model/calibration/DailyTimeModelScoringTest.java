package fortscale.streaming.model.calibration;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import fortscale.streaming.model.field.DailyTimeModel;


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
		Assert.assertEquals(0.0, score,0.01);
		timeModel.update(24600);
		score = timeModel.score(24600);
		Assert.assertEquals(0.02, score,0.01);
	}
	
	@Test
	public void testScoreOfIsolateTime() throws Exception{
		Random rnd = new Random(1);
		DailyTimeModel timeModel = new DailyTimeModel();
		for (int i = 0; i < 30; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 6000);
			timeModel.update(epochSeconds);
		}
		
		timeModel.update(30000);
		double score = timeModel.score(30000);
		Assert.assertEquals(0.98, score,0.01);
		score = timeModel.score(60000);
		Assert.assertEquals(0.98, score,0.01);
		score = timeModel.score(500);
		Assert.assertEquals(0.05, score,0.01);
	}
	
	@Test
	public void testScoresInDifferentDistancesFromTheCluster() throws Exception{
		Random rnd = new Random(1);
		DailyTimeModel timeModel = new DailyTimeModel();
		for (int i = 0; i < 30; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 6000);
			timeModel.update(epochSeconds);
		}
		
		timeModel.update(12000);
		double score = timeModel.score(12000);
		Assert.assertEquals(0.98, score,0.01);
		
		timeModel.update(9000);
		score = timeModel.score(9000);
		Assert.assertEquals(0.6, score,0.01);
		
		timeModel.update(7500);
		score = timeModel.score(7500);
		Assert.assertEquals(0.18, score,0.01);
		
		
	}
	
	@Test
	public void testScoresOfOneBigClusterAndManyDispersedTimes() throws Exception{
		Random rnd = new Random(1);
		DailyTimeModel timeModel = new DailyTimeModel();
		for (int i = 0; i < 50; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 3000);
			timeModel.update(epochSeconds);
		}
		
		long dispersedTimes[] = new long[10];
		for (int i = 0; i < 10; i++) {
			long epochSeconds = 3000 + ((i+1) * 6000);
			timeModel.update(epochSeconds);
			dispersedTimes[i] = epochSeconds;
		}
		
		for (int i = 0; i < 10; i++) {
			double score = timeModel.score(dispersedTimes[i]);
			Assert.assertEquals(0.71, score, 0.01);
		}
	}
}
