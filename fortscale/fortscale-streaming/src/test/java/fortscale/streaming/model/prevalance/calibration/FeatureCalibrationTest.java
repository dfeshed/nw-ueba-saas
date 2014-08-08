package fortscale.streaming.model.prevalance.calibration;

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
		Assert.assertEquals(100.0, score);
	}
	
	@Test
	public void simpleInputOutputForFeatureCalibration() throws Exception{
		Random rnd = new Random(1);
		FeatureCalibration calibration = createNewFeatureCalibration();
		Map<String, Double> featureValueToCountMap = new HashMap<String, Double>();
		for (int i = 0; i < 100; i++) {
			double val = Math.min( 100.0, rnd.nextDouble( ) * 100 + 8);
			featureValueToCountMap.put(String.format("test%d", i), val);
		}
		
		
		featureValueToCountMap.put("test101", 1D);
		calibration.init(featureValueToCountMap);
		double score = calibration.score( "test101" );
		Assert.assertEquals(99, score, 1);
		
		featureValueToCountMap.put("test101", 2D);
		calibration.init(featureValueToCountMap);
		score = calibration.score( "test101" );
		Assert.assertEquals(93, score, 1);
		
		featureValueToCountMap.put("test101", 3D);
		calibration.init(featureValueToCountMap);
		score = calibration.score( "test101" );
		Assert.assertEquals(0, score, 1);		
	}
	
	@Test
	public void simpleInputOutputForFeatureCalibrationStreaming() throws Exception{
		Random rnd = new Random(1);
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		for (; i < 100; i++) {
			double val = Math.min( 100.0, rnd.nextDouble( ) * 100 + 8);
			calibration.updateFeatureValueCount(String.format("test%d", i), val);
		}
		
		String featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		double score = calibration.score( featureValue );
		Assert.assertEquals(99, score, 1);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 2D);
		score = calibration.score( featureValue );
		Assert.assertEquals(88, score, 1);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 3D);
		score = calibration.score( featureValue );
		Assert.assertEquals(0, score, 1);		
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
		Assert.assertEquals(99, score, 1);
		
		String featureValue1 = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue1, 2D);
		score = calibration.score( featureValue );
		double score1 = calibration.score( featureValue1 );
		Assert.assertEquals(score, score1, 1);
		Assert.assertEquals(86, score, 1);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		score = calibration.score( featureValue );
		Assert.assertEquals(68, score, 1);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		score = calibration.score( featureValue );
		Assert.assertEquals(21, score, 1);
		
		for(int j = 0; j < 6; j++){
			featureValue = String.format("test%d", i++);
			calibration.updateFeatureValueCount(featureValue, 1D);
		}
		
		score = calibration.score( featureValue );
		Assert.assertEquals(0, score, 1);
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
		Assert.assertEquals(76, score, 1);
		
		String featureValue1 = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue1, 5D);
		score = calibration.score( featureValue );
		double score1 = calibration.score( featureValue1 );
		Assert.assertEquals(score, score1, 1);
		int j = 1;
		for(; j < 3; j++){
			String tmp = String.format("test%d", i++);
			calibration.updateFeatureValueCount(tmp, 4D + j);
		}
		
		score = calibration.score( featureValue );
		Assert.assertEquals(0, score, 1);		
	}
	
	@Test
	public void testingScoreOfOnlyRareFeatureValues() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String featureValue = String.format("test%d", i++);
		
		calibration.updateFeatureValueCount(featureValue, 1D);		
		
		for(int j = 0; j < 4; j++){
			String tmp = String.format("test%d", i++);
			calibration.updateFeatureValueCount(tmp, 2D + j);
		}
		
		double score = calibration.score( featureValue );
		Assert.assertEquals(0, score, 1);
	}
	
	@Test
	public void testingScoreOfVeryRareFeatureValueAgainstMediumFeatureValue() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String featureValue = String.format("test%d", i++);
		
		calibration.updateFeatureValueCount(featureValue, 1D);		
		
		for(int j = 0; j < 4; j++){
			String tmp = String.format("test%d", i++);
			calibration.updateFeatureValueCount(tmp, 8D + j);
		}
		
		double score = calibration.score( featureValue );
		Assert.assertEquals(99, score, 1);
	}
	
	@Test
	public void testingScoreOfMediumFeatureValueAgainstVeryLargeFetureValue() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 19000D);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 22D);
		
		double score = calibration.score( featureValue );
		Assert.assertEquals(57, score, 1);
	}
	
	@Test
	public void testingScoreOfFewMediumFeatureValueAgainstVeryLargeFetureValue() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 522D);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 22D);
		
		for(int j = 1; j < 2; j++){
			String tmp = String.format("test%d", i++);
			calibration.updateFeatureValueCount(tmp, 22D + j);
		}
		
		double score = calibration.score( featureValue );
		Assert.assertEquals(27, score, 1);
		
		for(int j = 1; j < 2; j++){
			String tmp = String.format("test%d", i++);
			calibration.updateFeatureValueCount(tmp, 22D + j);
		}
		
		score = calibration.score( featureValue );
		Assert.assertEquals(16, score, 1);
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
		Assert.assertEquals(90, score, 1);
		
		calibration.updateFeatureValueCount(largeFeatureValue, 100D);		
		calibration.updateFeatureValueCount(rareFeatureValue, 10D);
		
		score = calibration.score( rareFeatureValue );
		Assert.assertEquals(48, score, 1);
	}
	
	@Test
	public void test() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();

		int i = 0;
		
		String featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 100D);
		double score = calibration.score( featureValue );
		Assert.assertEquals(0, score, 1);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		score = calibration.score( featureValue );
		Assert.assertEquals(99, score, 1);
		
		calibration.updateFeatureValueCount(featureValue, 2D);
		score = calibration.score( featureValue );
		Assert.assertEquals(93, score, 1);
		
		calibration.updateFeatureValueCount(featureValue, 3D);
		score = calibration.score( featureValue );
		Assert.assertEquals(86, score, 1);
		
		calibration.updateFeatureValueCount(featureValue, 4D);
		score = calibration.score( featureValue );
		Assert.assertEquals(79, score, 1);	
		
		calibration.updateFeatureValueCount(featureValue, 5D);
		score = calibration.score( featureValue );
		Assert.assertEquals(73, score, 1);
		
		calibration.updateFeatureValueCount(featureValue, 10D);
		score = calibration.score( featureValue );
		Assert.assertEquals(48, score, 1);	
		
		calibration.updateFeatureValueCount(featureValue, 20D);
		score = calibration.score( featureValue );
		Assert.assertEquals(14, score, 1);	
	}
}
