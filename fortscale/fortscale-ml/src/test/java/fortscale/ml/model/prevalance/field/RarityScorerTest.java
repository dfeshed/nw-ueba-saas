package fortscale.ml.model.prevalance.field;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RunWith(JUnit4.class)
public class RarityScorerTest {
	private double calcScore(int maxRareCount, int maxNumOfRareFeatures, Map<String, Integer> featureValueToCountMap, int featureCountToScore) {
		RarityScorer hist = new RarityScorer(featureValueToCountMap.values(), maxRareCount, maxNumOfRareFeatures);
		return hist.score(featureCountToScore);
	}

	private void assertScoreRange(int maxRareCount, int maxNumOfRareFeatures, Map<String, Integer> featureValueToCountMap, int featureCount, double expectedRangeMin, double expectedRangeMax) {
		double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, featureCount);
		Assert.assertTrue(String.format("score (%e) >= expectedRangeMin (%e) does not hold", score, expectedRangeMin), score >= expectedRangeMin);
		Assert.assertTrue(String.format("score (%e) <= expectedRangeMax (%e) does not hold", score, expectedRangeMax), score <= expectedRangeMax);
	}

	private Map<String, Integer> createFeatureValueToCountWithConstantCount(int numOfFeatures, int count) {
		Map<String, Integer> res = new HashMap<>(numOfFeatures);
		if (count > 0) {
			while (numOfFeatures-- > 0) {
				res.put("feature-" + numOfFeatures, count);
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
	public void shouldScore0WhenThereAreMoreThanMaxNumOfRareFeaturesRareFeatures() throws Exception {
		int maxRareCount = 100;
		int count = 1;
		for (int maxNumOfRareFeatures = 1; maxNumOfRareFeatures < 10; maxNumOfRareFeatures++) {
			for (int numOfFeatures = 0; numOfFeatures <= maxNumOfRareFeatures; numOfFeatures++) {
				double rangeMin = (numOfFeatures == maxNumOfRareFeatures) ? 0 : 1;
				double rangeMax = (numOfFeatures == maxNumOfRareFeatures) ? 0 : 100;
				assertScoreRange(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCount(numOfFeatures, count), count, rangeMin, rangeMax);
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

	private void assertMonotonicity(@Nonnull double[][][] scores, PARAMETER overParameter, @Nullable Boolean shouldIncrease) {
		boolean hasStrongMonotonicity = false;
		for (int maxRareCountInd = 0; maxRareCountInd < scores.length; maxRareCountInd++) {
			for (int maxNumOfRareFeaturesInd = 0; maxNumOfRareFeaturesInd < scores[0].length; maxNumOfRareFeaturesInd++) {
				for (int featureCountInd = 0; featureCountInd < scores[0][0].length; featureCountInd++) {
					double scoresDelta;
					if (overParameter == PARAMETER.MAX_RARE_COUNT && maxRareCountInd == 0 ||
							overParameter == PARAMETER.MAX_NUM_OF_RARE_FEATURES && maxNumOfRareFeaturesInd == 0 ||
							overParameter == PARAMETER.FEATURE_COUNT && featureCountInd == 0) {
						scoresDelta = 0;
					} else if (overParameter == PARAMETER.MAX_RARE_COUNT) {
						scoresDelta = scores[maxRareCountInd][maxNumOfRareFeaturesInd][featureCountInd] - scores[maxRareCountInd - 1][maxNumOfRareFeaturesInd][featureCountInd];
					} else if (overParameter == PARAMETER.MAX_NUM_OF_RARE_FEATURES) {
						scoresDelta = scores[maxRareCountInd][maxNumOfRareFeaturesInd][featureCountInd] - scores[maxRareCountInd][maxNumOfRareFeaturesInd - 1][featureCountInd];
					} else {
						scoresDelta = scores[maxRareCountInd][maxNumOfRareFeaturesInd][featureCountInd] - scores[maxRareCountInd][maxNumOfRareFeaturesInd][featureCountInd - 1];
					}
					if (shouldIncrease == null) {
						Assert.assertTrue(scoresDelta == 0);
					} else {
						Assert.assertTrue(shouldIncrease && scoresDelta >= 0 || !shouldIncrease && scoresDelta <= 0);
					}

					double firstValueInMonotonicSeries = scores
							[overParameter == PARAMETER.MAX_RARE_COUNT ? 0 : maxRareCountInd]
							[overParameter == PARAMETER.MAX_NUM_OF_RARE_FEATURES ? 0 : maxNumOfRareFeaturesInd]
							[overParameter == PARAMETER.FEATURE_COUNT ? 0 : featureCountInd];
					double lastValueInMonotonicSeries = scores
							[overParameter == PARAMETER.MAX_RARE_COUNT ? scores.length - 1 : maxRareCountInd]
							[overParameter == PARAMETER.MAX_NUM_OF_RARE_FEATURES ? scores[0].length - 1 : maxNumOfRareFeaturesInd]
							[overParameter == PARAMETER.FEATURE_COUNT ? scores[0][0].length - 1 : featureCountInd];
					hasStrongMonotonicity = hasStrongMonotonicity || lastValueInMonotonicSeries - firstValueInMonotonicSeries != 0;
				}
			}
		}
		if (shouldIncrease != null) {
			// it's ok that some series are constant, but if all of them are - the model probably has a bug
			Assert.assertTrue(hasStrongMonotonicity);
		}
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
					double lastScore = 100;
					for (int numOfFeatures = 0; numOfFeatures <= maxNumOfFeatures; numOfFeatures++) {
						double score = calcScore(
								maxRareCount,
								maxNumOfRareFeatures,
								createFeatureValueToCountWithConstantCount(numOfFeatures, count),
								count);
						Assert.assertTrue(score <= lastScore);
						lastScore = score;
						print(score + "\t");
					}
				}
			}
		}
	}

	@Test
	public void shouldScoreLessWhenThereAreManyFeaturesWithTheSameCountAndThenTheirCountIncreasesByOne() {
		int maxNumOfFeaturesToPrint = 2;
		int maxRareCountToPrint = 15;
		int maxNumOfRareFeaturessToPrint[] = new int[]{5, 10, 15, 20, 30, 40, 50};

		boolean printedHeader = false;
		for (int maxRareCount = 1; maxRareCount < 30; maxRareCount++) {
			for (int numOfFeatures = 1; numOfFeatures < 10; numOfFeatures++) {
				for (int maxNumOfRareFeatures = 5; maxNumOfRareFeatures < 100; maxNumOfRareFeatures += 5) {
					if (numOfFeatures <= maxNumOfFeaturesToPrint && maxRareCount == maxRareCountToPrint && ArrayUtils.contains(maxNumOfRareFeaturessToPrint, maxNumOfRareFeatures)) {
						revertPrinting();
						printNewLineOrHeader(printedHeader, "maxNumOfRareFeaturesEffect2", 1, maxRareCount);
						printedHeader = true;
					} else {
						turnOffPrinting();
					}
					print(numOfFeatures + "->" + maxNumOfRareFeatures + "\t");
					double lastScore = 100;
					for (int featureCount = 1; featureCount <= maxRareCount; featureCount++) {
						double score = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCount(numOfFeatures, featureCount), featureCount);
						if (featureCount > 1) {
							String msg = String.format("score isn't decreasing in the following configuration:\nmaxRareCount = %d\nmaxNumOfRareFeatures = %d\nfeatureCount = %d -> %d\nnumOfFeatures = %d", maxRareCount, maxNumOfRareFeatures, featureCount - 1, featureCount, numOfFeatures);
							Assert.assertTrue(msg, score <= lastScore);
						}
						lastScore = score;
						print(score + "\t");
					}
				}
			}
		}
	}

	@Test
	public void shouldScoreIncreasinglyWhenLessRareFeatureComparedToVeryRareFeatureBecomesEvenLessRare() {
		int maxNumOfRareFeatures = 1;
		int maxMaxRareCount = 10;
		int maxFeatureCount = maxMaxRareCount + 1;

		boolean printedHeader = false;
		for (int maxRareCount = 1; maxRareCount <= maxMaxRareCount; maxRareCount++) {
			printNewLineOrHeader(printedHeader, "lessRareFeatureEffect", 0, maxFeatureCount);
			printedHeader = true;
			print(maxRareCount + "\t");
			double lastScore = 0;
			for (int featureCount = 0; featureCount <= maxFeatureCount; featureCount++) {
				double score = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCount(1, featureCount), 1);
				if (featureCount > 1) {
					Assert.assertTrue(score >= lastScore);
				}
				lastScore = score;
				print(score + "\t");
			}
		}
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
		double scoreWithCommon = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCount(10, veryCommonFeatureCount), veryRareFeatureCount);
		double scoreWithoutCommon = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCount(0, 0), veryRareFeatureCount);
		Assert.assertEquals(scoreWithoutCommon, scoreWithCommon, 1);
		Assert.assertTrue(scoreWithoutCommon >= 99);
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

//	@Test
//	public void shouldScoreWithFixedRatioGivenModelsBuiltWithDifferentData() throws Exception {
//		int maxRareCount = 6;
//		double maxNumOfRareFeatures = 10;
//		Map<String, Double> featureValueToCountMap1 = new HashMap<>();
//		Map<String, Double> featureValueToCountMap2 = new HashMap<>();
//		for (double i = 1; i <= maxRareCount; i++) {
//			featureValueToCountMap1.put("feature-" + i, i);
//			featureValueToCountMap2.put("feature-" + i, i * 2);
//		}
//		double[] ratios = new double[maxRareCount];
//		for (int count = 1; count <= maxRareCount; count++) {
//			double score1 = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap1, count);
//			double score2 = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap2, count);
//			ratios[count - 1] = score2 / score1;
//		}
//		for (int i = 1; i < ratios.length; i++) {
//			System.out.println(ratios[i]);
//			Assert.assertEquals(ratios[i], ratios[i - 1], 0.01);
//		}
//	}
}
