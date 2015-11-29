package fortscale.ml.model.prevalance.field;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class RarityScorer {
	private static final double SMALLEST_POSSIBLE_SCORE = 1;
	private static final int POSSIBLE_RARITY_NORMALIZED_RANGE = 3;
	private static final double STEEPNESS = Math.log(1 / (SMALLEST_POSSIBLE_SCORE / 100) - 1) / Math.log(POSSIBLE_RARITY_NORMALIZED_RANGE);
	private double rarityGauge;
	private double maxPossibleRarity;

	public RarityScorer(Collection<Double> featureOccurrences, int maxPossibleRarity, double maxRaritySum) {
		this.maxPossibleRarity = maxPossibleRarity;
		double raritySum = 0;
		for (double occurrence : featureOccurrences) {
			raritySum += occurrence * calcCommonnessDiscounting(occurrence);
		}
		rarityGauge = Math.min(1, Math.pow(raritySum, 2) / maxRaritySum);
	}

	private double calcCommonnessDiscounting(double occurrence) {
		return 1 / (1 + Math.pow((occurrence - 1) * POSSIBLE_RARITY_NORMALIZED_RANGE / maxPossibleRarity, STEEPNESS));
	}

	public double score(Double featureCount) {
		double featureRarity = (1 - rarityGauge) * calcCommonnessDiscounting(featureCount);
		return (int) (100 * featureRarity);
	}
}
