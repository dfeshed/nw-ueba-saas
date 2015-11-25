package fortscale.ml.model.builder;

import fortscale.ml.model.Model;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

public class CategoryRarityModelBuilderTest {
	@Test
	public void shouldBuildUsingOnlyFeaturesThatAreNotIgnored() {
		String ignore = "ignore";
		Map<String, Double> modelBuilderData = new HashMap<>();
		modelBuilderData.put("rareValue", 1d);
		modelBuilderData.put("commonValue", 10d);
		CategoryRarityModelBuilder builderWithIgnore = new CategoryRarityModelBuilder(ignore);
		CategoryRarityModelBuilder builderWithoutIgnore = new CategoryRarityModelBuilder(null);

		Model modelWithoutIgnoredValue = builderWithIgnore.build(modelBuilderData);
		modelBuilderData.put(ignore, 1d);
		Model modelWithIgnoredValue = builderWithIgnore.build(modelBuilderData);
		Model modelWithoutIgnoring = builderWithoutIgnore.build(modelBuilderData);

		Double count = 1d;
		Assert.assertTrue(modelWithIgnoredValue.calculateScore(count) > modelWithoutIgnoring.calculateScore(count));
		Assert.assertEquals(modelWithoutIgnoredValue.calculateScore(count), modelWithIgnoredValue.calculateScore(count), 0);
	}

	@Test
	public void shouldScore0ToEmptyString() throws Exception {
		CategoryRarityModelBuilder builder = new CategoryRarityModelBuilder(null);
		double score = builder.calculateScore(new ImmutablePair<Object, Double>("", 1d), null);
		Assert.assertEquals(0d, score, 0.000001);
	}

	@Test
	public void shouldScore0ToIgnoredValues() throws Exception {
		final String ignore = "ignore";
		CategoryRarityModelBuilder builder = new CategoryRarityModelBuilder(ignore);
		double score = builder.calculateScore(new ImmutablePair<Object, Double>(ignore, 1d), null);
		Assert.assertEquals(0d, score, 0.000001);
	}

	@Test
	public void shouldDelegateNotIgnoredValuesToModel() throws Exception {
		CategoryRarityModelBuilder builder = new CategoryRarityModelBuilder("ignore");
		Model modelMock = Mockito.mock(Model.class);
		double count = 1d;
		double score = 95;
		Mockito.when(modelMock.calculateScore(count)).thenReturn(score);
		Assert.assertEquals(score, builder.calculateScore(new ImmutablePair<Object, Double>("do not ignore", count), modelMock), 0.000001);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInput() {
		new CategoryRarityModelBuilder(null).build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputType() {
		new CategoryRarityModelBuilder(null).build("");
	}
}
