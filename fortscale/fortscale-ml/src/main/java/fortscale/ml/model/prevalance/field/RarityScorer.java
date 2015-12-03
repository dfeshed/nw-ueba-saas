package fortscale.ml.model.prevalance.field;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class RarityScorer {
	private static final double MIN_POSSIBLE_SCORE = 1;
	private static final int MAX_POSSIBLE_SCORE = 100;
	private static final double RARITY_SUM_EXPONENT = 1.8;
	private static final int LOGISTIC_FUNCTION_DOMAIN = 3;
	// STEEPNESS makes sure that at the end of the domain (LOGISTIC_FUNCTION_DOMAIN)
	// the function gets 0.99999 * MIN_POSSIBLE_SCORE - so once we multiply by
	// MAX_POSSIBLE_SCORE (inside score function) we get a rounded score of 0
	private static final double STEEPNESS = Math.log(1 / (0.99999 * MIN_POSSIBLE_SCORE / MAX_POSSIBLE_SCORE) - 1) / Math.log(LOGISTIC_FUNCTION_DOMAIN);

	private double rarityGauge;
	private double maxPossibleRarity;

	public RarityScorer(Collection<Integer> featureOccurrences, int maxPossibleRarity, double maxRaritySum) {
		this.maxPossibleRarity = maxPossibleRarity;
		double raritySum = 0;
		for (int occurrence : featureOccurrences) {
			raritySum += occurrence * calcCommonnessDiscounting(occurrence);
		}
		rarityGauge = Math.min(1, Math.pow(raritySum, RARITY_SUM_EXPONENT) / maxRaritySum);
	}

	/**
	 * Apply a logistic function on the given input.
	 * A logistic function behaves approximately like this:
	 *    |
	 *   1|......
	 *    |       .....
	 *    |             ...
	 *    |                 ..
	 *    |                    .
	 *    |                     .
	 *    |                      .
	 *    |                       .
	 * 0.5|                       .
	 *    |                        .
	 *    |                         .
	 *    |                          .
	 *    |                            ...
	 *    |                                .....
	 *    |                                      ........
	 *   _|______________________________________________
	 *    |                                          (maxXValue)
	 * @param x the function input.
	 * @param maxXValue values above maxXValue will get approximately 0 as output (as shown in the fine ascii art).
	 */
	private double applyLogisticFunc(double x, double maxXValue) {
		return 1 / (1 + Math.pow(x * LOGISTIC_FUNCTION_DOMAIN / maxXValue, STEEPNESS));
	}

	private double calcCommonnessDiscounting(double occurrence) {
		return applyLogisticFunc(occurrence - 1, maxPossibleRarity);
	}

	public double score(Double featureCount) {
		double rarityGaugeDiscountedByFeatureRarity = (1 - rarityGauge) * calcCommonnessDiscounting(featureCount);
		return (int) (MAX_POSSIBLE_SCORE * rarityGaugeDiscountedByFeatureRarity);
	}
}
