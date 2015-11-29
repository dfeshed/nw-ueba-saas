package fortscale.ml.model.prevalance.field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class RarityScorerTest {
	private void  assertScoreRange(int maxPossibleRarity, double maxRaritySum, Map<String, Double> featureValueToCountMap, String feature, double expectedRangeMin, double expectedRangeMax) {
		RarityScorer hist = new RarityScorer(featureValueToCountMap.values(), maxPossibleRarity, maxRaritySum);
		double score = hist.score(featureValueToCountMap.get(feature));
		Assert.assertTrue(score >= expectedRangeMin);
		Assert.assertTrue(score <= expectedRangeMax);
	}

	@Test
	public void shouldScore0ToFeatureCountsGreaterThanMaxPossibleRarity() throws Exception {
		Map<String, Double> featureValueToCountMap = new HashMap<>();
		featureValueToCountMap.put("veryCommon", 1000D);
		String veryRareFeature = "veryRare";
		double maxRaritySum = 10;
		for (int maxPossibleRarity = 1; maxPossibleRarity < 10; maxPossibleRarity++) {
			for (int count = 1; count <= maxPossibleRarity + 1; count++) {
				featureValueToCountMap.put(veryRareFeature, (double) count);
				double rangeMin = 0;
				double rangeMax = (count == maxPossibleRarity + 1) ? 0 : 100;
				assertScoreRange(maxPossibleRarity, maxRaritySum, featureValueToCountMap, veryRareFeature, rangeMin, rangeMax);
			}
		}
	}
}
