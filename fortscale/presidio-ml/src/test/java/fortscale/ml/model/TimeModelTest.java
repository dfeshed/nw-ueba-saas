package fortscale.ml.model;

import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.ml.model.metrics.TimeModelBuilderMetricsContainer;
import fortscale.ml.model.metrics.TimeModelBuilderPartitionsMetricsContainer;
import fortscale.ml.scorer.algorithm.AbstractScorerTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class TimeModelTest extends AbstractScorerTest {

	private TimeModelBuilderMetricsContainer timeModelBuilderMetricsContainer = mock(TimeModelBuilderMetricsContainer.class);
	private TimeModelBuilderPartitionsMetricsContainer timeModelBuilderPartitionsMetricsContainer = mock(TimeModelBuilderPartitionsMetricsContainer.class);
	private CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer = mock(CategoryRarityModelBuilderMetricsContainer.class);

	private TimeModel createModel(int timeResolution, int bucketSize, int maxRareTimestampCount, Long... times) {
		Map<Long, Double> timeToCounter = new HashMap<>();

		for (int i=0; i< times.length;i++) {
			timeToCounter.put(times[i]+ i*timeResolution,1D);
		}

		TimeModel timeModel = new TimeModel();
		timeModel.init(timeResolution, bucketSize, maxRareTimestampCount, timeToCounter, 1, timeModelBuilderMetricsContainer, timeModelBuilderPartitionsMetricsContainer, categoryRarityModelBuilderMetricsContainer);
		return timeModel;
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfTimeResolutionIsNotMultiplicationOfBucketSize() {
		createModel(100, 99, 10);
	}

	@Test
	public void modelWithOneTimestamp() {
		int timeResolution = 86400;
		int bucketSize = 1;
		int maxRareTimestampCount = 10;
		long time = 1;
		TimeModel model = createModel(timeResolution, bucketSize, maxRareTimestampCount, time);

		Assert.assertEquals(1, model.getNumOfSamples());
		Assert.assertEquals(1, model.getCategoryRarityModel().getNumOfSamples());
		List<Double> buckets = model.getCategoryRarityModel().getBuckets();
		Assert.assertEquals(1,         buckets.stream().mapToDouble(Double::doubleValue).sum()	, 0.001);
		Assert.assertEquals(1, buckets.get(0), 0.001);
	}

	@Test
	public void modelWithTwoTimestampsInSameBucket() {
		int timeResolution = 86400;
		int bucketSize = 10;
		int maxRareTimestampCount = 10;
		long time1 = 1;
		long time2 = 2;
		TimeModel model = createModel(timeResolution, bucketSize, maxRareTimestampCount, time1, time2);

		Assert.assertEquals(2, model.getNumOfSamples());
		Assert.assertEquals(2, model.getCategoryRarityModel().getNumOfSamples());
		List<Double> buckets = model.getCategoryRarityModel().getBuckets();
        Assert.assertEquals(1,         buckets.stream().mapToDouble(Double::doubleValue).sum(), 0.001);
		Assert.assertEquals(1, buckets.get(1), 0.001);
	}

	@Test
	public void modelWithTwoTimestampsInDifferentBuckets() {
		int timeResolution = 86400;
		int bucketSize = 10*60;
		int maxRareTimestampCount = 10;
		long time1 = 1;
		long time2 = timeResolution+102+bucketSize;
		TimeModel model = createModel(timeResolution, bucketSize, maxRareTimestampCount, time1, time2);

		Assert.assertEquals(2, model.getNumOfSamples());
		Assert.assertEquals(4, model.getCategoryRarityModel().getNumOfSamples());
        List<Double> buckets = model.getCategoryRarityModel().getBuckets();
        Assert.assertEquals(2,         buckets.stream().mapToDouble(Double::doubleValue).sum() ,0.001);
		Assert.assertEquals(2, buckets.get(1), 0.001);
	}

	@Test
	public void testSmoothingWithOneTimestamp() {
		int timeResolution = 86400;
		int bucketSize = 1;
		int maxRareTimestampCount = 10;
		long time = 1;
		TimeModel model = createModel(timeResolution, bucketSize, maxRareTimestampCount, time);

		Assert.assertEquals(1, model.getSmoothedTimeCounter(time));
	}

	@Test
	public void testSmoothingWithOneTimestampAndOnlyOneBucket() {
		int timeResolution = 86400;
		int bucketSize = timeResolution;
		int maxRareTimestampCount = 10;
		long time = 1;
		TimeModel model = createModel(timeResolution, bucketSize, maxRareTimestampCount, time);

		Assert.assertEquals(1, model.getSmoothedTimeCounter(time));
	}

	@Test
	public void testSmoothingIsCyclic() {
		int timeResolution = 86400;
		int bucketSize = 1;
		int maxRareTimestampCount = 10;
		long time = 1;
		TimeModel model = createModel(timeResolution, bucketSize, maxRareTimestampCount, time);

		Assert.assertEquals(model.getSmoothedTimeCounter(time), model.getSmoothedTimeCounter(time + timeResolution));
	}

	private boolean isMonotonic(List<Long> smoothedCounters, int startInclusive, int endExclusive, boolean shouldIncrease) {
		return IntStream.range(startInclusive, endExclusive).mapToObj(i -> smoothedCounters.get(i) - smoothedCounters.get(i - 1)).allMatch(diff -> diff * (shouldIncrease ? 1 : -1) >= 0);
	}

	@Test
	public void testSmoothingSymmetricallyAcross10Buckets() {
		int timeResolution = 86400;
		int bucketSize = 432;
		int maxRareTimestampCount = 10;
		long time = timeResolution / 2;
		TimeModel model = createModel(
				timeResolution,
				bucketSize,
				maxRareTimestampCount,
				LongStream.range(0, 5).boxed().map(l -> time).collect(Collectors.toList()).toArray(new Long[0])
		);

		List<Long> smoothedCounters = IntStream.range(0, timeResolution).mapToObj(model::getSmoothedTimeCounter).collect(Collectors.toList());
		Assert.assertTrue("smoothed counters should increase from the left side", isMonotonic(smoothedCounters, 1, (int) time, true));
		Assert.assertTrue("smoothed counters should decrease from the right side", isMonotonic(smoothedCounters, (int) (time + 1), timeResolution, false));
		Assert.assertEquals("smoothing distance should be at most 10 buckets", 0, smoothedCounters.get((int) (((time / bucketSize) + 10) * bucketSize)), 0.001);
		for (int i = 1; i < 10; i++) {
			int ithBucketFromTheRight = (int) (((time / bucketSize) + i) * bucketSize);
			int ithBucketFromTheLeft = (int) (((time / bucketSize) - i) * bucketSize);
			Assert.assertEquals(String.format("smoothed counter in bucket #%d (%d) differs from smoothed counter in bucket #%d (%d)",
					ithBucketFromTheLeft, smoothedCounters.get(ithBucketFromTheRight), ithBucketFromTheRight, smoothedCounters.get(ithBucketFromTheRight)),
					smoothedCounters.get(ithBucketFromTheRight),
					smoothedCounters.get(ithBucketFromTheLeft));
		}
	}

	@Test
	public void testSmoothingTwoBucketsAddsUp() {
		int timeResolution = 86400;
		int bucketSize = 5;
		int maxRareTimestampCount = 10;
		long time1 = 0;
		long time2 = 6 * bucketSize;
		int numOfSamplesInEachTimestamp = 500;
		TimeModel modelWithTime1 = createModel(
				timeResolution,
				bucketSize,
				maxRareTimestampCount,
				LongStream.range(0, numOfSamplesInEachTimestamp).boxed().map(l -> time1).collect(Collectors.toList()).toArray(new Long[0])
		);
		TimeModel modelWithTime2 = createModel(
				timeResolution,
				bucketSize,
				maxRareTimestampCount,
				LongStream.range(0, numOfSamplesInEachTimestamp).boxed().map(l -> time2).collect(Collectors.toList()).toArray(new Long[0])
		);
		TimeModel modelWithTime1AndTime2 = createModel(
				timeResolution,
				bucketSize,
				maxRareTimestampCount,
				LongStream.range(0, numOfSamplesInEachTimestamp * 2).boxed().map(l -> l % 2 == 0 ? time1 : time2).collect(Collectors.toList()).toArray(new Long[0])
		);

		long timeInMiddle = (time1 + time2) / 2;
		long smoothedCounterFromModel1 = modelWithTime1.getSmoothedTimeCounter(timeInMiddle);
		long smoothedCounterFromModel2 = modelWithTime2.getSmoothedTimeCounter(timeInMiddle);
		long smoothedCounterFromModel1And2 = modelWithTime1AndTime2.getSmoothedTimeCounter(timeInMiddle);

		Assert.assertEquals(smoothedCounterFromModel1 + smoothedCounterFromModel2, smoothedCounterFromModel1And2, 0.00001);
	}
}
