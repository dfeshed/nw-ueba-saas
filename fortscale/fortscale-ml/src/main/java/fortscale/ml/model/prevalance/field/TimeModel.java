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

	public TimeModel(int timeResolution, int bucketSize, List<Long> times) {
		this.timeResolution = timeResolution;
		this.bucketSize = bucketSize;
		int numOfBuckets = (int) Math.ceil(timeResolution / (double) bucketSize);
		ArrayList<Double> buckets = new ArrayList<>(numOfBuckets);
		for (int i = 0; i < numOfBuckets; i++) {
			buckets.add(0d);
		}

		bucketToSmoothedCounter = new HashMap<>(numOfBuckets);
		Set<Integer> bucketHits = new HashSet<>();
		for (long time : times) {
			int pivot = getBucketIndex(time);
			bucketHits.add(pivot);
			double val = buckets.get(pivot) + 1;
			buckets.set(pivot, val);
			int upIndex = pivot + 1;
			int downIndex = pivot - 1 + numOfBuckets;
			for (int i = 0; i < SMOOTHENING_DISTANCE; i++,upIndex++,downIndex--) {
				double addVal = 1 - i/((double) SMOOTHENING_DISTANCE);
				int index = upIndex % numOfBuckets;
				val = buckets.get(index) + addVal;
				buckets.set(index, val);

				index = downIndex % numOfBuckets;
				val = buckets.get(index) + addVal;
				buckets.set(index, val);
			}
		}

		for (int bucket : bucketHits) {
			bucketToSmoothedCounter.put(bucket, buckets.get(bucket));
		}
		occurrencesHistogram = new OccurrencesHistogram(new ArrayList<>(bucketToSmoothedCounter.values()));
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
