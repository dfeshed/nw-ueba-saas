package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;

import java.util.*;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class TimeModel implements Model {
	private static final int SMOOTHENING_DISTANCE = 10;

	private int timeResolution;
	private int bucketSize;
	private Map<Integer, Double> bucketToSmoothedCounter;
	private OccurrencesHistogram occurrencesHistogram;

	public TimeModel(int timeResolution, int bucketSize, Map<Long, Double> timeToCounter) {
		this.timeResolution = timeResolution;
		this.bucketSize = bucketSize;
		int numOfBuckets = (int) Math.ceil(timeResolution / (double) bucketSize);
		ArrayList<Double> buckets = new ArrayList<>(numOfBuckets);
		for (int i = 0; i < numOfBuckets; i++) {
			buckets.add(0d);
		}

		Set<Integer> bucketHits = new HashSet<>();
		for (Map.Entry<Long, Double> entry : timeToCounter.entrySet()) {
			double counter = entry.getValue();
			int bucketHit = getBucketIndex(entry.getKey());
			bucketHits.add(bucketHit);
			cyclicallyAddToBucket(buckets, bucketHit, counter);
			for (int distance = 1; distance <= SMOOTHENING_DISTANCE; distance++) {
				double addVal = counter * (1 - (distance - 1) / ((double) SMOOTHENING_DISTANCE));
				cyclicallyAddToBucket(buckets, bucketHit + distance, addVal);
				cyclicallyAddToBucket(buckets, bucketHit - distance, addVal);
			}
		}

		bucketToSmoothedCounter = new HashMap<>(numOfBuckets);
		for (int bucket : bucketHits) {
			bucketToSmoothedCounter.put(bucket, buckets.get(bucket));
		}
		occurrencesHistogram = new OccurrencesHistogram(new ArrayList<>(bucketToSmoothedCounter.values()));
	}

	private void cyclicallyAddToBucket(ArrayList<Double> buckets, int index, double add) {
		index = (index + buckets.size()) % buckets.size();
		buckets.set(index, buckets.get(index) + add);
	}

	private int getBucketIndex(long epochSeconds) {
		return (int) ((epochSeconds % timeResolution) / bucketSize);
	}

	@Override
	public double calculateScore(Object value) {
		int bucketIndex = getBucketIndex((Long) value);
		Double smoothedCounter = bucketToSmoothedCounter.get(bucketIndex);
		return occurrencesHistogram != null ? occurrencesHistogram.score(smoothedCounter) : 0;
	}
}
