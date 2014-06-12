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
		Assert.assertEquals(0.0, score,0.01);
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
		Assert.assertEquals(0.98, score,0.01);
		timeModel.update(60000);
		score = timeModel.score(60000);
		Assert.assertEquals(0.98, score,0.01);
		score = timeModel.score(500);
		Assert.assertEquals(0.07, score,0.01);
	}
	
	@Test
	public void testScoresInDifferentDistancesFromTheCluster() throws Exception{
		Random rnd = new Random(1);
		DailyTimeModel timeModel = new DailyTimeModel();
		for (int i = 0; i < 50; i++) {
			long epochSeconds = (long)(rnd.nextDouble( ) * 6000);
			timeModel.update(epochSeconds);
		}
		
		timeModel.update(12000);
		double score = timeModel.score(12000);
		Assert.assertEquals(0.98, score,0.01);
		
		timeModel.update(9000);
		score = timeModel.score(9000);
		Assert.assertEquals(0.74, score,0.01);
		
		timeModel.update(7500);
		score = timeModel.score(7500);
		Assert.assertEquals(0.4, score,0.01);
		
		
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
		for (; i < 4; i++) {
			long epochSeconds = 3000 + ((i+1) * 6000);
			timeModel.update(epochSeconds);
			dispersedTimes[i] = epochSeconds;
		}
		
		for (int j = 0; j < 4; j++) {
			double score = timeModel.score(dispersedTimes[j]);
			Assert.assertEquals(0.86, score, 0.01);
		}
		
		for (; i < 10; i++) {
			long epochSeconds = 3000 + ((i+1) * 6000);
			timeModel.update(epochSeconds);
		}
		
		double score = timeModel.score(dispersedTimes[0]);
		Assert.assertEquals(0.28, score, 0.01);
	}
}
