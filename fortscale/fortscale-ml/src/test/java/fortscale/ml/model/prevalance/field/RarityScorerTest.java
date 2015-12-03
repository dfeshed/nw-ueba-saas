package fortscale.ml.model.prevalance.field;

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
	private double calcScore(int maxRareCount, int maxNumOfRareFeatures, Map<String, Integer> featureValueToCountMap, int featureCount) {
		RarityScorer hist = new RarityScorer(featureValueToCountMap.values(), maxRareCount, maxNumOfRareFeatures);
		return hist.score(featureCount);
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
			assertScoreRange(maxRareCount, maxNumOfRareFeatures, new HashMap<String, Integer>(), veryRareFeatureCount, 99, 100);
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

	private double[][][] calcScoresOverConfigurationMatrix(int maxMaxNumOfRareFeatures, int maxMaxRareCount, int maxFeatureCount) {
		return calcScoresOverConfigurationMatrix(new HashMap<String, Integer>(), maxMaxNumOfRareFeatures, maxMaxRareCount, maxFeatureCount);
	}

	private double[][][] calcScoresOverConfigurationMatrix(Map<String, Integer> featureValueToCountMap, int maxMaxNumOfRareFeatures, int maxMaxRareCount, int maxFeatureCount) {
		double[][][] scores = new double[maxMaxRareCount][][];
		for (int maxRareCount = 1; maxRareCount <= maxMaxRareCount; maxRareCount++) {
			scores[maxRareCount - 1] = new double[maxMaxNumOfRareFeatures][];
			for (int maxNumOfRareFeatures = 1; maxNumOfRareFeatures <= maxMaxNumOfRareFeatures; maxNumOfRareFeatures++) {
				scores[maxRareCount - 1][maxNumOfRareFeatures - 1] = new double[maxFeatureCount];
				for (int featureCount = 1; featureCount <= maxFeatureCount; featureCount++) {
					scores[maxRareCount - 1][maxNumOfRareFeatures - 1][featureCount - 1] = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, featureCount);
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
		assertMonotonicity(calcScoresOverConfigurationMatrix(100, 10, 10), PARAMETER.FEATURE_COUNT, false);
	}

	@Test
	public void shouldScoreIncreasinglyWhenMaxRareCountIncreases() {
		assertMonotonicity(calcScoresOverConfigurationMatrix(100, 10, 10), PARAMETER.MAX_RARE_COUNT, true);
	}

	@Test
	public void shouldScoreIncreasinglyWhenMaxNumOfRareFeaturesIncreases() {
		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		String veryRareFeature = "veryRare";
		featureValueToCountMap.put(veryRareFeature, 1);
		assertMonotonicity(calcScoresOverConfigurationMatrix(featureValueToCountMap, 100, 10, 10), PARAMETER.MAX_NUM_OF_RARE_FEATURES, true);
	}

	@Test
	public void shouldScoreConstantlyWhenMaxNumOfRareFeaturesIncreasesButModelDataIsEmpty() {
		assertMonotonicity(calcScoresOverConfigurationMatrix(100, 10, 10), PARAMETER.MAX_NUM_OF_RARE_FEATURES, null);
	}

	@Test
	public void shouldScoreLessWhenThereAreManyFeaturesWithTheSameCountAndThenTheirCountIncreasesByOne() {
		for (int maxRareCount = 1; maxRareCount < 30; maxRareCount++) {
			for (int maxNumOfRareFeatures = 1; maxNumOfRareFeatures < 100; maxNumOfRareFeatures += 5) {
				for (int featureCount = 1; featureCount <= maxRareCount; featureCount++) {
					for (int numOfFeatures = 1; numOfFeatures < 10; numOfFeatures++) {
						double scoreBeforeCountIncreases = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCount(numOfFeatures, featureCount), featureCount);
						double scoreAfterCountIncreases = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCount(numOfFeatures, featureCount + 1), featureCount + 1);
						String msg = String.format("score isn't decreasing in the following configuration:\nmaxRareCount = %d\nmaxNumOfRareFeatures = %d\nfeatureCount = %d -> %d\nnumOfFeatures = %d", maxRareCount, maxNumOfRareFeatures, featureCount, featureCount + 1, numOfFeatures);
						Assert.assertTrue(msg, scoreAfterCountIncreases <= scoreBeforeCountIncreases);
					}
				}
			}
		}
	}



	/*************************************************************************************
	 *************************************************************************************
	 *********** GRAPHS SHOWING HOW MODEL BEHAVES WHEN PARAMETERS ARE ISOLATED ***********
	 ****** JUST UNCOMMENT THE "@Test" LINE IN ORDER TO RUN A GRAPH GENERATING TEST ******
	 ************** THEN, COPY THE RESULTS INTO THE CORRESPONDING GOOGLE SHEET ***********
	 ********************** (WITH THE NAME MATCHING THE TEST'S NAME) *********************
	 * https://docs.google.com/spreadsheets/d/1eNqu2K3mIUCH3b-NXeQM5VqBkcaEqwcSxiNWZ-FzdHg/edit#gid=513658611&vpid=A1 *
	 *************************************************************************************
	 *************************************************************************************/

	private double[][][] calcScoresOverConfigurationMatrix(int maxRareCount, int[] maxNumOfRareFeaturess, int[] counts, int maxNumOfFeatures) {
		double[][][] res = new double[counts.length][][];
		for (int countInd = 0; countInd < counts.length; countInd++) {
			res[countInd] = new double[maxNumOfRareFeaturess.length][];
			for (int maxNumOfRareFeaturesInd = 0; maxNumOfRareFeaturesInd < maxNumOfRareFeaturess.length; maxNumOfRareFeaturesInd++) {
				res[countInd][maxNumOfRareFeaturesInd] = new double[maxNumOfFeatures + 1];
				for (int numOfFeatures = 0; numOfFeatures <= maxNumOfFeatures; numOfFeatures++) {
					res[countInd][maxNumOfRareFeaturesInd][numOfFeatures] = calcScore(
							maxRareCount,
							maxNumOfRareFeaturess[maxNumOfRareFeaturesInd],
							createFeatureValueToCountWithConstantCount(numOfFeatures, counts[countInd]),
							counts[countInd]);
				}
			}
		}
		return res;
	}

	private void printNewLineOrHeader(boolean printedHeader, int fromCount, int toCount) {
		int counts[] = new int[toCount - fromCount + 1];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = fromCount + i;
		}
		printNewLineOrHeader(printedHeader, counts);
	}

	private void printNewLineOrHeader(boolean printedHeader, int counts[]) {
		if (printedHeader) {
			System.out.println();
		} else {
			String featureCountsStr = "featureCount";
			for (int count : counts) {
				featureCountsStr += "\t" + count;
			}
			System.out.println(featureCountsStr);
		}
	}

	@Test
	public void printMaxRareCountEffect() {
		int maxNumOfRareFeatures = 10;
		int maxMaxRareCount = 10;
		int maxFeatureCount = maxMaxRareCount + 1;
		double[][][] scores = calcScoresOverConfigurationMatrix(maxNumOfRareFeatures, maxMaxRareCount, maxFeatureCount);

		boolean printedHeader = false;
		for (int maxRareCount = 0; maxRareCount < scores[0].length; maxRareCount++) {
			printNewLineOrHeader(printedHeader, 1, maxFeatureCount);
			printedHeader = true;
			System.out.print(maxRareCount + "\t");
			for (int featureCount = 0; featureCount < scores[0][0].length; featureCount++) {
				System.out.print(scores[maxRareCount][maxNumOfRareFeatures - 1][featureCount] + "\t");
			}
		}
	}

	@Test
	public void printMaxNumOfRareFeaturesEffect1() {
		int maxRareCount = 15;
		int maxNumOfRareFeaturess[] = new int[]{5, 7, 9, 11, 13, 15};
		int counts[] = new int[]{1,4};
		int maxNumOfFeatures = maxRareCount + 1;
		double[][][] scores = calcScoresOverConfigurationMatrix(maxRareCount, maxNumOfRareFeaturess, counts, maxNumOfFeatures);

		boolean printedHeader = false;
		for (int countInd = 0; countInd < counts.length; countInd++) {
			for (int maxNumOfRareFeaturesInd = 0; maxNumOfRareFeaturesInd < maxNumOfRareFeaturess.length; maxNumOfRareFeaturesInd++) {
				printNewLineOrHeader(printedHeader, 0, maxNumOfFeatures - 1);
				printedHeader = true;
				System.out.print(counts[countInd] + "->" + maxNumOfRareFeaturess[maxNumOfRareFeaturesInd] + "\t");
				for (int numOfFeatures = 0; numOfFeatures < scores[0][0].length; numOfFeatures++) {
					System.out.print(scores[countInd][maxNumOfRareFeaturesInd][numOfFeatures] + "\t");
				}
			}
		}
	}

	@Test
	public void printMaxNumOfRareFeaturesEffect2() {
		int maxRareCount = 15;
		int maxNumOfRareFeaturess[] = new int[]{5, 10, 15, 20, 30, 40, 50};
		int counts[] = new int[maxRareCount + 1];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = i + 1;
		}
		int maxNumOfFeatures = 5;
		double[][][] scores = calcScoresOverConfigurationMatrix(maxRareCount, maxNumOfRareFeaturess, counts, maxNumOfFeatures);

		boolean printedHeader = false;
		for (int numOfFeatures = 1; numOfFeatures <= maxNumOfFeatures; numOfFeatures++) {
			for (int maxNumOfRareFeaturesInd = 0; maxNumOfRareFeaturesInd < maxNumOfRareFeaturess.length; maxNumOfRareFeaturesInd++) {
				printNewLineOrHeader(printedHeader, counts);
				printedHeader = true;
				System.out.print(numOfFeatures + "->" + maxNumOfRareFeaturess[maxNumOfRareFeaturesInd] + "\t");
				for (int countInd = 0; countInd < counts.length; countInd++) {
					System.out.print(scores[countInd][maxNumOfRareFeaturesInd][numOfFeatures] + "\t");
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
			printNewLineOrHeader(printedHeader, 0, maxFeatureCount);
			printedHeader = true;
			System.out.print(maxRareCount + "\t");
			double lastScore = 0;
			for (int featureCount = 0; featureCount <= maxFeatureCount; featureCount++) {
				double score = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCount(1, featureCount), 1);
				if (featureCount > 1) {
					Assert.assertTrue(score >= lastScore);
				}
				lastScore = score;
				System.out.print(score + "\t");
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
