package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;
import fortscale.utils.ConversionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class TimeModel implements Model {
	private static final int SMOOTHING_DISTANCE = 10;

	private int timeResolution;
	private int bucketSize;
	private List<Double> smoothedBuckets;
	private CategoryRarityModel categoryRarityModel;

	public TimeModel(int timeResolution, int bucketSize, int minEvents, int maxRareTimestampCount, int maxNumOfRareTimestamps, Map<?, Double> timeToCounter) {
		this.timeResolution = timeResolution;
		this.bucketSize = bucketSize;

		List<Double> bucketHits = calcBucketHits(timeToCounter);
		smoothedBuckets = calcSmoothedBuckets(bucketHits);

		Map<Integer, Double> roundedSmoothedCountersThatWereHitToNumOfBuckets = IntStream.range(0, bucketHits.size())
				.filter(bucketInd -> bucketHits.get(bucketInd) > 0)
				.boxed()
				.collect(Collectors.groupingBy(
						this::getRoundedCounter,
						Collectors.reducing(
								0D,
								smoothedCounter -> 1D,
								(smoothedCounter1, smoothedCounter2) -> smoothedCounter1 + smoothedCounter2
						)));

		categoryRarityModel = new CategoryRarityModel(minEvents, maxRareTimestampCount, maxNumOfRareTimestamps, roundedSmoothedCountersThatWereHitToNumOfBuckets);
	}

	private int calcNumOfBuckets() {
		return (int) Math.ceil(timeResolution / bucketSize);
	}

	private List<Double> createInitializedBuckets() {
		return IntStream.range(0, calcNumOfBuckets())
				.map(a -> 0)
				.asDoubleStream()
				.boxed()
				.collect(Collectors.toList());
	}

	private List<Double> calcBucketHits(Map<?, Double> timeToCounter) {
		List<Double> bucketHits = createInitializedBuckets();
		for (Map.Entry<?, Double> timeAndCounter: timeToCounter.entrySet()) {
			int bucketHit = getBucketIndex(ConversionUtils.convertToLong(timeAndCounter.getKey()));
			bucketHits.set(bucketHit, bucketHits.get(bucketHit) + timeAndCounter.getValue());
		}
		return bucketHits;
	}

	private List<Double> calcSmoothedBuckets(List<Double> bucketHits) {
		List<Double> smoothedBucketHits = createInitializedBuckets();
		for (int bucketInd = 0; bucketInd < bucketHits.size(); bucketInd++) {
			if (bucketHits.get(bucketInd) > 0) {
				cyclicallyAddToBucket(smoothedBucketHits, bucketInd, bucketHits.get(bucketInd));
				for (int distance = 1; distance <= SMOOTHING_DISTANCE; distance++) {
					double addVal = bucketHits.get(bucketInd) * (1 - (distance - 1) / ((double) SMOOTHING_DISTANCE));
					cyclicallyAddToBucket(smoothedBucketHits, bucketInd + distance, addVal);
					cyclicallyAddToBucket(smoothedBucketHits, bucketInd - distance, addVal);
				}
			}
		}
		return smoothedBucketHits;
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
		int bucketInd = getBucketIndex((Long) value);
		return categoryRarityModel.calculateScore(getRoundedCounter(bucketInd) + 1);
	}

	private int getRoundedCounter(int bucketInd) {
		return smoothedBuckets.get(bucketInd).intValue();
	}
}
