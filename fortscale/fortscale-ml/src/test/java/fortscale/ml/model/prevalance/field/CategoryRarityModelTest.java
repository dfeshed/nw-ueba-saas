package fortscale.ml.model.prevalance.field;

import fortscale.ml.model.CategoryRarityModel;
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
		model.init(createOccurrencesToNumOfFeatures(1));
		double[] buckets = model.getBuckets();
		Assert.assertEquals(1, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(1, buckets[0], 0.001);
		Assert.assertEquals(1, model.getNumOfSamples());
		Assert.assertEquals(1, model.getNumOfDistinctRareFeatures());
	}

	@Test
	public void modelWithOneFeatureTwoOccurrences() {
		CategoryRarityModel model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(2));
		double[] buckets = model.getBuckets();
		Assert.assertEquals(1, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(1, buckets[1], 0.001);
		Assert.assertEquals(2, model.getNumOfSamples());
		Assert.assertEquals(1, model.getNumOfDistinctRareFeatures());
	}

	@Test
	public void modelWithTwoFeaturesOneOccurrence() {
		CategoryRarityModel model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(1, 1));
		double[] buckets = model.getBuckets();
		Assert.assertEquals(2, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(2, buckets[0], 0.001);
		Assert.assertEquals(2, model.getNumOfSamples());
		Assert.assertEquals(2, model.getNumOfDistinctRareFeatures());
	}

	@Test
	public void modelWithTwoFeaturesTwoOccurrencesOneFeatureOneOccurrence() {
		CategoryRarityModel model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(2, 2, 1));
		double[] buckets = model.getBuckets();
		Assert.assertEquals(3, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(1, buckets[0], 0.001);
		Assert.assertEquals(2, buckets[1], 0.001);
		Assert.assertEquals(5, model.getNumOfSamples());
		Assert.assertEquals(3, model.getNumOfDistinctRareFeatures());
	}

	@Test
	public void shouldStoreOnly_NUM_OF_BUCKETS_buckets() {
		CategoryRarityModel model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(CategoryRarityModel.NUM_OF_BUCKETS));
		double[] buckets = model.getBuckets();
		Assert.assertEquals(1, DoubleStream.of(buckets).sum(), 0.001);

		model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(CategoryRarityModel.NUM_OF_BUCKETS + 1));
		buckets = model.getBuckets();
		Assert.assertEquals(0, DoubleStream.of(buckets).sum(), 0.001);
	}

	@Test
	public void modelWithOneFeature_NUM_OF_BUCKETS_plusOneOccurrences() {
		CategoryRarityModel model = new CategoryRarityModel();
		model.init(createOccurrencesToNumOfFeatures(CategoryRarityModel.NUM_OF_BUCKETS + 1));
		double[] buckets = model.getBuckets();
		Assert.assertEquals(0, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(CategoryRarityModel.NUM_OF_BUCKETS + 1, model.getNumOfSamples());
		Assert.assertEquals(1, model.getNumOfDistinctRareFeatures());
	}
}
