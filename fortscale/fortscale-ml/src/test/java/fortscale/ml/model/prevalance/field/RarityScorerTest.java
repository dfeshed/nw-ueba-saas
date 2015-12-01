package fortscale.ml.model.prevalance.field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class RarityScorerTest {
	private double calcScore(int maxPossibleRarity, double maxRaritySum, Map<String, Double> featureValueToCountMap, double featureCount) {
		RarityScorer hist = new RarityScorer(featureValueToCountMap.values(), maxPossibleRarity, maxRaritySum);
		return hist.score(featureCount);
	}

	private void assertScoreRange(int maxPossibleRarity, double maxRaritySum, Map<String, Double> featureValueToCountMap, double featureCount, double expectedRangeMin, double expectedRangeMax) {
		double score = calcScore(maxPossibleRarity, maxRaritySum, featureValueToCountMap, featureCount);
		Assert.assertTrue(score >= expectedRangeMin);
		Assert.assertTrue(score <= expectedRangeMax);
	}

	@Test
	public void shouldScore0ToFeatureCountsGreaterThanMaxPossibleRarity() throws Exception {
		double maxRaritySum = 10;
		for (int maxPossibleRarity = 1; maxPossibleRarity < 8; maxPossibleRarity++) {
			for (double count = 1; count <= maxPossibleRarity + 1; count++) {
				double rangeMin = (count == maxPossibleRarity + 1) ? 0 : 1;
				double rangeMax = (count == maxPossibleRarity + 1) ? 0 : 100;
				assertScoreRange(maxPossibleRarity, maxRaritySum, new HashMap<String, Double>(), count, rangeMin, rangeMax);
			}
		}
	}

	@Test
	public void shouldScore99ToVeryRareFeatureNoMatterWhatIsMaxPossibleRarity() throws Exception {
		double maxRaritySum = 10;
		int veryRareFeatureCount = 1;
		for (int maxPossibleRarity = 1; maxPossibleRarity < 10; maxPossibleRarity++) {
			Assert.assertEquals(100, calcScore(maxPossibleRarity, maxRaritySum, new HashMap<String, Double>(), veryRareFeatureCount), 0.0001);
		}
	}

	private double[][][] calcScoresOverConfigurationMatrix(int maxMaxRaritySum, int maxMaxPossibleRarity, int maxFeatureCount) {
		return calcScoresOverConfigurationMatrix(new HashMap<String, Double>(), maxMaxRaritySum, maxMaxPossibleRarity, maxFeatureCount);
	}

	private double[][][] calcScoresOverConfigurationMatrix(Map<String, Double> featureValueToCountMap, int maxMaxRaritySum, int maxMaxPossibleRarity, int maxFeatureCount) {
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
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		String veryRareFeature = "veryRare";
		featureValueToCountMap.put(veryRareFeature, 1D);
		assertMonotonicity(calcScoresOverConfigurationMatrix(featureValueToCountMap, 100, 10, 10), PARAMETER.MAX_RARITY_SUM, true);
	}

	@Test
	public void shouldScoreConstantlyWhenMaxRaritySumIncreasesButModelDataIsEmpty() {
		assertMonotonicity(calcScoresOverConfigurationMatrix(100, 10, 10), PARAMETER.MAX_RARITY_SUM, null);
	}

//	@Test
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

//	@Test
	public void printMaxRaritySumEffect() {
		int maxPossibleRarity = 15;
		int maxRaritySums[] = new int[]{5, 10, 15, 20, 30, 40, 50};
		int counts[] = new int[]{1,4};
		int maxNumOfFeatures = 30;
		double[][][] res = new double[counts.length][][];
		for (int countInd = 0; countInd < counts.length; countInd++) {
			res[countInd] = new double[maxRaritySums.length][];
			for (int maxRaritySumInd = 0; maxRaritySumInd < maxRaritySums.length; maxRaritySumInd++) {
				res[countInd][maxRaritySumInd] = new double[maxNumOfFeatures];
				Map<String, Double> featureValueToCountMap = new HashMap<>();
				for (int numOfFeatures = 0; numOfFeatures < maxNumOfFeatures; numOfFeatures++) {
					String feature = "feature-" + numOfFeatures;
					res[countInd][maxRaritySumInd][numOfFeatures] = calcScore(maxPossibleRarity, maxRaritySums[maxRaritySumInd], featureValueToCountMap, counts[countInd]);
					featureValueToCountMap.put(feature, (double) counts[countInd]);
				}
			}
		}

		System.out.println("count -> maxRaritySum (each column has constant count and maxRaritySum, and varying numOfFeatures from 1 to " + maxNumOfFeatures + ")");
		for (int countInd = 0; countInd < counts.length; countInd++) {
			for (int maxRaritySum : maxRaritySums) {
				System.out.print(counts[countInd] + "->" + maxRaritySum + "\t");
			}
		}
		for (int numOfFeatures = 1; numOfFeatures <= maxNumOfFeatures; numOfFeatures++) {
			System.out.println();
			for (int countInd = 0; countInd < counts.length; countInd++) {
				for (int maxRaritySumInd = 0; maxRaritySumInd < maxRaritySums.length; maxRaritySumInd++) {
					System.out.print(res[countInd][maxRaritySumInd][numOfFeatures - 1] + "\t");
				}
			}
		}
	}
}
