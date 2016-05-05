package fortscale.ml.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;

@RunWith(JUnit4.class)
public class CategoryRarityModelTest {
	private static final int NUM_OF_BUCKETS = 15;

	private Map<Long, Double> createOccurrencesToNumOfFeatures(long... occurrences) {
		return LongStream.of(occurrences)
				.boxed()
				.collect(Collectors.groupingBy(
						o -> o,
						Collectors.reducing(
								0D,
								o -> 1D,
								(o1, o2) -> o1 + o2
						)
				));
	}

	@Test
	public void modelWithOneFeatureOneOccurrence() {
		CategoryRarityModel model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(1), NUM_OF_BUCKETS);
		double[] buckets = model.getBuckets();
		Assert.assertEquals(1, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(1, buckets[0], 0.001);
		Assert.assertEquals(1, model.getNumOfSamples());
		Assert.assertEquals(1, model.getNumOfDistinctFeatures());
	}

	@Test
	public void modelWithOneFeatureTwoOccurrences() {
		CategoryRarityModel model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(2), NUM_OF_BUCKETS);
		double[] buckets = model.getBuckets();
		Assert.assertEquals(1, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(1, buckets[1], 0.001);
		Assert.assertEquals(2, model.getNumOfSamples());
		Assert.assertEquals(1, model.getNumOfDistinctFeatures());
	}

	@Test
	public void modelWithTwoFeaturesOneOccurrence() {
		CategoryRarityModel model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(1, 1), NUM_OF_BUCKETS);
		double[] buckets = model.getBuckets();
		Assert.assertEquals(2, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(2, buckets[0], 0.001);
		Assert.assertEquals(2, model.getNumOfSamples());
		Assert.assertEquals(2, model.getNumOfDistinctFeatures());
	}

	@Test
	public void modelWithTwoFeaturesTwoOccurrencesOneFeatureOneOccurrence() {
		CategoryRarityModel model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(2, 2, 1), NUM_OF_BUCKETS);
		double[] buckets = model.getBuckets();
		Assert.assertEquals(3, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(1, buckets[0], 0.001);
		Assert.assertEquals(2, buckets[1], 0.001);
		Assert.assertEquals(5, model.getNumOfSamples());
		Assert.assertEquals(3, model.getNumOfDistinctFeatures());
	}

	@Test
	public void shouldStoreOnlyNumOfBucketsBuckets() {
		CategoryRarityModel model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(NUM_OF_BUCKETS), NUM_OF_BUCKETS);
		double[] buckets = model.getBuckets();
		Assert.assertEquals(1, DoubleStream.of(buckets).sum(), 0.001);

		model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(NUM_OF_BUCKETS + 1), NUM_OF_BUCKETS);
		buckets = model.getBuckets();
		Assert.assertEquals(0, DoubleStream.of(buckets).sum(), 0.001);
	}

	@Test
	public void modelWithOneFeatureNumOfBucketsPlusOneOccurrences() {
		CategoryRarityModel model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(NUM_OF_BUCKETS + 1), NUM_OF_BUCKETS);
		double[] buckets = model.getBuckets();
		Assert.assertEquals(0, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(NUM_OF_BUCKETS + 1, model.getNumOfSamples());
		Assert.assertEquals(1, model.getNumOfDistinctFeatures());
	}
}
