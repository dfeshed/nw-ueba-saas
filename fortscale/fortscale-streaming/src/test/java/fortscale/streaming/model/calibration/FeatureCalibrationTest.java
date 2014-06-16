package fortscale.streaming.model.calibration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FeatureCalibrationTest {

	private FeatureCalibration createNewFeatureCalibration(){
		return new FeatureCalibration();
	}
	@Test
	public void elementarycheckForFeatureCalibration() throws Exception {
		FeatureCalibration calibration = createNewFeatureCalibration();
		Map<String, Double> featureValueToCountMap = new HashMap<String, Double>();
		for (int i = 0; i < 100; i++) {
			featureValueToCountMap.put(String.format("test%d", i), 100D);
		}
		calibration.init(featureValueToCountMap);
		for (String featureValue : featureValueToCountMap.keySet()) {
			double score = calibration.score(featureValue);
			Assert.assertEquals(0.0, score);
			
		}
	}
	
	@Test
	public void testScoreOfNewFeatureValueWhichWasNotUpdatedInTheCalibration() throws Exception {
		FeatureCalibration calibration = createNewFeatureCalibration();
		Map<String, Double> featureValueToCountMap = new HashMap<String, Double>();
		for (int i = 0; i < 100; i++) {
			featureValueToCountMap.put(String.format("test%d", i), 100D);
		}
		calibration.init(featureValueToCountMap);
		double score = calibration.score("notExist");
		Assert.assertEquals(1.0, score);
	}
	
	@Test
	public void simpleInputOutputForFeatureCalibration() throws Exception{
		Random rnd = new Random(1);
		FeatureCalibration calibration = createNewFeatureCalibration();
		Map<String, Double> featureValueToCountMap = new HashMap<String, Double>();
		for (int i = 0; i < 100; i++) {
			double val = Math.min( 100.0, rnd.nextDouble( ) * 100 );
			featureValueToCountMap.put(String.format("test%d", i), val);
		}
		
		
		featureValueToCountMap.put("test101", 1D);
		calibration.init(featureValueToCountMap);
		double score = calibration.score( "test101" );
		Assert.assertEquals(0.93, score, 0.01);
		
		featureValueToCountMap.put("test101", 11D);
		calibration.init(featureValueToCountMap);
		score = calibration.score( "test101" );
		Assert.assertEquals(0.69, score, 0.01);
		
		featureValueToCountMap.put("test101", 12D);
		calibration.init(featureValueToCountMap);
		score = calibration.score( "test101" );
		Assert.assertEquals(0.67, score, 0.01);
		
		featureValueToCountMap.put("test101", 28D);
		calibration.init(featureValueToCountMap);
		score = calibration.score( "test101" );
		Assert.assertEquals(0.38, score, 0.01);
		
		featureValueToCountMap.put("test101", 64D);
		calibration.init(featureValueToCountMap);
		score = calibration.score( "test101" );
		Assert.assertEquals(0.0, score, 0.01);
	}
	
	@Test
	public void simpleInputOutputForFeatureCalibrationStreaming() throws Exception{
		Random rnd = new Random(1);
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		for (; i < 100; i++) {
			double val = Math.min( 100.0, rnd.nextDouble( ) * 100 );
			calibration.updateFeatureValueCount(String.format("test%d", i), val);
		}
		
		String featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		double score = calibration.score( featureValue );
		Assert.assertEquals(0.93, score, 0.01);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 11D);
		score = calibration.score( featureValue );
		Assert.assertEquals(0.67, score, 0.01);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 12D);
		score = calibration.score( featureValue );
		Assert.assertEquals(0.65, score, 0.01);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 28D);
		score = calibration.score( featureValue );
		Assert.assertEquals(0.37, score, 0.01);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 64D);
		score = calibration.score( featureValue );
		Assert.assertEquals(0.0, score, 0.01);
	}
	
	
	@Test
	public void testingScoreOfVeryRareFeatureValues() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String featureValue = String.format("test%d", i++);
		
		calibration.updateFeatureValueCount(featureValue, 50D);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		double score = calibration.score( featureValue );
		Assert.assertEquals(0.96, score, 0.01);
		
		String featureValue1 = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue1, 2D);
		score = calibration.score( featureValue );
		double score1 = calibration.score( featureValue1 );
		Assert.assertEquals(score, score1, 0.01);
		Assert.assertEquals(0.87, score, 0.01);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		score = calibration.score( featureValue );
		Assert.assertEquals(0.82, score, 0.01);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		score = calibration.score( featureValue );
		Assert.assertEquals(0.74, score, 0.01);
		
		for(int j = 0; j < 6; j++){
			featureValue = String.format("test%d", i++);
			calibration.updateFeatureValueCount(featureValue, 1D);
		}
		
		score = calibration.score( featureValue );
		Assert.assertEquals(0.25, score, 0.01);
	}
	
	@Test
	public void testingScoreOfRareFeatureValues() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String featureValue = String.format("test%d", i++);
		
		calibration.updateFeatureValueCount(featureValue, 50D);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 4D);
		double score = calibration.score( featureValue );		
		Assert.assertEquals(0.85, score, 0.01);
		
		String featureValue1 = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue1, 5D);
		score = calibration.score( featureValue );
		double score1 = calibration.score( featureValue1 );
		Assert.assertEquals(score, score1, 0.01);
		
		for(int j = 1; j < 5; j++){
			String tmp = String.format("test%d", i++);
			calibration.updateFeatureValueCount(tmp, 4D + j);
		}
		
		score = calibration.score( featureValue );
		Assert.assertEquals(0.53, score, 0.01);
		
	}
	
	@Test
	public void testingScoreOfOnlyRareFeatureValues() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String featureValue = String.format("test%d", i++);
		
		calibration.updateFeatureValueCount(featureValue, 1D);		
		
		for(int j = 0; j < 4; j++){
			String tmp = String.format("test%d", i++);
			calibration.updateFeatureValueCount(tmp, 1D + j);
		}
		
		double score = calibration.score( featureValue );
		Assert.assertEquals(0.43, score, 0.01);
	}
	
	@Test
	public void testingScoreOfMediumFeatureValueAgainstVeryLargeFetureValue() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 522D);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 22D);
		
		double score = calibration.score( featureValue );
		Assert.assertEquals(0.8, score, 0.01);
	}
	
	@Test
	public void testingScoreOfFewMediumFeatureValueAgainstVeryLargeFetureValue() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 522D);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 22D);
		
		for(int j = 1; j < 4; j++){
			String tmp = String.format("test%d", i++);
			calibration.updateFeatureValueCount(tmp, 22D + j);
		}
		
		double score = calibration.score( featureValue );
		Assert.assertEquals(0.48, score, 0.01);
	}
	
	@Test
	public void testingScoreOfRareFeatureValueAcrossTime() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String largeFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(largeFeatureValue, 20D);
		
		String rareFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(rareFeatureValue, 2D);
		
		double score = calibration.score( rareFeatureValue );
		Assert.assertEquals(0.88, score, 0.01);
		
		calibration.updateFeatureValueCount(largeFeatureValue, 100D);		
		calibration.updateFeatureValueCount(rareFeatureValue, 10D);
		
		score = calibration.score( rareFeatureValue );
		Assert.assertEquals(0.78, score, 0.01);
	}
}
