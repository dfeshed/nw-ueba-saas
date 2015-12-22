package fortscale.ml.model.prevalance.field;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
/**
 * For documentation and explanation of how this model works - refer to https://fortscale.atlassian.net/wiki/display/FSC/category+rarity+model
 */
public class RarityScorer {
	private static final double MIN_POSSIBLE_SCORE = 1;
	private static final int MAX_POSSIBLE_SCORE = 100;
	private static final double RARITY_SUM_EXPONENT = 1.8;
	private static final int LOGISTIC_FUNCTION_DOMAIN = 3;
	// STEEPNESS makes sure that at the end of the domain (LOGISTIC_FUNCTION_DOMAIN)
	// the function gets 0.99999 * MIN_POSSIBLE_SCORE - so once we multiply by
	// MAX_POSSIBLE_SCORE (inside score function) we get a rounded score of 0
	private static final double STEEPNESS = Math.log(1 / (0.99999 * MIN_POSSIBLE_SCORE / MAX_POSSIBLE_SCORE) - 1) / Math.log(LOGISTIC_FUNCTION_DOMAIN);

	private double[] buckets;
	private int minEvents;
	private int totalEvents;
	private int maxNumOfRareFeatures;

	public RarityScorer(int minEvents, int maxRareCount, int maxNumOfRareFeatures, Map<Integer, Double> occurrencesToNumOfFeatures) {
		this.minEvents = minEvents;
		this.maxNumOfRareFeatures = maxNumOfRareFeatures;
		buckets = new double[maxRareCount * 2];
		totalEvents = 0;
		for (Map.Entry<Integer, Double> entry : occurrencesToNumOfFeatures.entrySet()) {
			int occurrences = entry.getKey();
			double numOfFeatures = entry.getValue();
			if (occurrences <= buckets.length) {
				buckets[occurrences - 1] = numOfFeatures;
			}
			totalEvents += numOfFeatures * occurrences;
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
	 *
	 * For more info, look into
	 * 		https://www.google.co.il/search?q=1%2F(1%2B(x%2B1.5)%5E4.18)&oq=1%2F(1%2B(x%2B1.5)%5E4.18)&aqs=chrome..69i57j69i59l2.239j0j7&sourceid=chrome&es_sm=0&ie=UTF-8#q=1%2F(1%2Bx%5E4.182667533025268)
	 *
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

	public Double score(int featureCount) {
		if (totalEvents < minEvents) {
			return null;
		}
		if (featureCount > getMaxRareCount()) {
			return 0D;
		}
		double numRareEvents = 0;
		double numDistinctRareFeatures = 0;
		for (int i = 0; i < featureCount; i++) {
			numRareEvents += (i + 1) * buckets[i];
			numDistinctRareFeatures += buckets[i];
		}
		for (int i = featureCount; i < featureCount + getMaxRareCount(); i++) {
			double commonnessDiscount = calcCommonnessDiscounting(i - featureCount + 2);
			numRareEvents += (i + 1) * buckets[i] * commonnessDiscount;
			numDistinctRareFeatures += buckets[i] * commonnessDiscount;
		}
		double commonEventProbability = 1 - numRareEvents / totalEvents;
		double numRareFeaturesDiscount = 1 - Math.min(1, Math.pow(numDistinctRareFeatures / maxNumOfRareFeatures, RARITY_SUM_EXPONENT));
		double score = commonEventProbability * numRareFeaturesDiscount * calcCommonnessDiscounting(featureCount);
		return Math.floor(MAX_POSSIBLE_SCORE * score);
	}
}
