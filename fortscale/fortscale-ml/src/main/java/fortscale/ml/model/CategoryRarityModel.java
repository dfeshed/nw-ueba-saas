package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.util.HashMap;
import java.util.Map;

/**
 * For documentation and explanation of how this model works refer to:
 * https://fortscale.atlassian.net/wiki/display/FSC/category+rarity+model
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class CategoryRarityModel implements Model {
	private double[] buckets;
	private long numOfSamples;
	private long numDistinctRareFeatures;
	private Map<String, Double> featureOccurrences;

	public void init(Map<Long, Double> occurrencesToNumOfFeatures, int numOfBuckets) {
		buckets = new double[numOfBuckets];
		numOfSamples = 0;
		numDistinctRareFeatures = 0;
		featureOccurrences = new HashMap<>();

		for (Map.Entry<Long, Double> entry : occurrencesToNumOfFeatures.entrySet()) {
			long occurrences = entry.getKey();
			double numOfFeatures = entry.getValue();

			if (occurrences <= buckets.length) {
				buckets[(int)(occurrences - 1)] = numOfFeatures;
			}

			numOfSamples += numOfFeatures * occurrences;
			numDistinctRareFeatures += numOfFeatures;
		}
	}

	public double[] getBuckets() {
		return buckets;
	}

	@Override
	public long getNumOfSamples() {
		return numOfSamples;
	}

	public long getNumOfDistinctRareFeatures() {
		return numDistinctRareFeatures;
	}

	public Double getFeatureCount(String feature) {
		if (feature == null || featureOccurrences == null) {
			return null;
		} else {
			return featureOccurrences.get(feature);
		}
	}

	public void setFeatureCount(String feature, double counter) {
		if (feature == null || counter < 0) return;
		if (featureOccurrences == null) featureOccurrences = new HashMap<>();
		featureOccurrences.put(feature, counter);
	}

	public int getNumOfSavedFeatures() {
		if (featureOccurrences == null) return 0;
		return featureOccurrences.size();
	}
}
