package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.common.feature.Feature;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Map;

/**
 * For documentation and explanation of how this model works - refer to https://fortscale.atlassian.net/wiki/display/FSC/category+rarity+model
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class CategoryRarityModel implements Model{

	public static final int NUM_OF_BUCKETS = 100;

	private double[] buckets;
	private long numOfSamples;
	private long numDistinctRareFeatures;


	public void init(Map<Long, Double> occurrencesToNumOfFeatures) {
		buckets = new double[NUM_OF_BUCKETS];
		numOfSamples = 0;
		for (Map.Entry<Long, Double> entry : occurrencesToNumOfFeatures.entrySet()) {
			long occurrences = entry.getKey();
			double numOfFeatures = entry.getValue();
			if (occurrences <= buckets.length) {
				buckets[(int) (occurrences - 1)] = numOfFeatures;
			}
			numOfSamples += numOfFeatures * occurrences;
			numDistinctRareFeatures += numOfFeatures;
		}
	}

	@Override
	public long getNumOfSamples() {
		return numOfSamples;
	}


	public long getNumOfDistinctRareFeatures() {
		return numDistinctRareFeatures;
	}

	public double[] getBuckets() {
		return buckets;
	}

}
