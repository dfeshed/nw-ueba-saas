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
		RarityScorer hist = new RarityScorer(featureValueToCountMap.values(), maxPossibleRarity, maxRaritySum);
		return hist.score(featureValueToCountMap.get(feature));
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
	public void shouldScore100ToVeryRareFeatureNoMatterWhatIsMaxPossibleRarity() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		String veryRareFeature = "veryRare";
		featureValueToCountMap.put(veryRareFeature, 1D);
		double maxRaritySum = 10;
		for (int maxPossibleRarity = 1; maxPossibleRarity < 10; maxPossibleRarity++) {
			assertScore(maxPossibleRarity, maxRaritySum, featureValueToCountMap, veryRareFeature, 99);
		}
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
			for (int maxRaritySumInd = 0; maxRaritySumInd < maxRaritySums.length; maxRaritySumInd++) {
				System.out.print(count + "->" + maxRaritySums[maxRaritySumInd] + "\t");
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
