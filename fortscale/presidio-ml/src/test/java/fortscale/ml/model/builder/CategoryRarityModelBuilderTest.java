package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.CategoryRarityModel;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.DoubleStream;

public class CategoryRarityModelBuilderTest {
	private static final int MAX_RARE_COUNT = 15;

	private static CategoryRarityModelBuilderConf getConfig(int maxRareCount) {
		return new CategoryRarityModelBuilderConf(maxRareCount);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInput() {
		new CategoryRarityModelBuilder(getConfig(MAX_RARE_COUNT)).build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputType() {
		new CategoryRarityModelBuilder(getConfig(MAX_RARE_COUNT)).build("");
	}

	@Test
	public void shouldBuildWithTheSpecifiedNumOfBuckets() {
		Map<String, Long> featureValueToCountMap = new HashMap<>();
		CategoryRarityModel model = (CategoryRarityModel) new CategoryRarityModelBuilder(getConfig(MAX_RARE_COUNT)).build(castModelBuilderData(featureValueToCountMap));
		Assert.assertEquals(MAX_RARE_COUNT, model.getBuckets().length);
	}

	@Test
	public void testBuildWithOneFeature() {
		Map<String, Long> featureValueToCountMap = new HashMap<>();
		String featureValue = "featureValue";
		long featureCount = 1;
		featureValueToCountMap.put(featureValue, featureCount);

		CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(getConfig(MAX_RARE_COUNT)).build(castModelBuilderData(featureValueToCountMap));

		Assert.assertEquals(1, model.getNumOfSamples());
		double[] buckets = model.getBuckets();
		Assert.assertEquals(1, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(1, buckets[0], 0.001);
	}

	@Test
	public void testBuildWithMultipleFeatures() {
		Map<String, Long> featureValueToCountMap = new HashMap<>();
		String featureValue1 = "featureValue1";
		String featureValue2 = "featureValue2";
		long featureCount = 1;
		featureValueToCountMap.put(featureValue1, featureCount);
		featureValueToCountMap.put(featureValue2, featureCount);

		CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(getConfig(MAX_RARE_COUNT)).build(castModelBuilderData(featureValueToCountMap));

		Assert.assertEquals(2, model.getNumOfSamples());
		double[] buckets = model.getBuckets();
		Assert.assertEquals(2, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(2, buckets[0], 0.001);
	}

	@Test
	public void shouldBuildModelWithoutSavedEntries() {
		CategoryRarityModelBuilderConf config = getConfig(MAX_RARE_COUNT);
		config.setEntriesToSaveInModel(0);

		Map<String, Long> countMap = new HashMap<>();
		countMap.put("value1", 1L);
		countMap.put("value2", 2L);
		countMap.put("value3", 3L);

		CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(config).build(castModelBuilderData(countMap));
		Assert.assertEquals(0, model.getNumOfSavedFeatures());
	}

	@Test
	public void shouldBuildModelWithDefaultNumberOfSavedFeatures() {
		CategoryRarityModelBuilderConf config = getConfig(MAX_RARE_COUNT);

		long entriesInModelBuilderData = CategoryRarityModelBuilderConf.DEFAULT_ENTRIES_TO_SAVE_IN_MODEL * 2;
		Map<String, Long> countMap = new HashMap<>();
		for (long i = 1; i <= entriesInModelBuilderData; i++) {
			countMap.put(String.format("value%d", i), i);
		}

		CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(config).build(castModelBuilderData(countMap));
		Assert.assertEquals(CategoryRarityModelBuilderConf.DEFAULT_ENTRIES_TO_SAVE_IN_MODEL, model.getNumOfSavedFeatures());

		for (int i = 0; i < CategoryRarityModelBuilderConf.DEFAULT_ENTRIES_TO_SAVE_IN_MODEL; i++) {
			double actualFeatureCount = model.getFeatureCount(String.format("value%d", entriesInModelBuilderData));
			Assert.assertEquals(entriesInModelBuilderData, actualFeatureCount, 0);
			entriesInModelBuilderData--;
		}
	}

	@Test
	public void shouldBuildModelWithNonDefaultNumberOfSavedFeatures() {
		CategoryRarityModelBuilderConf config = getConfig(MAX_RARE_COUNT);
		config.setEntriesToSaveInModel(3);

		Map<String, Long> countMap = new HashMap<>();
		countMap.put("a", 97L);
		countMap.put("b", 55L);
		countMap.put("c", 47L);
		countMap.put("d", 78L);
		countMap.put("e", 25L);

		CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(config).build(castModelBuilderData(countMap));
		Assert.assertEquals(3, model.getNumOfSavedFeatures());

		Assert.assertEquals(97, model.getFeatureCount("a"), 0);
		Assert.assertEquals(78, model.getFeatureCount("d"), 0);
		Assert.assertEquals(55, model.getFeatureCount("b"), 0);

		Assert.assertNull(model.getFeatureCount("c"));
		Assert.assertNull(model.getFeatureCount("e"));
	}

	private static GenericHistogram castModelBuilderData(Map<String, Long> map) {
		GenericHistogram histogram = new GenericHistogram();
		map.entrySet().forEach(entry -> histogram.add(entry.getKey(), entry.getValue().doubleValue()));
		return histogram;
	}
}
