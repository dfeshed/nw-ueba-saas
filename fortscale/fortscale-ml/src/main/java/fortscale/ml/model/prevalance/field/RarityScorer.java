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

	private int maxNumOfRareFeatures;
	private double[] buckets;
	private int totalEvents;

	public RarityScorer(Collection<Integer> featureOccurrences, int maxRareCount, int maxNumOfRareFeatures) {
		this.maxNumOfRareFeatures = maxNumOfRareFeatures;
		buckets = new double[maxRareCount * 2];
		totalEvents = 0;
		for (int occurrence : featureOccurrences) {
			if (occurrence <= buckets.length) {
				buckets[occurrence - 1]++;
			}
			totalEvents += occurrence;
		}
		if (totalEvents == 0) {
			totalEvents = 1;
		}
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
		return applyLogisticFunc(occurrence - 1, getMaxRareCount());
	}

	private int getMaxRareCount() {
		return buckets.length / 2;
	}

	public double score(int featureCount) {
		if (featureCount > getMaxRareCount()) {
			return 0;
		}
		double numRareEvents = 0;
		double numRareFeatures = 0;
		for (int i = 0; i < featureCount; i++) {
			numRareEvents += (i + 1) * buckets[i];
			numRareFeatures += buckets[i];
		}
		for (int i = featureCount; i < featureCount + getMaxRareCount(); i++) {
			double commonnessDiscount = calcCommonnessDiscounting(i - featureCount + 2);
			numRareEvents += (i + 1) * buckets[i] * commonnessDiscount;
			numRareFeatures += buckets[i] * commonnessDiscount;
		}
		double commonEventProbability = 1 - numRareEvents / totalEvents;
		double numRareFeaturesDiscount = 1 - Math.min(1, Math.pow(numRareFeatures / maxNumOfRareFeatures, RARITY_SUM_EXPONENT));
		double score = commonEventProbability * numRareFeaturesDiscount * calcCommonnessDiscounting(featureCount);
		return (int) (MAX_POSSIBLE_SCORE * score);
	}
}
