package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import fortscale.ml.model.Model;
import org.springframework.util.Assert;

import javax.persistence.Transient;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
/**
 * For documentation and explanation of how this model works - refer to https://fortscale.atlassian.net/wiki/display/FSC/category+rarity+model
 */
public class CategoryRarityModel implements Model {
	private static final double MIN_POSSIBLE_SCORE = 1;
	private static final double MAX_POSSIBLE_SCORE = 100;
	private static final double RARITY_SUM_EXPONENT = 1.8;

	private double[] buckets;
	private int totalEvents;
	@JsonIgnore
	@Transient
	private int minEvents;
	@JsonIgnore
	@Transient
	private int maxNumOfRareFeatures;

	public CategoryRarityModel(int minEvents, int maxRareCount, int maxNumOfRareFeatures, Map<Integer, Double> occurrencesToNumOfFeatures) {
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

	private double calcCommonnessDiscounting(double occurrence) {
		// make sure getMaxRareCount() will be scored less than MIN_POSSIBLE_SCORE - so once we multiply
		// by MAX_POSSIBLE_SCORE (inside calculateScore function) we get a rounded score of 0
		return Sigmoid.calcLogisticFunc(
				getMaxRareCount() * 0.3333333333333333,
				getMaxRareCount(),
				(MIN_POSSIBLE_SCORE / MAX_POSSIBLE_SCORE) * 0.99999999,
				occurrence - 1);
	}

	private int getMaxRareCount() {
		return buckets.length / 2;
	}

	public Double calculateScore(Object value) {
		if (totalEvents < minEvents) {
			return null;
		}
		int featureCount = (int) value;
		Assert.isTrue(featureCount > 0, featureCount < 0 ?
				"featureCount can't be negative - you probably have a bug" : "if you're scoring a first-time-seen feature, you should pass 1 as its count");
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
