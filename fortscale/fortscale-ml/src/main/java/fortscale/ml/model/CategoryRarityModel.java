package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.util.HashMap;
import java.util.Map;

/**
 * For documentation and explanation of how this model works refer to:
 * https://fortscale.atlassian.net/wiki/display/FSC/category+rarity+model
 */
@JsonAutoDetect(
		fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class CategoryRarityModel implements Model {
	private double[] buckets;
	private long numOfSamples;
	private long numDistinctFeatures;
	private Map<String, Double> featureOccurrences;

	// The entriesToSaveInModel value from the model conf from which this model was built.
	// We store this information in the model to help us reduce the number of calls to
	// mongo in order to get feature count. I.e. there is a point to retrieve feature count from mongo
	// only if the isModelLoadedWithNumberOfEntries() is true (which means that probably there are other
	// features count that are not stored in the model because we reached the entriesToSaveInModel number when
	// building this model.
	private int numberOfEntriesToSaveInModel;

	public void init(Map<Long, Double> occurrencesToNumOfFeatures, int numOfBuckets) {
		buckets = new double[numOfBuckets];
		numOfSamples = 0;
		numDistinctFeatures = 0;
		featureOccurrences = new HashMap<>();

		for (Map.Entry<Long, Double> entry : occurrencesToNumOfFeatures.entrySet()) {
			long occurrences = entry.getKey();
			double numOfFeatures = entry.getValue();

			if (occurrences <= buckets.length) {
				buckets[(int)(occurrences - 1)] = numOfFeatures;
			}

			numOfSamples += numOfFeatures * occurrences;
			numDistinctFeatures += numOfFeatures;
		}
	}

	public double[] getBuckets() {
		return buckets;
	}

	@Override
	public long getNumOfSamples() {
		return numOfSamples;
	}

	public long getNumOfDistinctFeatures() {
		return numDistinctFeatures;
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

	public void setNumberOfEntriesToSaveInModel(int numberOfEntriesToSaveInModel) {
		this.numberOfEntriesToSaveInModel = numberOfEntriesToSaveInModel;
	}

	public int getNumberOfEntriesToSaveInModel() {
		return numberOfEntriesToSaveInModel;
	}

	public boolean isModelLoadedWithNumberOfEntries() {
		return getNumOfSavedFeatures() >= numberOfEntriesToSaveInModel;
	}
}
