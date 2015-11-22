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
		double count = 100D;
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		for (int i = 0; i < 100; i++) {
			featureValueToCountMap.put(String.format("test%d", i), count);
		}
		OccurrencesHistogram calibration = new OccurrencesHistogram(featureValueToCountMap);
		double score = calibration.score(count);
		Assert.assertEquals(0.0, score, 0.0);
	}
	
	@Test
	public void testScoreOfNewFeatureValueWhichWasNotUpdatedInTheCalibration() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		for (int i = 0; i < 100; i++) {
			featureValueToCountMap.put(String.format("test%d", i), 100D);
		}
		OccurrencesHistogram calibration = new OccurrencesHistogram(featureValueToCountMap);
		Assert.assertEquals(100.0, calibration.score(null), 0.0);
		Assert.assertEquals(100.0, calibration.score(0D), 0.0);
	}

	@Test
	public void simpleInputOutputForFeatureCalibration() throws Exception {
		Random rnd = new Random(1);
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		for (int i = 0; i < 50; i++) {
			double val = Math.min( 100.0, rnd.nextDouble( ) * 100 + 8);
			featureValueToCountMap.put(String.format("test%d", i), val);
		}

		double[] counts = new double[]{1, 2, 3, 4};
		double[] scores = new double[]{99, 93, 61, 18};
		for (int i = 0; i < scores.length; i++) {
			featureValueToCountMap.put("feature", counts[i]);
			OccurrencesHistogram calibration = new OccurrencesHistogram(featureValueToCountMap);
			double score = calibration.score(counts[i]);
			Assert.assertEquals(scores[i], score, 1);
		}
	}

	@Test
	public void testingScoreOfVeryRareFeatureValueAgainstVeryLargeFeatureValueWithValuesIncreasingByTime() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		double[] rareCounts = new double[]{1, 2, 3, 4, 8, 16, 32};
		double[] commonCounts = new double[]{5000, 10000, 15000, 20000, 40000, 80000, 160000};
		double[] scores = new double[]{99, 96, 93, 90, 81, 70, 56};
		for (int i = 0; i < scores.length; i++) {
			featureValueToCountMap.put("commonFeature", commonCounts[i]);
			featureValueToCountMap.put("rareFeature", rareCounts[i]);
			double score = new OccurrencesHistogram(featureValueToCountMap).score(rareCounts[i]);
			Assert.assertEquals(scores[i], score, 1);
		}
	}

	@Test
	public void testingScoreOfVeryRareFeatureValuesAgainstVeryLargeFeatureValue() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		String commonFeature = "commonFeature";
		featureValueToCountMap.put(commonFeature, 20000D);
		String rareFeatureA = "rareFeatureA";
		featureValueToCountMap.put(rareFeatureA, 1D);
		double score = new OccurrencesHistogram(featureValueToCountMap).score(featureValueToCountMap.get(rareFeatureA));
		Assert.assertEquals(99, score, 1);

		String rareFeatureB = "rareFeatureB";
		featureValueToCountMap.put(rareFeatureB, 2D);
		OccurrencesHistogram occurrencesHistogram = new OccurrencesHistogram(featureValueToCountMap);
		score = occurrencesHistogram.score(featureValueToCountMap.get(rareFeatureA));
		double score1 = occurrencesHistogram.score(featureValueToCountMap.get(rareFeatureB));
		Assert.assertEquals(score, score1, 1);
		Assert.assertEquals(94, score, 1);

		double[] counts = new double[]{2, 2, 1, 1, 1};
		double[] scores = new double[]{87, 76, 59, 38};
		for (int i = 0; i < scores.length; i++) {
			featureValueToCountMap.put(String.format("rareFeature-%d", i), counts[i]);
			score = new OccurrencesHistogram(featureValueToCountMap).score(counts[i]);
			Assert.assertEquals(scores[i], score, 1);
		}
	}

	@Test
	public void testingScoreOfOneVeryRareFeatureValueAndManyRareFeatureValuesAgainstVeryLargeFeatureValue() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		String veryCommonFeatureValue = "veryCommonFeatureValue";
		featureValueToCountMap.put(veryCommonFeatureValue, 20000D);
		String veryRareFeatureValue = "veryRareFeatureValue";
		double veryRareFeatureCount = 1D;
		featureValueToCountMap.put(veryRareFeatureValue, veryRareFeatureCount);
		double score = new OccurrencesHistogram(featureValueToCountMap).score(veryRareFeatureCount);
		Assert.assertEquals(99, score, 1);

		double[] rareFeatureCounts = new double[]{5, 6, 4, 5, 6, 4};
		double[] rareFeaturesScores = new double[]{85, 75, 64, 47, 27, 1};
		double[] veryRareFeaturesScores = new double[]{94, 90, 85, 78, 70, 60};
		for (int i = 0; i < rareFeatureCounts.length; i++) {
			featureValueToCountMap.put(String.format("rareFeatureValue-%d", i), rareFeatureCounts[i]);
			OccurrencesHistogram occurrencesHistogram = new OccurrencesHistogram(featureValueToCountMap);
			score = occurrencesHistogram.score(veryRareFeatureCount);
			Assert.assertEquals(veryRareFeaturesScores[i], score, 1);
			score = occurrencesHistogram.score(rareFeatureCounts[i]);
			Assert.assertEquals(rareFeaturesScores[i], score, 1);
		}
	}

	@Test
	public void testingScoreOfRareFeatureValuesAgainstMediumFeatureValue() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int j = 0;

		String mediumFeatureValue = String.format("test%d", j++);

		featureValueToCountMap.put(mediumFeatureValue, 50D);

		String featureValue = String.format("test%d", j++);
		featureValueToCountMap.put(featureValue, 4D);
		double score = new OccurrencesHistogram(featureValueToCountMap).score(featureValueToCountMap.get(featureValue));
		Assert.assertEquals(76, score, 1);

		String featureValue1 = String.format("test%d", j++);
		featureValueToCountMap.put(featureValue1, 5D);
		OccurrencesHistogram occurrencesHistogram = new OccurrencesHistogram(featureValueToCountMap);
		score = occurrencesHistogram.score(featureValueToCountMap.get(featureValue));
		double score1 = occurrencesHistogram.score(featureValueToCountMap.get(featureValue1));
		Assert.assertEquals(score, score1, 1);
		Assert.assertEquals(63, score, 1);

		double[] scores = new double[]{45, 16};
		for (int i = 0; i < scores.length; i++) {
			double featureCount = 5D;
			featureValueToCountMap.put(String.format("rareFeature-%d", i), featureCount);
			score = new OccurrencesHistogram(featureValueToCountMap).score(featureCount);
			Assert.assertEquals(scores[i], score, 1);
		}
	}

	@Test
	public void testingScoreOfOnlyRareFeatureValues() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int i = 0;
		String featureValue = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue, 1D);
		for (int j = 0; j < 4; j++) {
			String tmp = String.format("test%d", i++);
			featureValueToCountMap.put(tmp, 2D + j);
		}

		double score = new OccurrencesHistogram(featureValueToCountMap).score(featureValueToCountMap.get(featureValue));
		Assert.assertEquals(0, score, 1);
	}

	@Test
	public void testingScoreOfVeryRareFeatureValueAgainstMediumRareFeatureValues() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		int i = 0;
		String featureValue = String.format("test%d", i++);
		featureValueToCountMap.put(featureValue, 1D);

		for (int j = 0; j < 4; j++) {
			String mediumRareFeatureValue = String.format("test%d", i++);
			featureValueToCountMap.put(mediumRareFeatureValue, 8D + j);
			double score = new OccurrencesHistogram(featureValueToCountMap).score(featureValueToCountMap.get(featureValue));
			Assert.assertEquals(99, score, 1);
		}
	}

	@Test
	public void testingScoreOfRareFeatureValueAgainstMediumRareFeatureValues() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		String rareFeatureValue = "rareFeatureValue";
		featureValueToCountMap.put(rareFeatureValue, 3D);
		double[] mediumRareFeatureCounts = new double[]{8, 9, 10};
		double[] rareScores = new double[]{52, 48, 40};
		for (int i = 0; i < rareScores.length; i++) {
			featureValueToCountMap.put(String.format("mediumRareFeatureValue-%d", i), mediumRareFeatureCounts[i]);
			double score = new OccurrencesHistogram(featureValueToCountMap).score(featureValueToCountMap.get(rareFeatureValue));
			Assert.assertEquals(rareScores[i], score, 1);
		}
	}

	@Test
	public void testingScoreOfFewMediumFeatureValueAgainstVeryLargeFeatureValue() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		featureValueToCountMap.put("veryLargeFeatureValue", 19000D);

		double mediumFeatureCount = 22;
		double[] scores = new double[]{57, 55, 48, 36, 20};
		for (int i = 0; i < scores.length; i++) {
			featureValueToCountMap.put(String.format("mediumFeatureValue-%d", i), mediumFeatureCount);
			double score = new OccurrencesHistogram(featureValueToCountMap).score(mediumFeatureCount);
			Assert.assertEquals(scores[i], score, 1);
		}
	}

	@Test
	public void testingScoreOfRareFeatureValueAgainstMediumFeatureValueAcrossTime() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();

		String largeFeatureValue = "largeFeatureValue";
		String rareFeatureValue = "rareFeatureValue";

		double[] rareFeatureValues = new double[]{2, 10};
		double[] largeFeatureValues = new double[]{20, 100};
		double[] rareFeatureScores = new double[]{90, 48};
		for (int i = 0; i < rareFeatureScores.length; i++) {
			featureValueToCountMap.put(largeFeatureValue, largeFeatureValues[i]);
			featureValueToCountMap.put(rareFeatureValue, rareFeatureValues[i]);
			double score = new OccurrencesHistogram(featureValueToCountMap).score(rareFeatureValues[i]);
			Assert.assertEquals(rareFeatureScores[i], score, 1);
		}
	}

	@Test
	public void testRareToMediumFeatureValueAgainstMediumLargeFeatureValue() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();

		double mediumLargeFeatureCount = 100;
		featureValueToCountMap.put("mediumLargeFeatureValue", mediumLargeFeatureCount);
		double score = new OccurrencesHistogram(featureValueToCountMap).score(mediumLargeFeatureCount);
		Assert.assertEquals(0, score, 1);

		double[] rareFeatureCounts = new double[]{1, 2, 3, 4, 5, 10, 20};
		double[] rareFeatureScores = new double[]{99, 93, 86, 79, 73, 48, 14};
		for (int i = 0; i < rareFeatureScores.length; i++) {
			featureValueToCountMap.put("rareFeature", rareFeatureCounts[i]);
			score = new OccurrencesHistogram(featureValueToCountMap).score(rareFeatureCounts[i]);
			Assert.assertEquals(rareFeatureScores[i], score, 1);
		}
	}
}
