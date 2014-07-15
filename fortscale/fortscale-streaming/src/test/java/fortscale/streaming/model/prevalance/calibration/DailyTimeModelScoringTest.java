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
		Assert.assertEquals(98, score,1);
		timeModel.update(60000);
		score = timeModel.score(60000);
		Assert.assertEquals(95, score,1);
		score = timeModel.score(500);
		Assert.assertEquals(5, score,1);
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
		Assert.assertEquals(98, score,1);
		
		timeModel.update(9000);
		score = timeModel.score(9000);
		Assert.assertEquals(84, score,1);
		
		timeModel.update(7500);
		score = timeModel.score(7500);
		Assert.assertEquals(42, score,1);
		
		
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
		Assert.assertEquals(97, score, 1);
		i++;
		
		epochSeconds = 3000 + ((i+1) * 6000);
		timeModel.update(epochSeconds);
		dispersedTimes[i] = epochSeconds;
		score = timeModel.score(dispersedTimes[i]);
		Assert.assertEquals(92, score, 1);
		i++;
		
		epochSeconds = 3000 + ((i+1) * 6000);
		timeModel.update(epochSeconds);
		dispersedTimes[i] = epochSeconds;
		score = timeModel.score(dispersedTimes[i]);
		Assert.assertEquals(86, score, 1);
		i++;
		
		epochSeconds = 3000 + ((i+1) * 6000);
		timeModel.update(epochSeconds);
		dispersedTimes[i] = epochSeconds;
		i++;
		
		for (int j = 0; j < 4; j++) {
			score = timeModel.score(dispersedTimes[j]);
			Assert.assertEquals(77, score, 1);
		}
		
		for (; i < 10; i++) {
			epochSeconds = 3000 + ((i+1) * 6000);
			timeModel.update(epochSeconds);
		}
		
		score = timeModel.score(dispersedTimes[0]);
		Assert.assertEquals(27, score, 1);
	}
}
