package fortscale.ml.model.prevalance.field;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class RarityScorer {
	private static final double EPSILON = 0.01;
	private double rarityGauge;
	private double maxPossibleRarity;

	public RarityScorer(Collection<Double> featureOccurrences, int maxPossibleRarity, double maxRaritySum) {
		this.maxPossibleRarity = maxPossibleRarity;
		double raritySum = 0;
		double factor = getFactor();
		for (double occurrence : featureOccurrences) {
			raritySum += occurrence * calcCommonnessDiscounting(occurrence, factor);
		}
		rarityGauge = Math.min(1, Math.pow(raritySum, 2) / maxRaritySum);
	}

	private double calcCommonnessDiscounting(double occurrence, double factor) {
		return Math.pow(1.0 / factor, occurrence);
	}

	private double getFactor() {
		return 1.0 / (Math.pow(EPSILON, 1.0 / (maxPossibleRarity + 1)));
	}

	public double score(Double featureCount) {
		if (rarityGauge == 1) {
			return 0;
		}
		double featureRarity = (1 - rarityGauge) * calcCommonnessDiscounting(featureCount, getFactor());
		return (int) (100 * featureRarity);
	}
}
