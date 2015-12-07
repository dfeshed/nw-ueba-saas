package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class TimeModel implements Model {
	public static final String MODEL_TYPE = "time_model";
	private static final long serialVersionUID = 5006807217250354329L;
	private static final int SMOOTHING_DISTANCE = 10;

	private int timeResolution;
	private int bucketSize;
	private List<Double> smoothedCounterBuckets;
	private OccurrencesHistogram occurrencesHistogram;

	public TimeModel(int timeResolution, int bucketSize, Map<Long, Double> timeToCounter) {
		this.timeResolution = timeResolution;
		this.bucketSize = bucketSize;
		int numOfBuckets = (int) Math.ceil(timeResolution / (double) bucketSize);
		smoothedCounterBuckets = new ArrayList<>(numOfBuckets);
		List<Boolean> bucketHits = new ArrayList<>(numOfBuckets);
		for (int i = 0; i < numOfBuckets; i++) {
			smoothedCounterBuckets.add(0d);
			bucketHits.add(false);
		}

		for (Map.Entry<Long, Double> entry : timeToCounter.entrySet()) {
			double counter = entry.getValue();
			int bucketHit = getBucketIndex(entry.getKey());
			bucketHits.set(bucketHit, true);
			cyclicallyAddToBucket(smoothedCounterBuckets, bucketHit, counter);
			for (int distance = 1; distance <= SMOOTHING_DISTANCE; distance++) {
				double addVal = counter * (1 - (distance - 1) / ((double)SMOOTHING_DISTANCE));
				cyclicallyAddToBucket(smoothedCounterBuckets, bucketHit + distance, addVal);
				cyclicallyAddToBucket(smoothedCounterBuckets, bucketHit - distance, addVal);
			}
		}

		List<Double> smoothedCountersThatWereHit = new ArrayList<>(numOfBuckets);
		for (int i = 0; i < numOfBuckets; i++) {
			if (bucketHits.get(i)) {
				smoothedCountersThatWereHit.add(smoothedCounterBuckets.get(i));
			}
		}
		occurrencesHistogram = new OccurrencesHistogram(smoothedCountersThatWereHit);
	}

	private void cyclicallyAddToBucket(List<Double> buckets, int index, double add) {
		index = (index + buckets.size()) % buckets.size();
		buckets.set(index, buckets.get(index) + add);
	}

	private int getBucketIndex(long epochSeconds) {
		return (int) ((epochSeconds % timeResolution) / bucketSize);
	}

	@Override
	public Double calculateScore(Object value) {
		int bucketIndex = getBucketIndex((Long) value);
		Double smoothedCounter = smoothedCounterBuckets.get(bucketIndex);
		return occurrencesHistogram.score(smoothedCounter);
	}
}
