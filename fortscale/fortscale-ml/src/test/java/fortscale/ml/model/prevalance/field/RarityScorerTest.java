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
	private double calcScore(int maxPossibleRarity, int maxRaritySum, Map<String, Integer> featureValueToCountMap, int featureCount) {
		RarityScorer hist = new RarityScorer(featureValueToCountMap.values(), maxPossibleRarity, maxRaritySum);
		return hist.score(featureCount);
	}

	private void assertScoreRange(int maxPossibleRarity, int maxRaritySum, Map<String, Integer> featureValueToCountMap, int featureCount, double expectedRangeMin, double expectedRangeMax) {
		double score = calcScore(maxPossibleRarity, maxRaritySum, featureValueToCountMap, featureCount);
		Assert.assertTrue(String.format("score (%e) >= expectedRangeMin (%e) does not hold", score, expectedRangeMin), score >= expectedRangeMin);
		Assert.assertTrue(String.format("score (%e) <= expectedRangeMax (%e) does not hold", score, expectedRangeMax), score <= expectedRangeMax);
	}

	private Map<String, Integer> createFeatureValueToCountWithConstantCount(int numOfFeatures, int count) {
		Map<String, Integer> res = new HashMap<>(numOfFeatures);
		while (numOfFeatures-- > 0) {
			res.put("feature-" + numOfFeatures, count);
		}
		return res;
	}

	/*************************************************************************************
	 *************************************************************************************
	 ************ TEST BASIC MODEL BEHAVIOUR WHEN MODEL PARAMETERS ARE ISOLATED **********
	 *************************************************************************************
	 *************************************************************************************/

	@Test
	public void shouldScore0ToFeatureCountsGreaterThanMaxPossibleRarity() throws Exception {
		int maxRaritySum = 10;
		for (int maxPossibleRarity = 1; maxPossibleRarity < 10; maxPossibleRarity++) {
			for (int count = 1; count <= maxPossibleRarity + 1; count++) {
				double rangeMin = (count == maxPossibleRarity + 1) ? 0 : 1;
				double rangeMax = (count == maxPossibleRarity + 1) ? 0 : 100;
				assertScoreRange(maxPossibleRarity, maxRaritySum, new HashMap<String, Integer>(), count, rangeMin, rangeMax);
			}
		}
	}

	@Test
	public void shouldScore100ToVeryRareFeatureNoMatterWhatIsMaxPossibleRarity() throws Exception {
		int maxRaritySum = 10;
		int veryRareFeatureCount = 1;
		for (int maxPossibleRarity = 1; maxPossibleRarity < 10; maxPossibleRarity++) {
			assertScoreRange(maxPossibleRarity, maxRaritySum, new HashMap<String, Integer>(), veryRareFeatureCount, 99, 100);
		}
	}

	@Test
	public void shouldScore0WhenThereAreMoreThanMaxRaritySumRareFeatures() throws Exception {
		int maxPossibleRarity = 100;
		int count = 1;
		for (int maxRaritySum = 1; maxRaritySum < 10; maxRaritySum++) {
			for (int numOfFeatures = 0; numOfFeatures <= maxRaritySum + 1; numOfFeatures++) {
				double rangeMin = (numOfFeatures == maxRaritySum + 1) ? 0 : 1;
				double rangeMax = (numOfFeatures == maxRaritySum + 1) ? 0 : 100;
				assertScoreRange(maxPossibleRarity, maxRaritySum, createFeatureValueToCountWithConstantCount(numOfFeatures, count), count, rangeMin, rangeMax);
			}
		}
	}

	private double[][][] calcScoresOverConfigurationMatrix(int maxMaxRaritySum, int maxMaxPossibleRarity, int maxFeatureCount) {
		return calcScoresOverConfigurationMatrix(new HashMap<String, Integer>(), maxMaxRaritySum, maxMaxPossibleRarity, maxFeatureCount);
	}

	private double[][][] calcScoresOverConfigurationMatrix(Map<String, Integer> featureValueToCountMap, int maxMaxRaritySum, int maxMaxPossibleRarity, int maxFeatureCount) {
		double[][][] scores = new double[maxMaxPossibleRarity][][];
		for (int maxPossibleRarity = 1; maxPossibleRarity <= maxMaxPossibleRarity; maxPossibleRarity++) {
			scores[maxPossibleRarity - 1] = new double[maxMaxRaritySum][];
			for (int maxRaritySum = 1; maxRaritySum <= maxMaxRaritySum; maxRaritySum++) {
				scores[maxPossibleRarity - 1][maxRaritySum - 1] = new double[maxFeatureCount];
				for (int featureCount = 1; featureCount <= maxFeatureCount; featureCount++) {
					scores[maxPossibleRarity - 1][maxRaritySum - 1][featureCount - 1] = calcScore(maxPossibleRarity, maxRaritySum, featureValueToCountMap, featureCount);
				}
			}
		}
		return scores;
	}

	private enum PARAMETER {
		MAX_POSSIBLE_RARITY,
		MAX_RARITY_SUM,
		FEATURE_COUNT
	}

	private void assertMonotonicity(@Nonnull double[][][] scores, PARAMETER overParameter, @Nullable Boolean shouldIncrease) {
		boolean hasStrongMonotonicity = false;
		for (int maxPossibleRarityInd = 0; maxPossibleRarityInd < scores.length; maxPossibleRarityInd++) {
			for (int maxRaritySumInd = 0; maxRaritySumInd < scores[0].length; maxRaritySumInd++) {
				for (int featureCountInd = 0; featureCountInd < scores[0][0].length; featureCountInd++) {
					double scoresDelta;
					if (overParameter == PARAMETER.MAX_POSSIBLE_RARITY && maxPossibleRarityInd == 0 ||
							overParameter == PARAMETER.MAX_RARITY_SUM && maxRaritySumInd == 0 ||
							overParameter == PARAMETER.FEATURE_COUNT && featureCountInd == 0) {
						scoresDelta = 0;
					} else if (overParameter == PARAMETER.MAX_POSSIBLE_RARITY) {
						scoresDelta = scores[maxPossibleRarityInd][maxRaritySumInd][featureCountInd] - scores[maxPossibleRarityInd - 1][maxRaritySumInd][featureCountInd];
					} else if (overParameter == PARAMETER.MAX_RARITY_SUM) {
						scoresDelta = scores[maxPossibleRarityInd][maxRaritySumInd][featureCountInd] - scores[maxPossibleRarityInd][maxRaritySumInd - 1][featureCountInd];
					} else {
						scoresDelta = scores[maxPossibleRarityInd][maxRaritySumInd][featureCountInd] - scores[maxPossibleRarityInd][maxRaritySumInd][featureCountInd - 1];
					}
					if (shouldIncrease == null) {
						Assert.assertTrue(scoresDelta == 0);
					} else {
						Assert.assertTrue(shouldIncrease && scoresDelta >= 0 || !shouldIncrease && scoresDelta <= 0);
					}

					double firstValueInMonotonicSeries = scores
							[overParameter == PARAMETER.MAX_POSSIBLE_RARITY ? 0 : maxPossibleRarityInd]
							[overParameter == PARAMETER.MAX_RARITY_SUM ? 0 : maxRaritySumInd]
							[overParameter == PARAMETER.FEATURE_COUNT ? 0 : featureCountInd];
					double lastValueInMonotonicSeries = scores
							[overParameter == PARAMETER.MAX_POSSIBLE_RARITY ? scores.length - 1 : maxPossibleRarityInd]
							[overParameter == PARAMETER.MAX_RARITY_SUM ? scores[0].length - 1 : maxRaritySumInd]
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
	public void shouldScoreIncreasinglyWhenMaxPossibleRarityIncreases() {
		assertMonotonicity(calcScoresOverConfigurationMatrix(100, 10, 10), PARAMETER.MAX_POSSIBLE_RARITY, true);
	}

	@Test
	public void shouldScoreIncreasinglyWhenMaxRaritySumIncreases() {
		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		String veryRareFeature = "veryRare";
		featureValueToCountMap.put(veryRareFeature, 1);
		assertMonotonicity(calcScoresOverConfigurationMatrix(featureValueToCountMap, 100, 10, 10), PARAMETER.MAX_RARITY_SUM, true);
	}

	@Test
	public void shouldScoreConstantlyWhenMaxRaritySumIncreasesButModelDataIsEmpty() {
		assertMonotonicity(calcScoresOverConfigurationMatrix(100, 10, 10), PARAMETER.MAX_RARITY_SUM, null);
	}

	@Test
	public void shouldScoreLessWhenThereAreManyFeaturesWithTheSameCountAndThenTheirCountIncreasesByOne() {
		for (int maxPossibleRarity = 1; maxPossibleRarity < 30; maxPossibleRarity++) {
			for (int maxRaritySum = 1; maxRaritySum < 100; maxRaritySum += 5) {
				for (int featureCount = 1; featureCount <= maxPossibleRarity; featureCount++) {
					for (int numOfFeatures = 1; numOfFeatures < 10; numOfFeatures++) {
						double scoreBeforeCountIncreases = calcScore(maxPossibleRarity, maxRaritySum, createFeatureValueToCountWithConstantCount(numOfFeatures, featureCount), featureCount);
						double scoreAfterCountIncreases = calcScore(maxPossibleRarity, maxRaritySum, createFeatureValueToCountWithConstantCount(numOfFeatures, featureCount + 1), featureCount + 1);
						String msg = String.format("score isn't decreasing in the following configuration:\nmaxPossibleRarity = %d\nmaxRaritySum = %d\nfeatureCount = %d -> %d\nnumOfFeatures = %d", maxPossibleRarity, maxRaritySum, featureCount, featureCount + 1, numOfFeatures);
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

	private double[][][] calcScoresOverConfigurationMatrix(int maxPossibleRarity, int[] maxRaritySums, int[] counts, int maxNumOfFeatures) {
		double[][][] res = new double[counts.length][][];
		for (int countInd = 0; countInd < counts.length; countInd++) {
			res[countInd] = new double[maxRaritySums.length][];
			for (int maxRaritySumInd = 0; maxRaritySumInd < maxRaritySums.length; maxRaritySumInd++) {
				res[countInd][maxRaritySumInd] = new double[maxNumOfFeatures + 1];
				for (int numOfFeatures = 0; numOfFeatures <= maxNumOfFeatures; numOfFeatures++) {
					res[countInd][maxRaritySumInd][numOfFeatures] = calcScore(
							maxPossibleRarity,
							maxRaritySums[maxRaritySumInd],
							createFeatureValueToCountWithConstantCount(numOfFeatures, counts[countInd]),
							counts[countInd]);
				}
			}
		}
		return res;
	}

	@Test
	public void printMaxPossibleRarityEffect() {
		int maxRaritySum = 10;
		int maxMaxPossibleRarity = 10;
		int maxFeatureCount = maxMaxPossibleRarity + 1;
		double[][][] scores = calcScoresOverConfigurationMatrix(maxRaritySum, maxMaxPossibleRarity, maxFeatureCount);

		System.out.println("maxPossibleRarity (each column has constant maxPossibleRarity, and varying featureCount from 1 to " + maxFeatureCount + "). maxRaritySum is always " + maxRaritySum);
		for (int maxPossibleRarity = 1; maxPossibleRarity <= scores.length; maxPossibleRarity++) {
			System.out.print(maxPossibleRarity + "\t");
		}
		for (int featureCount = 0; featureCount < scores[0][0].length; featureCount++) {
			System.out.println();
			for (int maxPossibleRarity = 0; maxPossibleRarity < scores[0].length; maxPossibleRarity++) {
				System.out.print(scores[maxPossibleRarity][maxRaritySum - 1][featureCount] + "\t");
			}
		}
	}

	@Test
	public void printMaxRaritySumEffect1() {
		int maxPossibleRarity = 15;
		int maxRaritySums[] = new int[]{5, 7, 9, 11, 13, 15};
		int counts[] = new int[]{1,4};
		int maxNumOfFeatures = maxPossibleRarity + 1;
		double[][][] scores = calcScoresOverConfigurationMatrix(maxPossibleRarity, maxRaritySums, counts, maxNumOfFeatures);

		System.out.println("count -> maxRaritySum (each column has constant count and maxRaritySum, and varying numOfFeatures from 0 to " + maxNumOfFeatures + ")");
		for (int countInd = 0; countInd < counts.length; countInd++) {
			for (int maxRaritySum : maxRaritySums) {
				System.out.print(counts[countInd] + "->" + maxRaritySum + "\t");
			}
		}
		for (int numOfFeatures = 0; numOfFeatures < scores[0][0].length; numOfFeatures++) {
			System.out.println();
			for (int countInd = 0; countInd < counts.length; countInd++) {
				for (int maxRaritySumInd = 0; maxRaritySumInd < maxRaritySums.length; maxRaritySumInd++) {
					System.out.print(scores[countInd][maxRaritySumInd][numOfFeatures] + "\t");
				}
			}
		}
	}

	@Test
	public void printMaxRaritySumEffect2() {
		int maxPossibleRarity = 15;
		int maxRaritySums[] = new int[]{5, 10, 15, 20, 30, 40, 50};
		int counts[] = new int[maxPossibleRarity + 1];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = i + 1;
		}
		int maxNumOfFeatures = 5;
		double[][][] scores = calcScoresOverConfigurationMatrix(maxPossibleRarity, maxRaritySums, counts, maxNumOfFeatures);

		String countsStr = String.valueOf(counts[0]);
		for (int i = 1; i < counts.length; i++) {
			countsStr += ", " + counts[i];
		}
		System.out.println("numOfFeatures -> maxRaritySum (each column has constant numOfFeatures and maxRaritySum, and varying featureCount of " +  countsStr + ")");
		for (int numOfFeatures = 1; numOfFeatures <= maxNumOfFeatures; numOfFeatures++) {
			for (int maxRaritySum : maxRaritySums) {
				System.out.print(numOfFeatures + "->" + maxRaritySum + "\t");
			}
		}

		for (int countInd = 0; countInd < counts.length; countInd++) {
			System.out.println();
			for (int numOfFeatures = 1; numOfFeatures <= maxNumOfFeatures; numOfFeatures++) {
				for (int maxRaritySumInd = 0; maxRaritySumInd < maxRaritySums.length; maxRaritySumInd++) {
					System.out.print(scores[countInd][maxRaritySumInd][numOfFeatures] + "\t");
				}
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
		int maxPossibleRarity = 10;
		int maxRaritySum = 30;
		int veryRareFeatureCount = 1;
		int veryCommonFeatureCount = 10000;
		double scoreWithCommon = calcScore(maxPossibleRarity, maxRaritySum, createFeatureValueToCountWithConstantCount(10, veryCommonFeatureCount), veryRareFeatureCount);
		double scoreWithoutCommon = calcScore(maxPossibleRarity, maxRaritySum, createFeatureValueToCountWithConstantCount(0, 0), veryRareFeatureCount);
		Assert.assertEquals(scoreWithoutCommon, scoreWithCommon, 1);
		Assert.assertTrue(scoreWithoutCommon >= 99);
	}

	@Test
	public void simpleInputOutputForOccurrencesHistogram() throws Exception {
		int maxPossibleRarity = 6;
		int maxRaritySum = 5;

		Random rnd = new Random(1);
		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		for (int i = 0; i < 2; i++) {
			int val = (int) Math.min( 100.0, rnd.nextDouble( ) * 100 + 8);
			featureValueToCountMap.put(String.format("test%d", i), val);
		}

		int[] counts = new int[]{1, 2, 3, 4};
//		double[] scores = new double[]{99, 93, 61, 18};
		double[] scores = new double[]{99, 94, 49, 15};
		for (int i = 0; i < scores.length; i++) {
			double score = calcScore(maxPossibleRarity, maxRaritySum, featureValueToCountMap, counts[i]);
			Assert.assertEquals(scores[i], score, 1);
		}
	}

//	@Test
//	public void shouldScoreWithFixedRatioGivenModelsBuiltWithDifferentData() throws Exception {
//		int maxPossibleRarity = 6;
//		double maxRaritySum = 10;
//		Map<String, Double> featureValueToCountMap1 = new HashMap<>();
//		Map<String, Double> featureValueToCountMap2 = new HashMap<>();
//		for (double i = 1; i <= maxPossibleRarity; i++) {
//			featureValueToCountMap1.put("feature-" + i, i);
//			featureValueToCountMap2.put("feature-" + i, i * 2);
//		}
//		double[] ratios = new double[maxPossibleRarity];
//		for (int count = 1; count <= maxPossibleRarity; count++) {
//			double score1 = calcScore(maxPossibleRarity, maxRaritySum, featureValueToCountMap1, count);
//			double score2 = calcScore(maxPossibleRarity, maxRaritySum, featureValueToCountMap2, count);
//			ratios[count - 1] = score2 / score1;
//		}
//		for (int i = 1; i < ratios.length; i++) {
//			System.out.println(ratios[i]);
//			Assert.assertEquals(ratios[i], ratios[i - 1], 0.01);
//		}
//	}
}
