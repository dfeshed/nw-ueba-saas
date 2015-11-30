package fortscale.ml.model.prevalance.field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class RarityScorerTest {
	private double calcScore(int maxPossibleRarity, double maxRaritySum, Map<String, Double> featureValueToCountMap, String feature) {
		return calcScore(maxPossibleRarity, maxRaritySum, featureValueToCountMap, featureValueToCountMap.get(feature));
	}

	private double calcScore(int maxPossibleRarity, double maxRaritySum, Map<String, Double> featureValueToCountMap, double featureCount) {
		RarityScorer hist = new RarityScorer(featureValueToCountMap.values(), maxPossibleRarity, maxRaritySum);
		return hist.score(featureCount);
	}

	private void assertScore(int maxPossibleRarity, double maxRaritySum, Map<String, Double> featureValueToCountMap, String feature, double expected) {
		double score = calcScore(maxPossibleRarity, maxRaritySum, featureValueToCountMap, feature);
		Assert.assertEquals(expected, score, 0.001);
	}

	private void assertScoreRange(int maxPossibleRarity, double maxRaritySum, Map<String, Double> featureValueToCountMap, String feature, double expectedRangeMin, double expectedRangeMax) {
		double score = calcScore(maxPossibleRarity, maxRaritySum, featureValueToCountMap, feature);
		Assert.assertTrue(score >= expectedRangeMin);
		Assert.assertTrue(score <= expectedRangeMax);
	}

	@Test
	public void shouldScore0ToFeatureCountsGreaterThanMaxPossibleRarity() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		String veryRareFeature = "veryRare";
		double maxRaritySum = 10;
		for (int maxPossibleRarity = 1; maxPossibleRarity < 8; maxPossibleRarity++) {
			for (double count = 1; count <= maxPossibleRarity + 1; count++) {
				featureValueToCountMap.put(veryRareFeature, count);
				double rangeMin = (count == maxPossibleRarity + 1) ? 0 : 1;
				double rangeMax = (count == maxPossibleRarity + 1) ? 0 : 100;
				assertScoreRange(maxPossibleRarity, maxRaritySum, featureValueToCountMap, veryRareFeature, rangeMin, rangeMax);
			}
		}
	}

	@Test
	public void shouldScore99ToVeryRareFeatureNoMatterWhatIsMaxPossibleRarity() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		String veryRareFeature = "veryRare";
		featureValueToCountMap.put(veryRareFeature, 1D);
		double maxRaritySum = 10;
		for (int maxPossibleRarity = 1; maxPossibleRarity < 10; maxPossibleRarity++) {
			assertScore(maxPossibleRarity, maxRaritySum, featureValueToCountMap, veryRareFeature, 99);
		}
	}

	private double[][][] calcScoresOverConfigurationMatrix(int maxMaxRaritySum, int maxMaxPossibleRarity, int maxFeatureCount) {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		String veryRareFeature = "veryRare";
		featureValueToCountMap.put(veryRareFeature, 1D);
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

	private void assertMonotonicity(double[][][] scores, int overIndex, boolean shouldIncrease) {
		boolean hasStrongMonotonicity = false;
		for (int i = 0; i < scores.length; i++) {
			for (int j = 0; j < scores[0].length; j++) {
				for (int k = 0; k < scores[0][0].length; k++) {
					double scoresDelta;
					if (overIndex == 0 && i == 0 || overIndex == 1 && j == 0 || overIndex == 2 && k == 0) {
						scoresDelta = 0;
					} else if (overIndex == 0) {
						scoresDelta = scores[i][j][k] - scores[i - 1][j][k];
					} else if (overIndex == 1) {
						scoresDelta = scores[i][j][k] - scores[i][j - 1][k];
					} else {
						scoresDelta = scores[i][j][k] - scores[i][j][k - 1];
					}
					Assert.assertTrue(shouldIncrease && scoresDelta >= 0 || !shouldIncrease && scoresDelta <= 0);

					double firstValueInMonotonicSeries = scores
							[overIndex == 0 ? 0 : i]
							[overIndex == 1 ? 0 : j]
							[overIndex == 2 ? 0 : k];
					double lastValueInMonotonicSeries = scores
							[overIndex == 0 ? scores.length - 1 : i]
							[overIndex == 1 ? scores[0].length - 1 : j]
							[overIndex == 2 ? scores[0][0].length - 1 : k];
					hasStrongMonotonicity = hasStrongMonotonicity || lastValueInMonotonicSeries - firstValueInMonotonicSeries != 0;
				}
			}
		}
		// it's ok that some series are constant, but if all of them are - the model probably has a bug
		Assert.assertTrue(hasStrongMonotonicity);
	}

	@Test
	public void shouldScoreDecreasinglyWhenFeatureCountIncreases() {
		assertMonotonicity(calcScoresOverConfigurationMatrix(100, 10, 10), 2, false);
	}

	@Test
	public void shouldScoreIncreasinglyWhenMaxRaritySumIncreases() {
		assertMonotonicity(calcScoresOverConfigurationMatrix(100, 10, 10), 1, true);
	}

	@Test
	public void shouldScoreIncreasinglyWhenMaxPossibleRarityIncreases() {
		assertMonotonicity(calcScoresOverConfigurationMatrix(100, 10, 10), 0, true);
	}

	private void printMaxRaritySumEffect() throws Exception {
		int maxPossibleRarity = 30;
		int maxRaritySums[] = new int[]{5, 10, 15, 20, 30, 40, 50};
		int maxCount = 3;
		int maxNumOfFeatures = 30;
		double[][][] res = new double[maxCount - 1][][];
		for (int count = 1; count < maxCount; count++) {
			res[count - 1] = new double[maxRaritySums.length][];
			for (int maxRaritySumInd = 0; maxRaritySumInd < maxRaritySums.length; maxRaritySumInd++) {
				res[count - 1][maxRaritySumInd] = new double[maxNumOfFeatures];
				Map<String, Double> featureValueToCountMap = new HashMap<>();
				for (int numOfFeatures = 1; numOfFeatures <= maxNumOfFeatures; numOfFeatures++) {
					String feature = "feature-" + numOfFeatures;
					featureValueToCountMap.put(feature, (double) count);
					res[count - 1][maxRaritySumInd][numOfFeatures - 1] = calcScore(maxPossibleRarity, maxRaritySums[maxRaritySumInd], featureValueToCountMap, feature);
				}
			}
		}

		System.out.println("count -> maxRaritySum (each column has numOfFeatures from 1 to " + maxNumOfFeatures + ")");
		for (int count = 1; count < maxCount; count++) {
			for (int maxRaritySum : maxRaritySums) {
				System.out.print(count + "->" + maxRaritySum + "\t");
			}
		}
		for (int numOfFeatures = 1; numOfFeatures <= maxNumOfFeatures; numOfFeatures++) {
			System.out.println();
			for (int count = 1; count < maxCount; count++) {
				for (int maxRaritySumInd = 0; maxRaritySumInd < maxRaritySums.length; maxRaritySumInd++) {
					System.out.print(res[count - 1][maxRaritySumInd][numOfFeatures - 1] + "\t");
				}
			}
		}
	}
}
