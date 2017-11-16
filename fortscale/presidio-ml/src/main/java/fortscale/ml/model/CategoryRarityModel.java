package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * For documentation and explanation of how this model works refer to:
 * https://fortscale.atlassian.net/wiki/display/FSC/category+rarity+model
 */
@JsonAutoDetect(
		fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class CategoryRarityModel implements PartitionedDataModel {
	private double[] buckets;
	private long numOfSamples;
	private long numDistinctFeatures;
	private Map<String, Double> featureOccurrences;
	// number of partitions we found data on. can be used for certainty calculation.
	private long numOfPartitions;

	// The entriesToSaveInModel value from the model conf from which this model was built.
	// We store this information in the model to help us reduce the number of calls to
	// mongo in order to get feature count. I.e. there is a point to retrieve feature count from mongo
	// only if the isModelLoadedWithNumberOfEntries() is true (which means that probably there are other
	// features count that are not stored in the model because we reached the entriesToSaveInModel number when
	// building this model.
	private int numberOfEntriesToSaveInModel;

	public void init(Map<Long, Integer> occurrencesToNumOfPartitions, int numOfBuckets, long numOfPartitions, long numDistinctFeatures) {
		this.numDistinctFeatures = numDistinctFeatures;
		featureOccurrences = new HashMap<>();
		this.numOfPartitions = numOfPartitions;
		buckets = new double[numOfBuckets];

		this.numOfSamples = 0;
		for (Map.Entry<Long, Integer> entry : occurrencesToNumOfPartitions.entrySet()) {
			long occurrences = entry.getKey();
			int occurencesNumOfPartitions = entry.getValue();

			if (occurrences <= buckets.length) {
				buckets[(int)(occurrences - 1)] = occurencesNumOfPartitions;
			}
			this.numOfSamples += occurencesNumOfPartitions * occurrences;
		}
	}

	@Override
	public String toString() {
		String featureOccurencessStr="null";
		if(featureOccurrences!=null)
		{
			featureOccurencessStr = featureOccurrences.toString();
		}
		return String.format("<CategoryRarityModel: buckets=%s, numOfSamples=%d, numDistinctFeatures=%d, featureOccurrences=%s>", Arrays.toString(buckets),numOfSamples,numDistinctFeatures, featureOccurencessStr);

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

	@Override
	public long getNumOfPartitions() {
		return numOfPartitions;
	}

	public void setNumOfPartitions(long numOfPartitions) {
		this.numOfPartitions = numOfPartitions;
	}

	public Map<String, Double> getFeatureOccurrences() {
		return featureOccurrences;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CategoryRarityModel)) return false;
		CategoryRarityModel that = (CategoryRarityModel)o;
		if (numOfSamples != that.numOfSamples) return false;
		if (numDistinctFeatures != that.numDistinctFeatures) return false;
		if (numOfPartitions != that.numOfPartitions) return false;
		if (numberOfEntriesToSaveInModel != that.numberOfEntriesToSaveInModel) return false;
		if (!Arrays.equals(buckets, that.buckets)) return false;
		return featureOccurrences.equals(that.featureOccurrences);
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(buckets);
		result = 31 * result + (int)(numOfSamples ^ (numOfSamples >>> 32));
		result = 31 * result + (int)(numDistinctFeatures ^ (numDistinctFeatures >>> 32));
		result = 31 * result + featureOccurrences.hashCode();
		result = 31 * result + (int)(numOfPartitions ^ (numOfPartitions >>> 32));
		result = 31 * result + numberOfEntriesToSaveInModel;
		return result;
	}
}
