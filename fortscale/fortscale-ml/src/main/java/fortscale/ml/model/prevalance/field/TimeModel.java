package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Model;
import fortscale.utils.ConversionUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
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
	private long numOfSamples;

	public void init(int timeResolution, int bucketSize, int maxRareTimestampCount, Map<?, Double> timeToCounter) {
		Assert.isTrue(timeResolution % bucketSize == 0);

		this.timeResolution = timeResolution;
		this.bucketSize = bucketSize;

		numOfSamples = (long) timeToCounter.values().stream().mapToDouble(Double::doubleValue).sum();

		List<Double> bucketHits = calcBucketHits(timeToCounter);
		smoothedBuckets = calcSmoothedBuckets(bucketHits);

		Map<Long, Double> roundedSmoothedCountersThatWereHitToNumOfBuckets = IntStream.range(0, bucketHits.size())
				.filter(bucketInd -> bucketHits.get(bucketInd) > 0)
				.boxed()
				.collect(Collectors.groupingBy(
						this::getRoundedCounter,
						Collectors.reducing(
								0D,
								smoothedCounter -> 1D,
								(smoothedCounter1, smoothedCounter2) -> smoothedCounter1 + smoothedCounter2
						)));

		categoryRarityModel = new CategoryRarityModel();
		categoryRarityModel.init(roundedSmoothedCountersThatWereHitToNumOfBuckets, maxRareTimestampCount * 2);
	}

	private List<Double> createInitializedBuckets() {
		int numOfBuckets = timeResolution / bucketSize;
		return new ArrayList<>(Collections.nCopies(numOfBuckets, 0D));
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
			double hits = bucketHits.get(bucketInd);
			if (hits > 0) {
				addSmoothedHits(smoothedBucketHits, bucketInd, hits, SMOOTHING_DISTANCE);
			}
		}
		return smoothedBucketHits;
	}

	private void addSmoothedHits(List<Double> smoothedBucketHits, int bucketInd, double hits, int smoothingDistance) {
		smoothingDistance = Math.min(smoothingDistance, (smoothedBucketHits.size() - 1) / 2);
		cyclicallyAddToBucket(smoothedBucketHits, bucketInd, hits);
		for (int distance = 1; distance <= smoothingDistance; distance++) {
			double addVal = hits * Sigmoid.calcLogisticFunc(smoothingDistance * 0.5, smoothingDistance, 0.1 / hits, distance);
			cyclicallyAddToBucket(smoothedBucketHits, bucketInd + distance, addVal);
			cyclicallyAddToBucket(smoothedBucketHits, bucketInd - distance, addVal);
		}
	}

	private void cyclicallyAddToBucket(List<Double> buckets, int index, double add) {
		index = (index + buckets.size()) % buckets.size();
		buckets.set(index, buckets.get(index) + add);
	}

	private int getBucketIndex(long epochSeconds) {
		return (int) ((epochSeconds % timeResolution) / bucketSize);
	}

	private long getRoundedCounter(int bucketInd) {
		return smoothedBuckets.get(bucketInd).longValue();
	}

	public long getSmoothedTimeCounter(long time) {
		return getRoundedCounter(getBucketIndex(time));
	}

	@Override
	public long getNumOfSamples() {
		return numOfSamples;
	}

	public CategoryRarityModel getCategoryRarityModel() {
		return categoryRarityModel;
	}
}
