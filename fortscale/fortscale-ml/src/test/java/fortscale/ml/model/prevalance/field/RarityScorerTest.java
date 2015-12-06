package fortscale.ml.model.prevalance.field;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@RunWith(JUnit4.class)
public class RarityScorerTest {
	private double calcScore(int maxRareCount, int maxNumOfRareFeatures, Map<String, Integer> featureValueToCountMap, int featureCountToScore) {
		RarityScorer rarityScorer = new RarityScorer(featureValueToCountMap.values(), maxRareCount, maxNumOfRareFeatures);
		return rarityScorer.score(featureCountToScore);
	}

	private void assertScoreRange(int maxRareCount, int maxNumOfRareFeatures, Map<String, Integer> featureValueToCountMap, int featureCount, double expectedRangeMin, double expectedRangeMax) {
		double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, featureCount);
		Assert.assertTrue(String.format("score (%e) >= expectedRangeMin (%e) does not hold", score, expectedRangeMin), score >= expectedRangeMin);
		Assert.assertTrue(String.format("score (%e) <= expectedRangeMax (%e) does not hold", score, expectedRangeMax), score <= expectedRangeMax);
	}

	private Map<String, Integer> createFeatureValueToCountWithConstantCounts(int... numOfFeaturesAndCounts) {
		Map<String, Integer> res = new HashMap<>();
		for (int i = 0; i < numOfFeaturesAndCounts.length; i += 2) {
			int numOfFeatures = numOfFeaturesAndCounts[i];
			int count = numOfFeaturesAndCounts[i + 1];
			if (count > 0) {
				while (numOfFeatures-- > 0) {
					res.put("feature-" + i + "-" + numOfFeatures, count);
				}
			}
		}
		return res;
	}

	/*************************************************************************************
	 *************************************************************************************
	 ************ TEST BASIC MODEL BEHAVIOUR WHEN MODEL PARAMETERS ARE ISOLATED **********
	 *************************************************************************************
	 *************************************************************************************/

	@Test
	public void shouldScore0ToFeatureCountsGreaterThanMaxRareCount() throws Exception {
		int maxNumOfRareFeatures = 10;
		for (int maxRareCount = 1; maxRareCount < 10; maxRareCount++) {
			for (int count = 1; count <= maxRareCount + 1; count++) {
				double rangeMin = (count == maxRareCount + 1) ? 0 : 1;
				double rangeMax = (count == maxRareCount + 1) ? 0 : 100;
				assertScoreRange(maxRareCount, maxNumOfRareFeatures, new HashMap<String, Integer>(), count, rangeMin, rangeMax);
			}
		}
	}

	@Test
	public void shouldScore100ToVeryRareFeatureNoMatterWhatIsMaxRareCount() throws Exception {
		int maxNumOfRareFeatures = 10;
		int veryRareFeatureCount = 1;
		for (int maxRareCount = 1; maxRareCount < 10; maxRareCount++) {
			Assert.assertEquals(100, calcScore(maxRareCount, maxNumOfRareFeatures, new HashMap<String, Integer>(), veryRareFeatureCount), 0.0001);
		}
	}

	@Test
	public void shouldScore100ToVeryRareFeatureEvenWhenThereAreCommonFeatures() throws Exception {
		int maxRareCount = 35;
		int maxNumOfRareFeatures = 100;
		int veryRareFeatureCount = 1;
		Assert.assertEquals(100, calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(10000, maxRareCount + 2), veryRareFeatureCount), 0.0001);
	}

	@Test
	public void shouldScore0WhenThereAreMoreThanMaxNumOfRareFeaturesRareFeatures() throws Exception {
		int maxRareCount = 100;
		int count = 1;
		for (int maxNumOfRareFeatures = 1; maxNumOfRareFeatures < 10; maxNumOfRareFeatures++) {
			for (int numOfFeatures = 0; numOfFeatures <= maxNumOfRareFeatures; numOfFeatures++) {
				double rangeMin = (numOfFeatures == maxNumOfRareFeatures) ? 0 : 1;
				double rangeMax = (numOfFeatures == maxNumOfRareFeatures) ? 0 : 100;
				assertScoreRange(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(numOfFeatures, count, 1, 10000), count, rangeMin, rangeMax);
			}
		}
	}

	@Test
	public void shouldScore100ToVeryRareFeatureAndEmptyBuildDataNoMatterWhatIsMaxNumOfRareFeatures() throws Exception {
		int maxRareCount = 10;
		int veryRareFeatureCount = 1;
		for (int maxNumOfRareFeatures = 1; maxNumOfRareFeatures < 10; maxNumOfRareFeatures++) {
			Assert.assertEquals(100, calcScore(maxRareCount, maxNumOfRareFeatures, new HashMap<String, Integer>(), veryRareFeatureCount), 0.0001);
		}
	}

	private double[][][] calcScoresOverConfigurationMatrix(int maxMaxRareCount, int maxMaxNumOfRareFeatures, int maxFeatureCountToScore) {
		return calcScoresOverConfigurationMatrix(new HashMap<String, Integer>(), maxMaxRareCount, maxMaxNumOfRareFeatures, maxFeatureCountToScore);
	}

	private double[][][] calcScoresOverConfigurationMatrix(Map<String, Integer> featureValueToCountMap, int maxMaxRareCount, int maxMaxNumOfRareFeatures, int maxFeatureCountToScore) {
		double[][][] scores = new double[maxMaxRareCount][][];
		for (int maxRareCount = 1; maxRareCount <= maxMaxRareCount; maxRareCount++) {
			scores[maxRareCount - 1] = new double[maxMaxNumOfRareFeatures][];
			for (int maxNumOfRareFeatures = 1; maxNumOfRareFeatures <= maxMaxNumOfRareFeatures; maxNumOfRareFeatures++) {
				scores[maxRareCount - 1][maxNumOfRareFeatures - 1] = new double[maxFeatureCountToScore];
				for (int featureCountToScore = 1; featureCountToScore <= maxFeatureCountToScore; featureCountToScore++) {
					scores[maxRareCount - 1][maxNumOfRareFeatures - 1][featureCountToScore - 1] = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, featureCountToScore);
				}
			}
		}
		return scores;
	}

	private enum PARAMETER {
		MAX_RARE_COUNT,
		MAX_NUM_OF_RARE_FEATURES,
		FEATURE_COUNT
	}

	private void assertMonotonicity(List<List<Double>> scoresSeries, @Nullable Boolean shouldIncrease) {
		boolean hasStrongMonotonicity = false;
		for (List<Double> scores : scoresSeries) {
			if (scores.isEmpty()) {
				continue;
			}
			for (int i = 1; i < scores.size(); i++) {
				double scoresDelta = scores.get(i) - scores.get(i - 1);
				if (shouldIncrease == null) {
					Assert.assertTrue(scoresDelta == 0);
				} else {
					Assert.assertTrue(scoresDelta * (shouldIncrease ? 1 : -1) >= 0);
				}
			}
			hasStrongMonotonicity = hasStrongMonotonicity || scores.get(scores.size() - 1) != scores.get(0);
		}
		if (shouldIncrease != null) {
			// it's ok that some series are constant, but if all of them are - the model probably has a bug
			Assert.assertTrue(hasStrongMonotonicity);
		}
	}

	private void assertMonotonicity(@Nonnull double[][][] scores, PARAMETER overParameter, @Nullable Boolean shouldIncrease) {
		List<List<Double>> scoresSeries = new ArrayList<>();
		if (overParameter == PARAMETER.MAX_RARE_COUNT) {
			for (int maxNumOfRareFeaturesInd = 0; maxNumOfRareFeaturesInd < scores[0].length; maxNumOfRareFeaturesInd++) {
				for (int featureCountInd = 0; featureCountInd < scores[0][0].length; featureCountInd++) {
					scoresSeries.add(new ArrayList<Double>());
					for (int maxRareCountInd = 0; maxRareCountInd < scores.length; maxRareCountInd++) {
						scoresSeries.get(scoresSeries.size() - 1).add(scores[maxRareCountInd][maxNumOfRareFeaturesInd][featureCountInd]);
					}
				}
			}
		} else if (overParameter == PARAMETER.MAX_NUM_OF_RARE_FEATURES) {
			for (int maxRareCountInd = 0; maxRareCountInd < scores.length; maxRareCountInd++) {
				for (int featureCountInd = 0; featureCountInd < scores[0][0].length; featureCountInd++) {
					scoresSeries.add(new ArrayList<Double>());
					for (int maxNumOfRareFeaturesInd = 0; maxNumOfRareFeaturesInd < scores[0].length; maxNumOfRareFeaturesInd++) {
						scoresSeries.get(scoresSeries.size() - 1).add(scores[maxRareCountInd][maxNumOfRareFeaturesInd][featureCountInd]);
					}
				}
			}
		} else {
			for (int maxRareCountInd = 0; maxRareCountInd < scores.length; maxRareCountInd++) {
				for (int maxNumOfRareFeaturesInd = 0; maxNumOfRareFeaturesInd < scores[0].length; maxNumOfRareFeaturesInd++) {
					scoresSeries.add(new ArrayList<Double>());
					for (int featureCountInd = 0; featureCountInd < scores[0][0].length; featureCountInd++) {
						scoresSeries.get(scoresSeries.size() - 1).add(scores[maxRareCountInd][maxNumOfRareFeaturesInd][featureCountInd]);
					}
				}
			}
		}
		assertMonotonicity(scoresSeries, shouldIncrease);
	}

	@Test
	public void shouldScoreDecreasinglyWhenFeatureCountIncreases() {
		assertMonotonicity(calcScoresOverConfigurationMatrix(10, 100, 10), PARAMETER.FEATURE_COUNT, false);
	}

	@Test
	public void shouldScoreIncreasinglyWhenMaxNumOfRareFeaturesIncreases() {
		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		String veryRareFeature = "veryRare";
		featureValueToCountMap.put(veryRareFeature, 1);
		assertMonotonicity(calcScoresOverConfigurationMatrix(featureValueToCountMap, 10, 100, 10), PARAMETER.MAX_NUM_OF_RARE_FEATURES, true);
	}

	@Test
	public void shouldScoreConstantlyWhenMaxNumOfRareFeaturesIncreasesButModelDataIsEmpty() {
		assertMonotonicity(calcScoresOverConfigurationMatrix(10, 100, 10), PARAMETER.MAX_NUM_OF_RARE_FEATURES, null);
	}



	/*************************************************************************************
	 *************************************************************************************
	 *********** GRAPHS SHOWING HOW MODEL BEHAVES WHEN PARAMETERS ARE ISOLATED ***********
	 ************************** JUST PUT "PRINT_GRAPHS = true" ***************************
	 *************************************************************************************
	 *************************************************************************************/

	private static final boolean PRINT_GRAPHS = false;

	private boolean printingOffOverride = false;

	private void turnOffPrinting() {
		printingOffOverride = true;
	}

	private void revertPrinting() {
		printingOffOverride = false;
	}

	private void print(String msg) {
		if (PRINT_GRAPHS && !printingOffOverride) {
			System.out.print(msg);
		}
	}

	private void println(String msg) {
		print(msg + "\n");
	}

	private void println() {
		println("");
	}

	private void printNewLineOrHeader(boolean printedHeader, String googleSheetName, int fromCount, int toCount) {
		int counts[] = new int[toCount - fromCount + 1];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = fromCount + i;
		}
		printNewLineOrHeader(printedHeader, googleSheetName, counts);
	}

	private void printNewLineOrHeader(boolean printedHeader, String googleSheetName, int counts[]) {
		if (printedHeader) {
			println();
		} else {
			String featureCountsStr = "featureCount";
			for (int count : counts) {
				featureCountsStr += "\t" + count;
			}
			println("\n\n\nCopy the following output into \"" + googleSheetName + "\" sheet in the following URL: https://docs.google.com/spreadsheets/d/1eNqu2K3mIUCH3b-NXeQM5VqBkcaEqwcSxiNWZ-FzdHg/edit#gid=1047563136&vpid=A1\n");
			println(featureCountsStr);
		}
	}

	@Test
	public void shouldScoreIncreasinglyWhenMaxRareCountIncreases() {
		int maxMaxRareCount = 10;
		int maxFeatureCountToScore = maxMaxRareCount + 1;
		double[][][] scores = calcScoresOverConfigurationMatrix(maxMaxRareCount, 100, maxFeatureCountToScore);

		assertMonotonicity(scores, PARAMETER.MAX_RARE_COUNT, true);

		int maxNumOfRareFeaturesToPrint = 10;
		boolean printedHeader = false;
		for (int maxRareCount = 0; maxRareCount < scores.length; maxRareCount++) {
			printNewLineOrHeader(printedHeader, "maxRareCountEffect", 1, scores[0][0].length);
			printedHeader = true;
			print(maxRareCount + "\t");
			for (int featureCount = 0; featureCount < scores[0][0].length; featureCount++) {
				print(scores[maxRareCount][maxNumOfRareFeaturesToPrint - 1][featureCount] + "\t");
			}
		}
	}

	@Test
	public void shouldScoreDecreasinglyWhenNumberOfRareFeaturesWithSameCountIncreases() {
		int maxRareCountToPrint = 15;
		int maxNumOfRareFeaturess[] = new int[]{5, 7, 9, 11, 13, 15};
		int counts[] = new int[]{1,4};

		boolean printedHeader = false;
		List<List<Double>> scoresSeries = new ArrayList<>();
		for (int maxRareCount = 1; maxRareCount < 20; maxRareCount++) {
			for (int count : counts) {
				for (int maxNumOfRareFeatures : maxNumOfRareFeaturess) {
					int maxNumOfFeatures = maxRareCount + 1;
					if (maxRareCount == maxRareCountToPrint) {
						revertPrinting();
						printNewLineOrHeader(printedHeader, "maxNumOfRareFeaturesEffect1", 0, maxNumOfFeatures - 1);
						printedHeader = true;
					} else {
						turnOffPrinting();
					}
					print(count + "->" + maxNumOfRareFeatures + "\t");
					List<Double> scores = new ArrayList<>(maxNumOfFeatures + 1);
					for (int numOfFeatures = 0; numOfFeatures <= maxNumOfFeatures; numOfFeatures++) {
						double score = calcScore(
								maxRareCount,
								maxNumOfRareFeatures,
								createFeatureValueToCountWithConstantCounts(numOfFeatures, count, 1, 10000),
								count);
						scores.add(score);
						print(score + "\t");
					}
					scoresSeries.add(scores);
				}
			}
		}
		assertMonotonicity(scoresSeries, false);
	}

	@Test
	public void shouldScoreDecreasinglyWhenThereAreManyFeaturesWithTheSameCountAndThenTheirCountIncreasesByOne() {
		int maxNumOfFeaturesToPrint = 2;
		int maxRareCountToPrint = 15;
		int maxNumOfRareFeaturessToPrint[] = new int[]{5, 10, 15, 20, 30, 40, 50};

		List<List<Double>> scoresSeries = new ArrayList<>();
		boolean printedHeader = false;
		for (int maxRareCount = 1; maxRareCount < 30; maxRareCount++) {
			for (int numOfFeatures = 0; numOfFeatures < 10; numOfFeatures++) {
				for (int maxNumOfRareFeatures = 5; maxNumOfRareFeatures < 100; maxNumOfRareFeatures += 5) {
					if (numOfFeatures <= maxNumOfFeaturesToPrint && maxRareCount == maxRareCountToPrint && ArrayUtils.contains(maxNumOfRareFeaturessToPrint, maxNumOfRareFeatures)) {
						revertPrinting();
						printNewLineOrHeader(printedHeader, "maxNumOfRareFeaturesEffect2", 1, maxRareCount);
						printedHeader = true;
					} else {
						turnOffPrinting();
					}
					print(numOfFeatures + "->" + maxNumOfRareFeatures + "\t");
					List<Double> scores = new ArrayList<>(maxRareCount - 1);
					for (int featureCount = 1; featureCount <= maxRareCount; featureCount++) {
						double score = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(numOfFeatures, featureCount, 1, 10000), featureCount);
						if (featureCount > 1) {
							scores.add(score);
						}
						print(score + "\t");
					}
					scoresSeries.add(scores);
				}
			}
		}
		assertMonotonicity(scoresSeries, false);
	}

	@Test
	public void shouldScoreIncreasinglyWhenLessRareFeatureComparedToVeryRareFeatureBecomesEvenLessRare() {
		int maxNumOfRareFeatures = 1;
		int maxMaxRareCount = 10;
		int maxFeatureCount = maxMaxRareCount + 1;

		List<List<Double>> scoresSeries = new ArrayList<>(maxFeatureCount + 1);
		boolean printedHeader = false;
		for (int maxRareCount = 1; maxRareCount <= maxMaxRareCount; maxRareCount++) {
			printNewLineOrHeader(printedHeader, "lessRareFeatureEffect", 0, maxFeatureCount);
			printedHeader = true;
			print(maxRareCount + "\t");
			List<Double> scores = new ArrayList<>(maxFeatureCount + 1);
			for (int featureCount = 0; featureCount <= maxFeatureCount; featureCount++) {
				double score = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(1, featureCount), 1);
				if (featureCount > 0) {
					scores.add(score);
				}
				print(score + "\t");
			}
			scoresSeries.add(scores);
		}
		assertMonotonicity(scoresSeries, true);
	}



	/*************************************************************************************
	 *************************************************************************************
	 ****************** TEST VARIOUS SCENARIOS - FROM BASIC TO ADVANCED ******************
	 ***** (BUT NOT AS BASIC AS THE TESTS WHICH TRY TO ISOLATE THE MODEL PARAMETERS ******
	 *************************************************************************************
	 *************************************************************************************/

	@Test
	public void shouldScoreVeryRareFeatureTheSameWhenBuildingWithVeryCommonValuesAndWithoutThem() {
		int maxRareCount = 10;
		int maxNumOfRareFeatures = 30;
		int veryRareFeatureCount = 1;
		int veryCommonFeatureCount = 10000;
		double scoreWithCommon = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(10, veryCommonFeatureCount), veryRareFeatureCount);
		double scoreWithoutCommon = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(0, 0), veryRareFeatureCount);
		Assert.assertEquals(scoreWithoutCommon, scoreWithCommon, 1);
		Assert.assertTrue(scoreWithoutCommon >= 99);
	}

	@Test
	public void elementarycheckForOccurrencesHistogram() throws Exception {
		int maxRareCount = 15;
		int maxNumOfRareFeatures = 5;

		int count = 100;
		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		for (int i = 0; i < 100; i++) {
			featureValueToCountMap.put(String.format("test%d", i), count);
		}
		double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, count);
		Assert.assertEquals(0.0, score, 0.0);
	}

	@Test
	public void simpleInputOutputForOccurrencesHistogram() throws Exception {
		int maxRareCount = 6;
		int maxNumOfRareFeatures = 5;

		Random rnd = new Random(1);
		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		for (int i = 0; i < 2; i++) {
			int val = (int) Math.min( 100.0, rnd.nextDouble( ) * 100 + 8);
			featureValueToCountMap.put(String.format("test%d", i), val);
		}

		int[] counts = new int[]{1, 2, 3, 4};
//		double[] scores = new double[]{99, 93, 61, 18};
		double[] scores = new double[]{100, 94, 50, 15};
		for (int i = 0; i < scores.length; i++) {
			double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, counts[i]);
			Assert.assertEquals(scores[i], score, 0);
		}
	}

	@Test
	public void testingScoreOfVeryRareFeatureValueAgainstVeryLargeFeatureValueWithValuesIncreasingByTime() throws Exception {
		int maxRareCount = 35;
		int maxNumOfRareFeatures = 6;

		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		String rareFeature = "rareFeature";
		int[] rareCounts = new int[]{1, 2, 3, 4, 8, 16, 32};
//		int[] commonCounts = new int[]{5000, 10000, 15000, 20000, 40000, 80000, 160000};
		int[] commonCounts = new int[]{50, 100, 150, 200, 400, 800, 1600};
//		double[] scores = new double[]{99, 96, 93, 90, 81, 70, 56};
		double[] scores = new double[]{94, 94, 94, 93, 84, 24, 1};
		for (int i = 0; i < scores.length; i++) {
			featureValueToCountMap.put("commonFeature", commonCounts[i]);
			featureValueToCountMap.put(rareFeature, rareCounts[i]);
			double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareCounts[i]);
			Assert.assertEquals(scores[i], score, 0);
		}
	}

	@Test
	public void testingScoreOfVeryRareFeatureValuesAgainstVeryLargeFeatureValue() throws Exception {
		int maxRareCount = 15;
		int maxNumOfRareFeatures = 8;

		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		featureValueToCountMap.put("commonFeature", 20000);
		int rareFeatureCountA = 1;
		featureValueToCountMap.put("rareFeatureA", rareFeatureCountA);
		double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountA);
//		Assert.assertEquals(99, score, 1);
		Assert.assertEquals(97, score, 1);

		int rareFeatureCountB = 2;
		featureValueToCountMap.put("rareFeatureB", 2);
		double scoreA = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountA);
		double scoreB = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountB);
		Assert.assertEquals(scoreA, scoreB, 1);
//		Assert.assertEquals(94, scoreA, 1);
		Assert.assertEquals(91, scoreA, 1);

		int[] counts = new int[]{2, 2, 1, 1, 1};
//		double[] scores = new double[]{87, 76, 59, 38};
		double[] scores = new double[]{82, 71, 57, 40};
		for (int i = 0; i < scores.length; i++) {
			featureValueToCountMap.put(String.format("rareFeature-%d", i), counts[i]);
			score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, counts[i]);
			Assert.assertEquals(scores[i], score, 1);
		}
	}

	@Test
	public void testingScoreOfOneVeryRareFeatureValueAndManyRareFeatureValuesAgainstVeryLargeFeatureValue() throws Exception {
		int maxRareCount = 15;
		int maxNumOfRareFeatures = 8;

		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		featureValueToCountMap.put("veryCommonFeatureValue", 20000);
		int veryRareFeatureCount = 1;
		featureValueToCountMap.put("veryRareFeatureValue", veryRareFeatureCount);
		double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, 1);
//		Assert.assertEquals(99, score, 1);
		Assert.assertEquals(96, score, 1);

		int[] rareFeatureCounts = new int[]{5, 6, 4, 5, 6, 4};
//		double[] rareFeaturesScores = new double[]{85, 75, 64, 47, 27, 1};
		double[] rareFeaturesScores = new double[]{64, 41, 63, 40, 20, 19};
//		double[] veryRareFeaturesScores = new double[]{94, 90, 85, 78, 70, 60};
		double[] veryRareFeaturesScores = new double[]{92, 90, 81, 73, 66, 53};
		for (int i = 0; i < rareFeatureCounts.length; i++) {
			String rareFeature = String.format("rareFeatureValue-%d", i);
			featureValueToCountMap.put(rareFeature, rareFeatureCounts[i]);
			score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, veryRareFeatureCount);
			Assert.assertEquals(veryRareFeaturesScores[i], score, 1);
			score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCounts[i]);
			Assert.assertEquals(rareFeaturesScores[i], score, 1);
		}
	}

	@Test
	public void testingScoreOfRareFeatureValuesAgainstMediumFeatureValue() throws Exception {
		int maxRareCount = 25;
		int maxNumOfRareFeatures = 5;

		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		featureValueToCountMap.put("mediumFeatureValue", 50);

		int rareFeatureCount1 = 4;
		featureValueToCountMap.put("rareFeatureValue-1", rareFeatureCount1);
		double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCount1);
//		Assert.assertEquals(76, score, 1);
		Assert.assertEquals(86, score, 1);

		int rareFeatureCount2 = 5;
		featureValueToCountMap.put("rareFeatureValue-2", rareFeatureCount2);
		score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCount1);
		double score2 = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCount2);
//		Assert.assertEquals(score, score2, 1);
		Assert.assertEquals(score, score2, 2);
//		Assert.assertEquals(63, score, 1);
		Assert.assertEquals(67, score, 1);

//		double[] scores = new double[]{45, 16};
		double[] scores = new double[]{45, 23};
		for (int i = 0; i < scores.length; i++) {
			int rareFeatureCount3 = 5;
			featureValueToCountMap.put("newRareFeatureValue-" + i, rareFeatureCount3);
			score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCount3);
			Assert.assertEquals(scores[i], score, 1);
		}
	}

	@Test
	public void testingScoreOfOnlyRareFeatureValues() throws Exception {
		int maxRareCount = 25;
		int maxNumOfRareFeatures = 5;

		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		int rareFeatureCount = 1;
		featureValueToCountMap.put(String.format("rareFeatureValue"), rareFeatureCount);
		for (int i = 0; i < 4; i++) {
			featureValueToCountMap.put("newRareFeatureValue-" + i, 2 + i);
		}

		double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCount);
		Assert.assertEquals(0, score, 1);
	}

	@Test
	public void testingScoreOfFewMediumFeatureValueAgainstVeryLargeFeatureValue() throws Exception {
		int maxRareCount = 70;
		int maxNumOfRareFeatures = 7;

		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		featureValueToCountMap.put("veryLargeFeatureValue", 19000);

		int mediumFeatureCount = 22;
//		double[] scores = new double[]{57, 55, 48, 36, 20};
		double[] scores = new double[]{59, 55, 47, 38, 27};
		for (int i = 0; i < scores.length; i++) {
			featureValueToCountMap.put("mediumFeatureValue-" + i, mediumFeatureCount);
			double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, mediumFeatureCount);
			Assert.assertEquals(scores[i], score, 1);
		}
	}

	@Test
	public void testingScoreOfRareFeatureValueAgainstMediumFeatureValueAcrossTime() throws Exception {
		int maxRareCount = 30;
		int maxNumOfRareFeatures = 10;

		Map<String, Integer> featureValueToCountMap = new HashMap<>();

		int[] rareFeatureValues = new int[]{2, 10};
		int[] largeFeatureValues = new int[]{20, 100};
//		double[] rareFeatureScores = new double[]{90, 48};
		double[] rareFeatureScores = new double[]{83, 54};
		for (int i = 0; i < rareFeatureScores.length; i++) {
			featureValueToCountMap.put("largeFeatureValue", largeFeatureValues[i]);
			featureValueToCountMap.put("rareFeatureValue", rareFeatureValues[i]);
			double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureValues[i]);
			Assert.assertEquals(rareFeatureScores[i], score, 1);
		}
	}

	@Test
	public void testRareToMediumFeatureValueAgainstMediumLargeFeatureValue() throws Exception {
		int maxRareCount = 30;
		int maxNumOfRareFeatures = 5;

		Map<String, Integer> featureValueToCountMap = new HashMap<>();

		int mediumLargeFeatureCount = 100;
		featureValueToCountMap.put("mediumLargeFeatureValue", mediumLargeFeatureCount);
		double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, mediumLargeFeatureCount);
		Assert.assertEquals(0, score, 1);

		int[] rareFeatureCounts = new int[]{1, 2, 3, 4, 5, 10, 20};
//		double[] rareFeatureScores = new double[]{99, 93, 86, 79, 73, 48, 14};
		double[] rareFeatureScores = new double[]{93, 93, 91, 90, 88, 52, 5};
		for (int i = 0; i < rareFeatureScores.length; i++) {
			featureValueToCountMap.put("rareFeature", rareFeatureCounts[i]);
			score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCounts[i]);
			Assert.assertEquals(rareFeatureScores[i], score, 1);
		}
	}
}
