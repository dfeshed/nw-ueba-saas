package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.util.*;

/**
 * For documentation and explanation of how this model works refer to:
 * https://fortscale.atlassian.net/wiki/display/FSC/category+rarity+model
 */
@JsonAutoDetect(
		fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class CategoryRarityModel implements PartitionedDataModel {
	private List<Double> buckets;
	private Long numOfSamples;
	private Long numDistinctFeatures;
	private Map<String, Double> featureOccurrences;
	// number of partitions we found data on. can be used for certainty calculation.
	private Long numOfPartitions;

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
		this.numOfSamples = numOfPartitions;
		buckets = new ArrayList<>(Collections.nCopies(numOfBuckets,0.0));

		int occurrencesNumOfPartitionsMax = 0;
		for (Map.Entry<Long, Integer> entry : occurrencesToNumOfPartitions.entrySet()) {
			long occurrences = entry.getKey();
			if (occurrences <= buckets.size()) {
				int occurrencesNumOfPartitions = entry.getValue();
				occurrencesNumOfPartitionsMax = Math.max(occurrencesNumOfPartitionsMax, occurrencesNumOfPartitions);
				buckets.set((int)(occurrences - 1), (double) occurrencesNumOfPartitions);
			}
		}

		if(occurrencesNumOfPartitionsMax>0) {
			for (int i = numOfBuckets-1; i > 0 && buckets.get(i)==0; i--) {
				if (buckets.get(i) == 0) {
					buckets.set(i, (double) occurrencesNumOfPartitionsMax);
				}
			}
		}
	}

	@Override
	public String toString() {
		String featureOccurencessStr="null";
		if(featureOccurrences!=null)
		{
			featureOccurencessStr = featureOccurrences.toString();
		}
		return String.format("<CategoryRarityModel: buckets=%s, numOfSamples=%d, numDistinctFeatures=%d, featureOccurrences=%s>", buckets.toString(),numOfSamples,numDistinctFeatures, featureOccurencessStr);

	}

	public List<Double> getBuckets() {
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
		if (!numOfSamples.equals(that.numOfSamples)) return false;
		if (!numDistinctFeatures.equals(that.numDistinctFeatures)) return false;
		if (!numOfPartitions.equals(that.numOfPartitions)) return false;
		if (numberOfEntriesToSaveInModel != that.numberOfEntriesToSaveInModel) return false;
		if (buckets == null && that.buckets != null) return false;
		if (buckets != null && that.buckets == null) return false;
		if (buckets != that.buckets && !buckets.equals(that.buckets)) return false;
		return featureOccurrences.equals(that.featureOccurrences);
	}

	@Override
	public int hashCode() {
		int result =  buckets.hashCode();
		result = 31 * result + (int)(numOfSamples ^ (numOfSamples >>> 32));
		result = 31 * result + (int)(numDistinctFeatures ^ (numDistinctFeatures >>> 32));
		result = 31 * result + featureOccurrences.hashCode();
		result = 31 * result + (int)(numOfPartitions ^ (numOfPartitions >>> 32));
		result = 31 * result + numberOfEntriesToSaveInModel;
		return result;
	}
}
