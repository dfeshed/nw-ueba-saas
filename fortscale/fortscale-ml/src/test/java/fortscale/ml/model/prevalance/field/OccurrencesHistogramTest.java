package fortscale.ml.model.prevalance.field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RunWith(JUnit4.class)
public class OccurrencesHistogramTest {
	@Test
	public void elementarycheckForFeatureCalibration() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		for (int i = 0; i < 100; i++) {
			featureValueToCountMap.put(String.format("test%d", i), 100D);
		}
		OccurrencesHistogram calibration = new OccurrencesHistogram(featureValueToCountMap);
		for (String featureValue : featureValueToCountMap.keySet()) {
			double score = calibration.score(featureValue);
			Assert.assertEquals(0.0, score, 0.0);
		}
	}
	
	@Test
	public void testScoreOfNewFeatureValueWhichWasNotUpdatedInTheCalibration() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		for (int i = 0; i < 100; i++) {
			featureValueToCountMap.put(String.format("test%d", i), 100D);
		}
		OccurrencesHistogram calibration = new OccurrencesHistogram(featureValueToCountMap);
		double score = calibration.score("notExist");
		Assert.assertEquals(100.0, score, 0.0);
	}
	
	@Test
	public void simpleInputOutputForFeatureCalibration() throws Exception{
		Random rnd = new Random(1);
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		for (int i = 0; i < 50; i++) {
			double val = Math.min( 100.0, rnd.nextDouble( ) * 100 + 8);
			featureValueToCountMap.put(String.format("test%d", i), val);
		}

		featureValueToCountMap.put("test101", 1D);
		OccurrencesHistogram calibration = new OccurrencesHistogram(featureValueToCountMap);
		double score = calibration.score( "test101" );
		Assert.assertEquals(99, score, 1);

		featureValueToCountMap.put("test101", 2D);
		calibration = new OccurrencesHistogram(featureValueToCountMap);
		score = calibration.score( "test101" );
		Assert.assertEquals(93, score, 1);

		featureValueToCountMap.put("test101", 3D);
		calibration = new OccurrencesHistogram(featureValueToCountMap);
		score = calibration.score( "test101" );
		Assert.assertEquals(61, score, 1);

		featureValueToCountMap.put("test101", 4D);
		calibration = new OccurrencesHistogram(featureValueToCountMap);
		score = calibration.score( "test101" );
		Assert.assertEquals(18, score, 1);
	}


	@Test
	public void testingScoreOfVeryRareFeatureValueAgainstVeryLargeFeatureValueWithValuesIncreasingByTime() throws Exception{
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int i = 0;
		String featureValue1 = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue1, 5000D);
		String featureValue = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue, 1D);
		double score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(99, score, 1);

		featureValueToCountMap.put(featureValue1, 10000D);
		featureValueToCountMap.put(featureValue, 2D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(96, score, 1);

		featureValueToCountMap.put(featureValue1, 15000D);
		featureValueToCountMap.put(featureValue, 3D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(93, score, 1);

		featureValueToCountMap.put(featureValue1, 20000D);
		featureValueToCountMap.put(featureValue, 4D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(90, score, 1);

		featureValueToCountMap.put(featureValue1, 40000D);
		featureValueToCountMap.put(featureValue, 8D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(81, score, 1);

		featureValueToCountMap.put(featureValue1, 80000D);
		featureValueToCountMap.put(featureValue, 16D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(70, score, 1);

		featureValueToCountMap.put(featureValue1, 160000D);
		featureValueToCountMap.put(featureValue, 32D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(56, score, 1);
	}

	@Test
	public void testingScoreOfVeryRareFeatureValuesAgainstVeryLargeFeatureValue() throws Exception{
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int i = 0;

		String featureValue = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue, 20000D);
		featureValue = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue, 1D);
		double score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(99, score, 1);

		String featureValue1 = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue1, 2D);
		OccurrencesHistogram occurrencesHistogram = new OccurrencesHistogram(featureValueToCountMap);
		score = occurrencesHistogram.score(featureValue);
		double score1 = occurrencesHistogram.score(featureValue1);
		Assert.assertEquals(score, score1, 1);
		Assert.assertEquals(94, score, 1);

		featureValue = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue, 2D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(87, score, 1);

		featureValue = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue, 1D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(76, score, 1);

		featureValue = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue, 1D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(59, score, 1);

		featureValue = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue, 1D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(38, score, 1);
	}

	@Test
	public void testingScoreOfOneVeryRareFeatureValueAndManyRareFeatureValuesAgainstVeryLargeFeatureValue() throws Exception{
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int i = 0;

		String veryLargefeatureValue = String.format("test%d", i++);

		featureValueToCountMap.put(veryLargefeatureValue, 20000D);

		String veryRarefeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(veryRarefeatureValue, 1D);
		double score = new OccurrencesHistogram(featureValueToCountMap).score(veryRarefeatureValue);
		Assert.assertEquals(99, score, 1);

		String rareFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(rareFeatureValue, 5D);
		OccurrencesHistogram occurrencesHistogram = new OccurrencesHistogram(featureValueToCountMap);
		score = occurrencesHistogram.score(veryRarefeatureValue);
		Assert.assertEquals(94, score, 1);
		score = occurrencesHistogram.score(rareFeatureValue);
		Assert.assertEquals(85, score, 1);

		rareFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(rareFeatureValue, 6D);
		occurrencesHistogram = new OccurrencesHistogram(featureValueToCountMap);
		score = occurrencesHistogram.score( veryRarefeatureValue );
		Assert.assertEquals(90, score, 1);
		score = occurrencesHistogram.score( rareFeatureValue );
		Assert.assertEquals(75, score, 1);

		rareFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(rareFeatureValue, 4D);
		occurrencesHistogram = new OccurrencesHistogram(featureValueToCountMap);
		score = occurrencesHistogram.score(veryRarefeatureValue);
		Assert.assertEquals(85, score, 1);
		score = occurrencesHistogram.score( rareFeatureValue );
		Assert.assertEquals(64, score, 1);

		rareFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(rareFeatureValue, 5D);
		occurrencesHistogram = new OccurrencesHistogram(featureValueToCountMap);
		score = occurrencesHistogram.score( veryRarefeatureValue );
		Assert.assertEquals(78, score, 1);
		score = occurrencesHistogram.score( rareFeatureValue );
		Assert.assertEquals(47, score, 1);

		rareFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(rareFeatureValue, 6D);
		occurrencesHistogram = new OccurrencesHistogram(featureValueToCountMap);
		score = occurrencesHistogram.score( veryRarefeatureValue );
		Assert.assertEquals(70, score, 1);
		score = occurrencesHistogram.score( rareFeatureValue );
		Assert.assertEquals(27, score, 1);

		rareFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(rareFeatureValue, 4D);
		occurrencesHistogram = new OccurrencesHistogram(featureValueToCountMap);
		score = occurrencesHistogram.score( veryRarefeatureValue );
		Assert.assertEquals(60, score, 1);
		score = occurrencesHistogram.score( rareFeatureValue );
		Assert.assertEquals(1, score, 1);
	}

	@Test
	public void testingScoreOfRareFeatureValuesAgainstMediumFeatureValue() throws Exception{
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int i = 0;

		String mediumFeatureValue = String.format("test%d", i++);

		featureValueToCountMap.put(mediumFeatureValue, 50D);

		String featureValue = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue, 4D);
		double score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(76, score, 1);

		String featureValue1 = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue1, 5D);
		OccurrencesHistogram occurrencesHistogram = new OccurrencesHistogram(featureValueToCountMap);
		score = occurrencesHistogram.score( featureValue );
		double score1 = occurrencesHistogram.score( featureValue1 );
		Assert.assertEquals(score, score1, 1);
		Assert.assertEquals(63, score, 1);

		featureValue1 = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue1, 5D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(45, score, 1);

		featureValue1 = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue1, 5D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(16, score, 1);
	}

	@Test
	public void testingScoreOfOnlyRareFeatureValues() throws Exception{
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int i = 0;

		String featureValue = String.format("test%d", i++);

		featureValueToCountMap.put(featureValue, 1D);

		for(int j = 0; j < 4; j++){
			String tmp = String.format("test%d", i++);
			featureValueToCountMap.put(tmp, 2D + j);
		}

		double score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(0, score, 1);
	}

	@Test
	public void testingScoreOfVeryRareFeatureValueAgainstMediumRareFeatureValues() throws Exception{
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int i = 0;

		String featureValue = String.format("test%d", i++);

		featureValueToCountMap.put(featureValue, 1D);

		for(int j = 0; j < 4; j++){
			String mediumRareFeatureValue = String.format("test%d", i++);
			featureValueToCountMap.put(mediumRareFeatureValue, 8D + j);
			double score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
			Assert.assertEquals(99, score, 1);
		}
	}

	@Test
	public void testingScoreOfRareFeatureValueAgainstMediumRareFeatureValues() throws Exception{
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int i = 0;

		String rareFeatureValue = String.format("test%d", i++);

		featureValueToCountMap.put(rareFeatureValue, 3D);

		String mediumRareFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(mediumRareFeatureValue, 8D);
		double score = new OccurrencesHistogram(featureValueToCountMap).score(rareFeatureValue);
		Assert.assertEquals(52, score, 1);

		mediumRareFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(mediumRareFeatureValue, 9D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(rareFeatureValue);
		Assert.assertEquals(48, score, 1);

		mediumRareFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(mediumRareFeatureValue, 10D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(rareFeatureValue);
		Assert.assertEquals(40, score, 1);
	}

	@Test
	public void testingScoreOfFewMediumFeatureValueAgainstVeryLargeFeatureValue() throws Exception{
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int i = 0;

		String veryLargeFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(veryLargeFeatureValue, 19000D);

		String mediumFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(mediumFeatureValue, 22D);
		double score = new OccurrencesHistogram(featureValueToCountMap).score(mediumFeatureValue);
		Assert.assertEquals(57, score, 1);

		mediumFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(mediumFeatureValue, 22D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(mediumFeatureValue);
		Assert.assertEquals(55, score, 1);

		mediumFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(mediumFeatureValue, 22D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(mediumFeatureValue);
		Assert.assertEquals(48, score, 1);

		mediumFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(mediumFeatureValue, 22D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(mediumFeatureValue);
		Assert.assertEquals(36, score, 1);

		mediumFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(mediumFeatureValue, 22D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(mediumFeatureValue);
		Assert.assertEquals(20, score, 1);

	}

	@Test
	public void testingScoreOfRareFeatureValueAgainstMediumFeatureValueAcrossTime() throws Exception{
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int i = 0;

		String largeFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(largeFeatureValue, 20D);

		String rareFeatureValue = String.format("test%d", i++);
		featureValueToCountMap.put(rareFeatureValue, 2D);

		double score = new OccurrencesHistogram(featureValueToCountMap).score(rareFeatureValue);
		Assert.assertEquals(90, score, 1);

		featureValueToCountMap.put(largeFeatureValue, 100D);
		featureValueToCountMap.put(rareFeatureValue, 10D);

		score = new OccurrencesHistogram(featureValueToCountMap).score(rareFeatureValue);
		Assert.assertEquals(48, score, 1);
	}

	@Test
	public void testRareToMediumFeatureValueAgainstMediumLargeFeatureValue() throws Exception{
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int i = 0;

		String featureValue = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue, 100D);
		double score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(0, score, 1);

		featureValue = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue, 1D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(99, score, 1);

		featureValueToCountMap.put(featureValue, 2D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(93, score, 1);

		featureValueToCountMap.put(featureValue, 3D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(86, score, 1);

		featureValueToCountMap.put(featureValue, 4D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(79, score, 1);

		featureValueToCountMap.put(featureValue, 5D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(73, score, 1);

		featureValueToCountMap.put(featureValue, 10D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(48, score, 1);

		featureValueToCountMap.put(featureValue, 20D);
		score = new OccurrencesHistogram(featureValueToCountMap).score(featureValue);
		Assert.assertEquals(14, score, 1);
	}
}
