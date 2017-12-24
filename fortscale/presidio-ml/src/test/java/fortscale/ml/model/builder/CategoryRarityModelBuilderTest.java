package fortscale.ml.model.builder;

import fortscale.common.feature.CategoricalFeatureValue;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.DoubleStream;

import static org.mockito.Mockito.mock;

public class CategoryRarityModelBuilderTest {
	private static final int MAX_RARE_COUNT = 15;
	private CategoryRarityModelBuilderMetricsContainer categoryRarityMetricsContainer = mock(CategoryRarityModelBuilderMetricsContainer.class);

	private static CategoryRarityModelBuilderConf getConfig(int maxRareCount) {
		CategoryRarityModelBuilderConf categoryRarityModelBuilderConf = new CategoryRarityModelBuilderConf(maxRareCount);
		categoryRarityModelBuilderConf.setPartitionsResolutionInSeconds(86400);
		return categoryRarityModelBuilderConf;
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInput() {
		new CategoryRarityModelBuilder(getConfig(MAX_RARE_COUNT), categoryRarityMetricsContainer).build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputType() {
		new CategoryRarityModelBuilder(getConfig(MAX_RARE_COUNT), categoryRarityMetricsContainer).build("");
	}

	@Test
	public void shouldBuildWithTheSpecifiedNumOfBuckets() {
		Map<String, Long> featureValueToCountMap = new HashMap<>();
		CategoryRarityModel model = (CategoryRarityModel) new CategoryRarityModelBuilder(getConfig(MAX_RARE_COUNT), categoryRarityMetricsContainer).build(castModelBuilderData(featureValueToCountMap));
		Assert.assertEquals(MAX_RARE_COUNT, model.getBuckets().length);
	}

	@Test
	public void testBuildWithOneFeature() {
		Map<String, Long> featureValueToCountMap = new HashMap<>();
		String featureValue = "featureValue";
		long featureCount = 1;
		featureValueToCountMap.put(featureValue, featureCount);

		CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(getConfig(MAX_RARE_COUNT), categoryRarityMetricsContainer).build(castModelBuilderData(featureValueToCountMap));

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
		long featureCount = 3;
		featureValueToCountMap.put(featureValue1, featureCount);
		featureValueToCountMap.put(featureValue2, featureCount);

		CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(getConfig(MAX_RARE_COUNT), categoryRarityMetricsContainer).build(castModelBuilderData(featureValueToCountMap));

		Assert.assertEquals(1, model.getNumOfSamples());
		double[] buckets = model.getBuckets();
		Assert.assertEquals(1, DoubleStream.of(buckets).sum(), 0.001);
		Assert.assertEquals(1, buckets[0], 0.001);
	}

	@Test
	public void shouldBuildModelWithoutSavedEntries() {
		CategoryRarityModelBuilderConf config = getConfig(MAX_RARE_COUNT);
		config.setEntriesToSaveInModel(0);

		Map<String, Long> countMap = new HashMap<>();
		countMap.put("value1", 1L);
		countMap.put("value2", 2L);
		countMap.put("value3", 3L);

		CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(config, categoryRarityMetricsContainer).build(castModelBuilderData(countMap));
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

		CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(config, categoryRarityMetricsContainer).build(castModelBuilderData(countMap));
		Assert.assertEquals(CategoryRarityModelBuilderConf.DEFAULT_ENTRIES_TO_SAVE_IN_MODEL, model.getNumOfSavedFeatures());
	}

	@Test
	public void testCalcSequenceReduceData()
	{
		CategoryRarityModelBuilderConf config = getConfig(MAX_RARE_COUNT);
		CategoryRarityModelBuilder categoryRarityModelBuilder = new CategoryRarityModelBuilder(config, categoryRarityMetricsContainer);
		CategoricalFeatureValue categoricalFeatureValue = new CategoricalFeatureValue(FixedDurationStrategy.HOURLY);
		Instant startInstant = Instant.parse("2007-12-03T00:00:00.00Z");
		int amountOfFeatures = 3;
		int amountOfHours = 80;
		for (int i = 0; i< amountOfHours; i++) {
			startInstant = startInstant.plus(1,ChronoUnit.HOURS);
			for (int j = 0; j< amountOfFeatures; j++) {
				categoricalFeatureValue.getHistogram().put(new Pair<String, Instant>(String.format("feature%d",j), startInstant), 42D);
			}
		}
		Map<Pair<String, Long>, Double> sequenceReduceData = categoryRarityModelBuilder.calcSequenceReduceData(categoricalFeatureValue);
		double amountOfDays = Math.ceil((double) amountOfHours / 24);
		Assert.assertEquals(amountOfDays,sequenceReduceData.keySet().stream().map(Pair::getValue).distinct().count(),0);
		Assert.assertEquals(amountOfFeatures,sequenceReduceData.keySet().stream().map(Pair::getKey).distinct().count());
		sequenceReduceData.values().forEach(value -> Assert.assertEquals(1D,value,0));
		Map<String, Long> featureValueToCountMap = categoryRarityModelBuilder.castModelBuilderData(sequenceReduceData);
		for (int j = 0; j< amountOfFeatures; j++) {
			Assert.assertEquals(amountOfDays,featureValueToCountMap.get(String.format("feature%d",j)),0);
		}
	}

	@Test
	public void testCalcOccurrencesToNumOfFeatures()
	{
		CategoryRarityModelBuilderConf config = getConfig(MAX_RARE_COUNT);
		CategoryRarityModelBuilder categoryRarityModelBuilder = new CategoryRarityModelBuilder(config, categoryRarityMetricsContainer);

		Map<Pair<String, Long>, Double> sequenceReducedData = new HashMap<>();
		sequenceReducedData.put(new Pair<String, Long>("feature1",1L),1D);
		sequenceReducedData.put(new Pair<String, Long>("feature1",2L),1D);
		sequenceReducedData.put(new Pair<String, Long>("feature2",1L),1D);
		sequenceReducedData.put(new Pair<String, Long>("feature3",1L),1D);
		sequenceReducedData.put(new Pair<String, Long>("feature3",2L),1D);
		sequenceReducedData.put(new Pair<String, Long>("feature4",1L),1D);
		sequenceReducedData.put(new Pair<String, Long>("feature4",3L),1D);
		sequenceReducedData.put(new Pair<String, Long>("feature4",4L),1D);
		sequenceReducedData.put(new Pair<String, Long>("feature5",3L),1D);
		sequenceReducedData.put(new Pair<String, Long>("feature5",5L),1D);
		sequenceReducedData.put(new Pair<String, Long>("feature6",1L),1D);
		for(int featureIndex = 1; featureIndex <=10; featureIndex++){
			for (long partitionIndex = 1; partitionIndex <=5; partitionIndex++){
				sequenceReducedData.put(new Pair<String, Long>("feature7_"+featureIndex,partitionIndex),1D);
			}
		}
		Map<Long, Integer> occurrencesToNumOfFeatures = categoryRarityModelBuilder.calcOccurrencesToNumOfDistinctPartitions(sequenceReducedData);
		Map<Long, Integer> expectedMap = new HashMap<>();
		expectedMap.put(1L,1);// number of distinct partitions with 1 occurrence.
		expectedMap.put(2L,3);// number of distinct feature values with 2 occurrences.
		expectedMap.put(3L,1);// number of distinct feature values with 3 occurrences.
		expectedMap.put(5L,5);// number of distinct partitions with 5 occurrences.

		Assert.assertEquals(expectedMap,occurrencesToNumOfFeatures);


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

		CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(config, categoryRarityMetricsContainer).build(castModelBuilderData(countMap));
		Assert.assertEquals(3, model.getNumOfSavedFeatures());

		model.getFeatureOccurrences().values().forEach(value ->Assert.assertEquals(1D,value,0));

	}

	private static CategoricalFeatureValue castModelBuilderData(Map<String, Long> map) {
		CategoricalFeatureValue categoricalFeatureValue = new CategoricalFeatureValue(FixedDurationStrategy.HOURLY);

		GenericHistogram histogram = new GenericHistogram();
		map.entrySet().forEach(entry -> histogram.add(entry.getKey(), entry.getValue().doubleValue()));
		categoricalFeatureValue.add(histogram,Instant.parse("2007-12-03T00:00:00.00Z"));
		return categoricalFeatureValue;
	}
}
