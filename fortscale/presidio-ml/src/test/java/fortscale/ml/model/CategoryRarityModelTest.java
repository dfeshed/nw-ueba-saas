package fortscale.ml.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;

@RunWith(JUnit4.class)
public class CategoryRarityModelTest {
	private static final int NUM_OF_BUCKETS = 15;
	private static final int NUMBER_OF_PARTITIONS = 1;
	private Map<Long, Integer> createOccurrencesToNumOfPartitions(long... occurrences) {
		return LongStream.of(occurrences)
				.boxed()
				.collect(Collectors.groupingBy(
						o -> o,
						Collectors.reducing(
								0,
								o -> 1,
								(o1, o2) -> o1 + o2
						)
				));
	}

	@Test
	public void modelWithOneFeatureOneOccurrence() {
		CategoryRarityModel model = new CategoryRarityModel();
		Map<Long, Integer> occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(1);
		model.init(occurrencesToNumOfPartitions, NUM_OF_BUCKETS, NUMBER_OF_PARTITIONS,1);
		List<Double> buckets = model.getBuckets();
		Assert.assertEquals(1, buckets.stream().mapToDouble(Double::doubleValue).sum(), 0.001);
		Assert.assertEquals(1, buckets.get(0), 0.001);
		Assert.assertEquals(1, model.getNumOfSamples());
		Assert.assertEquals(1, model.getNumOfDistinctFeatures());
	}

	@Test
	public void modelWithOneFeatureTwoOccurrences() {
		CategoryRarityModel model = new CategoryRarityModel();
		Map<Long, Integer> occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(2);
		int numDistinctFeatures = 1;
		model.init(occurrencesToNumOfPartitions, NUM_OF_BUCKETS, NUMBER_OF_PARTITIONS, numDistinctFeatures);
		List<Double> buckets = model.getBuckets();
		Assert.assertEquals(1, buckets.stream().mapToDouble(Double::doubleValue).sum(), 0.001);
		Assert.assertEquals(1, buckets.get(1), 0.001);
		Assert.assertEquals(2, model.getNumOfSamples());
		Assert.assertEquals(1, model.getNumOfDistinctFeatures());
	}

	@Test
	public void modelWithTwoFeaturesOneOccurrence() {
		CategoryRarityModel model = new CategoryRarityModel();
		Map<Long, Integer> occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(1,1);
		int numDistinctFeatures = 2;
		model.init(occurrencesToNumOfPartitions, NUM_OF_BUCKETS, NUMBER_OF_PARTITIONS, numDistinctFeatures);
		List<Double> buckets = model.getBuckets();
		Assert.assertEquals(2, buckets.stream().mapToDouble(Double::doubleValue).sum(), 0.001);
		Assert.assertEquals(2, buckets.get(0), 0.001);
		Assert.assertEquals(2, model.getNumOfSamples());
		Assert.assertEquals(2, model.getNumOfDistinctFeatures());
	}

	@Test
	public void modelWithTwoFeaturesTwoOccurrencesOneFeatureOneOccurrence() {
		CategoryRarityModel model = new CategoryRarityModel();
		Map<Long, Integer> occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(2, 2, 1);
		int numDistinctFeatures = 3;
		model.init(occurrencesToNumOfPartitions, NUM_OF_BUCKETS, NUMBER_OF_PARTITIONS, numDistinctFeatures);
		List<Double> buckets = model.getBuckets();
		Assert.assertEquals(3, buckets.stream().mapToDouble(Double::doubleValue).sum(), 0.001);
		Assert.assertEquals(1, buckets.get(0), 0.001);
		Assert.assertEquals(2, buckets.get(1), 0.001);
		Assert.assertEquals(5, model.getNumOfSamples());
		Assert.assertEquals(3, model.getNumOfDistinctFeatures());
	}

	@Test
	public void shouldStoreOnlyNumOfBucketsBuckets() {
		CategoryRarityModel model = new CategoryRarityModel();
		Map<Long, Integer> occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(NUM_OF_BUCKETS);
		int numDistinctFeatures = 1;
		model.init(occurrencesToNumOfPartitions, NUM_OF_BUCKETS, NUMBER_OF_PARTITIONS, numDistinctFeatures);
		List<Double> buckets = model.getBuckets();
		Assert.assertEquals(1,buckets.stream().mapToDouble(Double::doubleValue).sum(), 0.001);

		model = new CategoryRarityModel();
		occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(NUM_OF_BUCKETS+1);
		numDistinctFeatures = 1;
		model.init(occurrencesToNumOfPartitions, NUM_OF_BUCKETS, NUMBER_OF_PARTITIONS, numDistinctFeatures);
		buckets = model.getBuckets();
		Assert.assertEquals(0, buckets.stream().mapToDouble(Double::doubleValue).sum(), 0.001);
	}

	@Test
	public void modelWithOneFeatureNumOfBucketsPlusOneOccurrences() {
		CategoryRarityModel model = new CategoryRarityModel();
		Map<Long, Integer> occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(NUM_OF_BUCKETS+1);
		int numDistinctFeatures = 1;
		model.init(occurrencesToNumOfPartitions, NUM_OF_BUCKETS, NUMBER_OF_PARTITIONS, numDistinctFeatures);
		List<Double> buckets = model.getBuckets();

		Assert.assertEquals(0, buckets.stream().mapToDouble(Double::doubleValue).sum(), 0.001);
		Assert.assertEquals(NUM_OF_BUCKETS + 1, model.getNumOfSamples());
		Assert.assertEquals(1, model.getNumOfDistinctFeatures());
	}
}
