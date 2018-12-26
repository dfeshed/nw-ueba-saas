package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

/**
 * For documentation and explanation of how this model works refer to:
 * https://fortscale.atlassian.net/wiki/display/FSC/category+rarity+model
 */
@JsonAutoDetect(
		fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class CategoryRarityModel implements PartitionedDataModel {
	private List<Double> occurrencesToNumOfPartitionsList;
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

	public void init(Map<Long, Integer> occurrencesToNumOfPartitions,
					 int numOfBuckets, long numOfPartitions, long numDistinctFeatures) {
		this.numDistinctFeatures = numDistinctFeatures;
		featureOccurrences = new HashMap<>();
		this.numOfPartitions = numOfPartitions;
		this.numOfSamples = numOfPartitions;
		this.occurrencesToNumOfPartitionsList = createListOutOfAccumulativeHistogram(occurrencesToNumOfPartitions, numOfBuckets);
	}

	private List<Double> createListOutOfAccumulativeHistogram(Map<Long, Integer> occurrencesToValueMap, int numOfBuckets){
		if(occurrencesToValueMap == null){
			return null;
		}
		List<Double> ret = new ArrayList<>(Collections.nCopies(numOfBuckets,0.0));
		int maxValue = 0;
		for (Map.Entry<Long, Integer> entry : occurrencesToValueMap.entrySet()) {
			long occurrences = entry.getKey();
			if (occurrences <= numOfBuckets) {
				int value = entry.getValue();
				maxValue = Math.max(maxValue, value);
				ret.set((int)(occurrences - 1), (double) value);
			}
		}

		if(maxValue>0) {
			for (int i = numOfBuckets-1; i > 0 && ret.get(i)==0; i--) {
				ret.set(i, (double) maxValue);
			}
		}

		return ret;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public List<Double> getOccurrencesToNumOfPartitionsList() {
		return occurrencesToNumOfPartitionsList;
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
		return new EqualsBuilder().append(that.numOfSamples, numOfSamples).append(that.numDistinctFeatures, numDistinctFeatures)
				.append(that.numOfPartitions, numOfPartitions).append(that.numberOfEntriesToSaveInModel, numberOfEntriesToSaveInModel)
				.append(that.occurrencesToNumOfPartitionsList, occurrencesToNumOfPartitionsList)
				.append(that.featureOccurrences, featureOccurrences).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(numOfSamples).append(numDistinctFeatures)
				.append(occurrencesToNumOfPartitionsList)
				.append(numOfPartitions).append(numberOfEntriesToSaveInModel).hashCode();
	}
}
