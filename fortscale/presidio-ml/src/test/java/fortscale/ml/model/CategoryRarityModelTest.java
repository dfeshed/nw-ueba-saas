package fortscale.ml.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
		model.init(occurrencesToNumOfPartitions, occurrencesToNumOfPartitions, NUM_OF_BUCKETS, 1,1);
		assertOnBuckets(model.getOccurrencesToNumOfPartitionsList(), 15, 1);
		Assert.assertEquals(1, model.getNumOfSamples());
		Assert.assertEquals(1, model.getNumOfDistinctFeatures());
	}

	private void assertOnBuckets(List<Double> buckets, double expectedBucketsSummation, double... expectedBucketsValues){
		Assert.assertEquals(expectedBucketsSummation, buckets.stream().mapToDouble(Double::doubleValue).sum(), 0.001);
		int i = 0;
		for(double val: expectedBucketsValues){
			Assert.assertEquals(val, buckets.get(i), 0.001);
			i++;
		}

	}

	@Test
	public void modelWithOneFeatureTwoOccurrences() {
		CategoryRarityModel model = new CategoryRarityModel();
		Map<Long, Integer> occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(2,2);
		int numDistinctFeatures = 1;
		model.init(occurrencesToNumOfPartitions, occurrencesToNumOfPartitions, NUM_OF_BUCKETS, 2, numDistinctFeatures);
		assertOnBuckets(model.getOccurrencesToNumOfPartitionsList(), 28, 0, 2);
		Assert.assertEquals(2, model.getNumOfSamples());
		Assert.assertEquals(1, model.getNumOfDistinctFeatures());
	}

	@Test
	public void modelWithTwoFeaturesOneOccurrenceOnDistinctDays() {
		CategoryRarityModel model = new CategoryRarityModel();
		Map<Long, Integer> occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(1,1);
		int numDistinctFeatures = 2;
		model.init(occurrencesToNumOfPartitions, occurrencesToNumOfPartitions, NUM_OF_BUCKETS, 2, numDistinctFeatures);
		assertOnBuckets(model.getOccurrencesToNumOfPartitionsList(), 30, 2);
		Assert.assertEquals(2, model.getNumOfSamples());
		Assert.assertEquals(2, model.getNumOfDistinctFeatures());
	}

	@Test
	public void modelWithTwoFeaturesTwoOccurrencesOneFeatureOneOccurrenceOnThreeDistinctDays() {
		CategoryRarityModel model = new CategoryRarityModel();
		Map<Long, Integer> occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(1, 2, 2, 2);
		int numDistinctFeatures = 3;
		model.init(occurrencesToNumOfPartitions, occurrencesToNumOfPartitions, NUM_OF_BUCKETS, 3, numDistinctFeatures);
		assertOnBuckets(model.getOccurrencesToNumOfPartitionsList(), 43, 1, 3);
		Assert.assertEquals(3, model.getNumOfSamples());
		Assert.assertEquals(3, model.getNumOfDistinctFeatures());
	}

	@Test
	public void shouldStoreOnlyNumOfBucketsBuckets() {
		CategoryRarityModel model = new CategoryRarityModel();
		Map<Long, Integer> occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(NUM_OF_BUCKETS);
		int numDistinctFeatures = 1;
		model.init(occurrencesToNumOfPartitions, occurrencesToNumOfPartitions, NUM_OF_BUCKETS, NUMBER_OF_PARTITIONS, numDistinctFeatures);
		assertOnBuckets(model.getOccurrencesToNumOfPartitionsList(), 1);

		model = new CategoryRarityModel();
		occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(NUM_OF_BUCKETS+1);
		numDistinctFeatures = 1;
		model.init(occurrencesToNumOfPartitions, occurrencesToNumOfPartitions, NUM_OF_BUCKETS, NUMBER_OF_PARTITIONS, numDistinctFeatures);
		assertOnBuckets(model.getOccurrencesToNumOfPartitionsList(), 0);
	}

	@Test
	public void modelWithOneFeatureNumOfBucketsPlusOneOccurrences() {
		CategoryRarityModel model = new CategoryRarityModel();
		Map<Long, Integer> occurrencesToNumOfPartitions = createOccurrencesToNumOfPartitions(NUM_OF_BUCKETS+1);
		int numDistinctFeatures = 1;
		model.init(occurrencesToNumOfPartitions, occurrencesToNumOfPartitions, NUM_OF_BUCKETS, NUM_OF_BUCKETS+1, numDistinctFeatures);
		assertOnBuckets(model.getOccurrencesToNumOfPartitionsList(), 0);
		Assert.assertEquals(NUM_OF_BUCKETS + 1, model.getNumOfSamples());
		Assert.assertEquals(1, model.getNumOfDistinctFeatures());
	}
}
