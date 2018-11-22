package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.google.common.base.Joiner;
import fortscale.common.feature.CategoricalFeatureValue;
import fortscale.ml.model.builder.CategoryRarityModelBuilder;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.ml.model.metrics.TimeModelBuilderMetricsContainer;
import fortscale.ml.model.metrics.TimeModelBuilderPartitionsMetricsContainer;
import fortscale.utils.ConversionUtils;
import fortscale.utils.data.Pair;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@JsonAutoDetect(
		fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class TimeModel implements PartitionedDataModel {
	private static final int SMOOTHING_DISTANCE = 10;
	private static final double SMOOTH_BUCKET_MAX_VALUE = 1;

	private Integer timeResolution;
	private Integer bucketSize;
	private List<Double> smoothedBuckets;
	private CategoryRarityModel categoryRarityModel;
	private Long numOfSamples;

	public void init(int timeResolution, int bucketSize, int maxRareTimestampCount, Map<?, Double> timeToCounter, long numberOfPartitions,
					 TimeModelBuilderMetricsContainer timeModelBuilderMetricsContainer, TimeModelBuilderPartitionsMetricsContainer timeModelBuilderPartitionsMetricsContainer, CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer) {
		Assert.isTrue(timeResolution % bucketSize == 0,"timeResolution must be multiplication of bucketSize");

		this.timeResolution = timeResolution;
		this.bucketSize = bucketSize;
		Map<Long, Double> convertedTimeToCounter = castTimeCounterKeyToLong(timeToCounter);
		Map<Long/*resolutionId*/, Map<Long/*time*/, Double /*counter*/>> resolutionTimeCounters = groupCountersByResolutionId(convertedTimeToCounter);
		Map</*resolutionId*/Long,/*bucketHits*/ List<Double>> resolutionIdToBucketHits = createResolutionIdToBucketHitsMap(resolutionTimeCounters);
		numOfSamples = getNumOfSamples(resolutionIdToBucketHits);
		List<Double> bucketHits = mergeBuckets(resolutionIdToBucketHits);

		// create smoothed buckets for each resolution id
		Map<Long, List<Double>> resolutionIdToSmoothedBuckets = createResolutionIdToSmoothedBucketsMap(resolutionIdToBucketHits);

		// fill smooth buckets and smoothedBucketsThatWereHitToNubOfBuckets (category rarity model data)
		smoothedBuckets = createInitializedBuckets();
		Map<Pair<String, Instant>/*i.e. smoothedbucket that was hit ,date of activity*/, /*1*/Double> smoothedBucketsThatWereHitToNumOfBuckets = new HashMap<>();
		mergeSmoothBuckets(resolutionIdToSmoothedBuckets, smoothedBucketsThatWereHitToNumOfBuckets,bucketHits);

		// build categorical model
		buildCategoryRarityModel(maxRareTimestampCount, categoryRarityModelBuilderMetricsContainer, smoothedBucketsThatWereHitToNumOfBuckets);

		// fill metrics data
		long numDistinctFeatures = bucketHits.stream().filter(hits -> hits > 0).count();

		int numOfDistinctSamples = 0;
		for(Object time : timeToCounter.keySet()){
			timeModelBuilderPartitionsMetricsContainer.incNumOfUsers(ConversionUtils.convertToLong(time), timeResolution);
			numOfDistinctSamples++;
		}

		long numOfSmoothedBuckets = smoothedBuckets.stream().filter(smooth -> smooth > 0).count();
		timeModelBuilderMetricsContainer.updateMetric(numOfDistinctSamples, numberOfPartitions, categoryRarityModel.getBuckets(), numDistinctFeatures, numOfSmoothedBuckets);

	}

	private void buildCategoryRarityModel(int maxRareTimestampCount, CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer, Map<Pair<String, Instant>, Double> smoothedBucketsThatWereHitToNumOfBuckets) {
		CategoryRarityModelBuilderConf categoryRarityModelBuilderConf = new CategoryRarityModelBuilderConf(maxRareTimestampCount * 2);
		categoryRarityModelBuilderConf.setPartitionsResolutionInSeconds(timeResolution);
		categoryRarityModelBuilderConf.setEntriesToSaveInModel(getNumOfBuckets());

		CategoryRarityModelBuilder categoryRarityModelBuilder = new CategoryRarityModelBuilder(categoryRarityModelBuilderConf, categoryRarityModelBuilderMetricsContainer);
		CategoricalFeatureValue categoricalModelData = new CategoricalFeatureValue(FixedDurationStrategy.fromSeconds(timeResolution));
		categoricalModelData.setHistogram(smoothedBucketsThatWereHitToNumOfBuckets);
		categoryRarityModel = (CategoryRarityModel) categoryRarityModelBuilder.build(categoricalModelData);
	}

	private void mergeSmoothBuckets(Map<Long, List<Double>> resolutionIdToSmoothedBuckets, Map<Pair<String, Instant>, Double> smoothedBucketsThatWereHitToNumOfBuckets, List<Double> bucketHits) {
		for (Map.Entry<Long, List<Double>> entry:resolutionIdToSmoothedBuckets.entrySet()){
			List<Double> resolutionIdBucketHits = entry.getValue();
			Long date = entry.getKey()*timeResolution;
			for (int i = 0; i < resolutionIdBucketHits.size(); i++) {
				Double resolutionIdBucketHit = resolutionIdBucketHits.get(i);
				smoothedBuckets.set(i,smoothedBuckets.get(i) + resolutionIdBucketHit);
				if( bucketHits.get(i)>0 && resolutionIdBucketHit >0)
				{
					smoothedBucketsThatWereHitToNumOfBuckets.put(new Pair<>(String.valueOf(i),Instant.ofEpochSecond(date)),SMOOTH_BUCKET_MAX_VALUE);
				}
			}
		}
	}

	private List<Double> mergeBuckets(Map<Long, List<Double>> resolutionIdToBucketHits) {
		List<Double> result = createInitializedBuckets();
		for (Map.Entry<Long, List<Double>> entry:resolutionIdToBucketHits.entrySet()){
			List<Double> bucketHits = entry.getValue();
			for (int i = 0; i < bucketHits.size(); i++) {
				result.set(i,result.get(i) + bucketHits.get(i));
			}
		}

		return result;
	}

	private Map<Long, Double> castTimeCounterKeyToLong(Map<?, Double> timeToCounter) {
		return timeToCounter.entrySet().stream().collect(Collectors.toMap(entry-> ConversionUtils.convertToLong(entry.getKey()), Map.Entry::getValue));
	}

	private Map<Long, List<Double>> createResolutionIdToSmoothedBucketsMap(Map<Long, List<Double>> resolutionIdToBucketHits) {
		return resolutionIdToBucketHits.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e->calcSmoothedBuckets(e.getValue())));
	}

	/**
	 *
	 * @return the sum of bucket hits at {@param resolutionBucketHits}
	 */
	public long getNumOfSamples(Map<Long, List<Double>> resolutionBucketHits) {
		return resolutionBucketHits.size();//long) resolutionBucketHits.values().stream().flatMap(List::stream).mapToDouble(Double::doubleValue).sum();
	}

	/**
	 *
	 * @param timeToCounter map containing sum of interaction in a specific time
	 * @return Map with key: resolution id (i.e. first/second-day identifier) value: key:time, value: bucket hits in the resolution (in the same day)
	 */
	private Map<Long, List<Double>> createResolutionIdToBucketHitsMap(Map<Long, Map<Long, Double>> timeToCounter) {
		Map<Long, List<Double>> result = new HashMap<>();

		for (Map.Entry<Long, Map<Long, Double>> entry :
				timeToCounter.entrySet()) {
			List<Double> bucketHits = calcBucketHits(entry.getValue());
			result.put(entry.getKey(), bucketHits);
		}

		return result;
	}

	/**
	 * gather counters from the same resolution
	 * @param timeToCounter map containing sum of interaction in a specific time
	 * @return Map with key: resolution id (i.e. first/second-day identifier) value: all the active buckets in the resolution (in the same day)
	 */
	private Map<Long, Map<Long, Double>> groupCountersByResolutionId(Map<Long, Double> timeToCounter) {
		Map<Long, Map<Long, Double>> resoutlionTimeCounters = new HashMap<>();

		for (Map.Entry<Long, Double> entry : timeToCounter.entrySet()) {
			Long timeResolutionId = entry.getKey() / timeResolution;
			Map<Long, Double> timeCounters = resoutlionTimeCounters.get(timeResolutionId);
			if (timeCounters == null) {
				timeCounters = new HashMap<>();
				resoutlionTimeCounters.put(timeResolutionId, timeCounters);
			}
			timeCounters.put(entry.getKey(), entry.getValue());
		}
		return resoutlionTimeCounters;
	}


	private List<Double> createInitializedBuckets() {
		int numOfBuckets = getNumOfBuckets();
		return new ArrayList<>(Collections.nCopies(numOfBuckets, 0D));
	}

	private int getNumOfBuckets() {
		return timeResolution / bucketSize;
	}

	private List<Double> calcBucketHits(Map<Long, Double> timeToCounter) {
		List<Double> bucketHits = createInitializedBuckets();
		for (Map.Entry<Long, Double> timeAndCounter: timeToCounter.entrySet()) {
			int bucketHit = getBucketIndex(timeAndCounter.getKey());
			if(timeAndCounter.getValue()>=1)
			{
				bucketHits.set(bucketHit, 1D);
			}
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
			double addVal = hits * Sigmoid.calcLogisticFunc(
					smoothingDistance * 0.5, smoothingDistance, 0.1 / hits, distance);
			cyclicallyAddToBucket(smoothedBucketHits, bucketInd + distance, addVal);
			cyclicallyAddToBucket(smoothedBucketHits, bucketInd - distance, addVal);
		}
	}

	private void cyclicallyAddToBucket(List<Double> buckets, int index, double add) {
		index = (index + buckets.size()) % buckets.size();
		buckets.set(index, Math.min(buckets.get(index) + add, SMOOTH_BUCKET_MAX_VALUE));
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


	public Double getDoubleSmoothedTimeCounter(long time) {
		return smoothedBuckets.get(getBucketIndex(time));
	}


	@Override
	public long getNumOfSamples() {
		return numOfSamples;
	}

	@Override
	public String toString() {

		String smoothedBucketsStr="null";
		if(smoothedBuckets!=null) {

			smoothedBucketsStr = Joiner.on(",").join(smoothedBuckets);
		}
		return String.format("<TimeModel: timeResolution=%d bucketSize=%d smoothedBuckets=%s numOfSamples=%d categoryRarityModel=%s>", timeResolution,bucketSize,smoothedBucketsStr,numOfSamples,categoryRarityModel.toString());
	}

	public CategoryRarityModel getCategoryRarityModel() {
		return categoryRarityModel;
	}

	@Override
	public long getNumOfPartitions() {
		return categoryRarityModel.getNumOfPartitions();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TimeModel)) return false;
		TimeModel timeModel = (TimeModel)o;
		if (!timeResolution.equals(timeModel.timeResolution)) return false;
		if (!bucketSize.equals(timeModel.bucketSize)) return false;
		if (!numOfSamples.equals(timeModel.numOfSamples)) return false;
		if (!smoothedBuckets.equals(timeModel.smoothedBuckets)) return false;
		return categoryRarityModel.equals(timeModel.categoryRarityModel);
	}

	@Override
	public int hashCode() {
		int result = timeResolution;
		result = 31 * result + bucketSize;
		result = 31 * result + smoothedBuckets.hashCode();
		result = 31 * result + categoryRarityModel.hashCode();
		result = 31 * result + (int)(numOfSamples ^ (numOfSamples >>> 32));
		return result;
	}
}
