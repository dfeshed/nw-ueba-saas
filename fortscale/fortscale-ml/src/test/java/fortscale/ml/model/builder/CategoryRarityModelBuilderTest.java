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

	private static GenericHistogram castModelBuilderData(Map<String, Long> map) {
		GenericHistogram histogram = new GenericHistogram();
		map.entrySet().forEach(entry -> histogram.add(entry.getKey(), entry.getValue().doubleValue()));
		return histogram;
	}
}
