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
		for (int i = 0; i < 50; i++) {
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
		Assert.assertEquals(61, score, 1);	
		
		featureValueToCountMap.put("test101", 4D);
		calibration.init(featureValueToCountMap);
		score = calibration.score( "test101" );
		Assert.assertEquals(18, score, 1);	
	}
	
	@Test
	public void simpleInputOutputForFeatureCalibrationStreaming() throws Exception{
		Random rnd = new Random(1);
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		for (; i < 50; i++) {
			double val = Math.min( 100.0, rnd.nextDouble( ) * 100 + 8);
			calibration.updateFeatureValueCount(String.format("test%d", i), val);
		}
		
		String featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		double score = calibration.score( featureValue );
		Assert.assertEquals(99, score, 1);
		
		calibration.updateFeatureValueCount(featureValue, 2D);
		score = calibration.score( featureValue );
		Assert.assertEquals(93, score, 1);
		
		calibration.updateFeatureValueCount(featureValue, 3D);
		score = calibration.score( featureValue );
		Assert.assertEquals(61, score, 1);	
		
		calibration.updateFeatureValueCount(featureValue, 4D);
		score = calibration.score( featureValue );
		Assert.assertEquals(18, score, 1);	
	}
	
	
	@Test
	public void testingScoreOfVeryRareFeatureValueAgainstVeryLargeFetureValueWithValuesIncreasingByTime() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String featureValue1 = String.format("test%d", i++);
		
		calibration.updateFeatureValueCount(featureValue1, 5000D);
		
		String featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		double score = calibration.score( featureValue );
		Assert.assertEquals(99, score, 1);
				
		calibration.updateFeatureValueCount(featureValue1, 10000D);
		calibration.updateFeatureValueCount(featureValue, 2D);
		score = calibration.score( featureValue );
		Assert.assertEquals(96, score, 1);
		
		calibration.updateFeatureValueCount(featureValue1, 15000D);
		calibration.updateFeatureValueCount(featureValue, 3D);
		score = calibration.score( featureValue );
		Assert.assertEquals(93, score, 1);
		
		calibration.updateFeatureValueCount(featureValue1, 20000D);
		calibration.updateFeatureValueCount(featureValue, 4D);
		score = calibration.score( featureValue );
		Assert.assertEquals(90, score, 1);
		
		calibration.updateFeatureValueCount(featureValue1, 40000D);
		calibration.updateFeatureValueCount(featureValue, 8D);
		score = calibration.score( featureValue );
		Assert.assertEquals(81, score, 1);
		
		calibration.updateFeatureValueCount(featureValue1, 80000D);
		calibration.updateFeatureValueCount(featureValue, 16D);
		score = calibration.score( featureValue );
		Assert.assertEquals(70, score, 1);
		
		calibration.updateFeatureValueCount(featureValue1, 160000D);
		calibration.updateFeatureValueCount(featureValue, 32D);
		score = calibration.score( featureValue );
		Assert.assertEquals(56, score, 1);
		
	}
	
	@Test
	public void testingScoreOfVeryRareFeatureValuesAgainstVeryLargeFetureValue() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String featureValue = String.format("test%d", i++);
		
		calibration.updateFeatureValueCount(featureValue, 20000D);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		double score = calibration.score( featureValue );
		Assert.assertEquals(99, score, 1);
		
		String featureValue1 = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue1, 2D);
		score = calibration.score( featureValue );
		double score1 = calibration.score( featureValue1 );
		Assert.assertEquals(score, score1, 1);
		Assert.assertEquals(94, score, 1);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 2D);
		score = calibration.score( featureValue );
		Assert.assertEquals(87, score, 1);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		score = calibration.score( featureValue );
		Assert.assertEquals(76, score, 1);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		score = calibration.score( featureValue );
		Assert.assertEquals(59, score, 1);
		
		featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 1D);
		score = calibration.score( featureValue );
		Assert.assertEquals(38, score, 1);
		
		
	}
	
	@Test
	public void testingScoreOfOneVeryRareFeatureValueAndManyRareFetureValuesAgainstVeryLargeFetureValue() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String veryLargefeatureValue = String.format("test%d", i++);
		
		calibration.updateFeatureValueCount(veryLargefeatureValue, 20000D);
		
		String veryRarefeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(veryRarefeatureValue, 1D);
		double score = calibration.score( veryRarefeatureValue );
		Assert.assertEquals(99, score, 1);
		
		String rareFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(rareFeatureValue, 5D);
		score = calibration.score( veryRarefeatureValue );
		Assert.assertEquals(94, score, 1);
		score = calibration.score( rareFeatureValue );
		Assert.assertEquals(85, score, 1);
		
		rareFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(rareFeatureValue, 6D);
		score = calibration.score( veryRarefeatureValue );
		Assert.assertEquals(90, score, 1);
		score = calibration.score( rareFeatureValue );
		Assert.assertEquals(75, score, 1);
		
		rareFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(rareFeatureValue, 4D);
		score = calibration.score( veryRarefeatureValue );
		Assert.assertEquals(85, score, 1);
		score = calibration.score( rareFeatureValue );
		Assert.assertEquals(64, score, 1);
		
		rareFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(rareFeatureValue, 5D);
		score = calibration.score( veryRarefeatureValue );
		Assert.assertEquals(78, score, 1);
		score = calibration.score( rareFeatureValue );
		Assert.assertEquals(47, score, 1);
		
		rareFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(rareFeatureValue, 6D);
		score = calibration.score( veryRarefeatureValue );
		Assert.assertEquals(70, score, 1);
		score = calibration.score( rareFeatureValue );
		Assert.assertEquals(27, score, 1);
		
		rareFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(rareFeatureValue, 4D);
		score = calibration.score( veryRarefeatureValue );
		Assert.assertEquals(60, score, 1);
		score = calibration.score( rareFeatureValue );
		Assert.assertEquals(1, score, 1);
	}
	
	@Test
	public void testingScoreOfRareFeatureValuesAgainstMediumFeatureValue() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String mediumFeatureValue = String.format("test%d", i++);
		
		calibration.updateFeatureValueCount(mediumFeatureValue, 50D);
		
		String featureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue, 4D);
		double score = calibration.score( featureValue );		
		Assert.assertEquals(76, score, 1);
		
		String featureValue1 = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue1, 5D);
		score = calibration.score( featureValue );
		double score1 = calibration.score( featureValue1 );
		Assert.assertEquals(score, score1, 1);
		Assert.assertEquals(63, score, 1);
		
		featureValue1 = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue1, 5D);
		score = calibration.score( featureValue );
		Assert.assertEquals(45, score, 1);
		
		featureValue1 = String.format("test%d", i++);
		calibration.updateFeatureValueCount(featureValue1, 5D);
		score = calibration.score( featureValue );
		Assert.assertEquals(16, score, 1);
		
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
	public void testingScoreOfVeryRareFeatureValueAgainstMediumRareFeatureValues() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String featureValue = String.format("test%d", i++);
		
		calibration.updateFeatureValueCount(featureValue, 1D);		
		
		for(int j = 0; j < 4; j++){
			String mediumRareFeatureValue = String.format("test%d", i++);
			calibration.updateFeatureValueCount(mediumRareFeatureValue, 8D + j);
			double score = calibration.score( featureValue );
			Assert.assertEquals(99, score, 1);
		}
	}
	
	@Test
	public void testingScoreOfRareFeatureValueAgainstMediumRareFeatureValues() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String rareFeatureValue = String.format("test%d", i++);
		
		calibration.updateFeatureValueCount(rareFeatureValue, 3D);		
		
		String mediumRareFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(mediumRareFeatureValue, 8D);		
		double score = calibration.score( rareFeatureValue );
		Assert.assertEquals(52, score, 1);
		
		mediumRareFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(mediumRareFeatureValue, 9D);		
		score = calibration.score( rareFeatureValue );
		Assert.assertEquals(48, score, 1);
		
		mediumRareFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(mediumRareFeatureValue, 10D);		
		score = calibration.score( rareFeatureValue );
		Assert.assertEquals(40, score, 1);
	}
		
	@Test
	public void testingScoreOfFewMediumFeatureValueAgainstVeryLargeFetureValue() throws Exception{
		FeatureCalibration calibration = createNewFeatureCalibration();
		int i = 0;
		
		String veryLargeFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(veryLargeFeatureValue, 19000D);
		
		String mediumFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(mediumFeatureValue, 22D);
		double score = calibration.score( mediumFeatureValue );
		Assert.assertEquals(57, score, 1);
		
		mediumFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(mediumFeatureValue, 22D);
		score = calibration.score( mediumFeatureValue );
		Assert.assertEquals(55, score, 1);
		
		mediumFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(mediumFeatureValue, 22D);
		score = calibration.score( mediumFeatureValue );
		Assert.assertEquals(48, score, 1);
		
		mediumFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(mediumFeatureValue, 22D);
		score = calibration.score( mediumFeatureValue );
		Assert.assertEquals(36, score, 1);
		
		mediumFeatureValue = String.format("test%d", i++);
		calibration.updateFeatureValueCount(mediumFeatureValue, 22D);
		score = calibration.score( mediumFeatureValue );
		Assert.assertEquals(20, score, 1);
		
	}
	
	@Test
	public void testingScoreOfRareFeatureValueAgainstMediumFeatureValueAcrossTime() throws Exception{
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
	public void testRareToMediumFetureValueAgainstMediumLargeFeatureValue() throws Exception{
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
