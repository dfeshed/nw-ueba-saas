package fortscale.ml.model.builder;

import fortscale.ml.model.Model;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

public class CategoryRarityModelBuilderTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsMaxPossibleRarity() {
		new CategoryRarityModelBuilder(null, null, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsMaxPossibleRarity() {
		new CategoryRarityModelBuilder(null, -1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsMaxRaritySum() {
		new CategoryRarityModelBuilder(null, 1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsMaxRaritySum() {
		new CategoryRarityModelBuilder(null, 1, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInput() {
		new CategoryRarityModelBuilder(null, 10, 10000).build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputType() {
		new CategoryRarityModelBuilder(null, 10, 10000).build("");
	}

	@Test
	public void shouldScore0ToEmptyString() throws Exception {
		CategoryRarityModelBuilder builder = new CategoryRarityModelBuilder(null, 10, 10000);
		double score = builder.calculateScore(new ImmutablePair<Object, Integer>("", 1), null);
		Assert.assertEquals(0d, score, 0.000001);
	}

	@Test
	public void shouldScore0ToIgnoredValues() throws Exception {
		final String ignore = "ignore";
		CategoryRarityModelBuilder builder = new CategoryRarityModelBuilder(ignore, 10, 10000);
		double score = builder.calculateScore(new ImmutablePair<Object, Integer>(ignore, 1), null);
		Assert.assertEquals(0d, score, 0.000001);
	}

	@Test
	public void shouldDelegateNotIgnoredValuesToModel() throws Exception {
		CategoryRarityModelBuilder builder = new CategoryRarityModelBuilder("ignore", 10, 10000);
		Model modelMock = Mockito.mock(Model.class);
		Integer count = 1;
		double score = 95;
		Mockito.when(modelMock.calculateScore(count)).thenReturn(score);
		Assert.assertEquals(score, builder.calculateScore(new ImmutablePair<Object, Integer>("do not ignore", count), modelMock), 0.000001);
	}

	@Test
	public void shouldBuildUsingOnlyFeaturesThatAreNotIgnored() {
		String ignore = "ignore";
		Map<String, Integer> modelBuilderData = new HashMap<>();
		modelBuilderData.put("rareValue", 1);
		modelBuilderData.put("commonValue", 10);
		Integer maxPossibleRarity = 10;
		int maxRaritySum = 10;
		CategoryRarityModelBuilder builderWithIgnore = new CategoryRarityModelBuilder(ignore, maxPossibleRarity, maxRaritySum);
		CategoryRarityModelBuilder builderWithoutIgnore = new CategoryRarityModelBuilder(null, maxPossibleRarity, maxRaritySum);

		Model modelWithoutIgnoredValue = builderWithIgnore.build(modelBuilderData);
		modelBuilderData.put(ignore, 1);
		Model modelWithIgnoredValue = builderWithIgnore.build(modelBuilderData);
		Model modelWithoutIgnoring = builderWithoutIgnore.build(modelBuilderData);

		Integer count = 1;
		Assert.assertTrue(modelWithIgnoredValue.calculateScore(count) > modelWithoutIgnoring.calculateScore(count));
		Assert.assertEquals(modelWithoutIgnoredValue.calculateScore(count), modelWithIgnoredValue.calculateScore(count), 0);
	}

	@Test
	public void shouldBuildAccordingToMaxPossibleRarity() {
		Map<String, Integer> modelBuilderData = new HashMap<>();
		int rareCount = 4;
		modelBuilderData.put("rareValue", rareCount);
		int maxRaritySum = 10;
		CategoryRarityModelBuilder builderWithSmallMaxPossibleRarity = new CategoryRarityModelBuilder(null, rareCount - 1, maxRaritySum);
		CategoryRarityModelBuilder builderWithBigMaxPossibleRarity = new CategoryRarityModelBuilder(null, rareCount + 1, maxRaritySum);

		double scoreWithBigMaxPossibleRarity = builderWithBigMaxPossibleRarity.build(modelBuilderData).calculateScore(rareCount);
		double scoreWithSmallMaxPossibleRarity = builderWithSmallMaxPossibleRarity.build(modelBuilderData).calculateScore(rareCount);
		Assert.assertTrue(scoreWithBigMaxPossibleRarity > scoreWithSmallMaxPossibleRarity);
	}

	@Test
	public void shouldBuildAccordingToMaxRaritySum() {
		Map<String, Integer> modelBuilderData = new HashMap<>();
		int maxPossibleRarity = 10;
		int rareCount = 1;
		modelBuilderData.put("rareValue", rareCount);
		CategoryRarityModelBuilder builderWithBigMaxRaritySum = new CategoryRarityModelBuilder(null, maxPossibleRarity, 10000);
		CategoryRarityModelBuilder builderWithSmallMaxRaritySum = new CategoryRarityModelBuilder(null, maxPossibleRarity, 5);

		double scoreWithBigMaxRaritySum = builderWithBigMaxRaritySum.build(modelBuilderData).calculateScore(rareCount);
		double scoreWithSmallMaxRaritySum = builderWithSmallMaxRaritySum.build(modelBuilderData).calculateScore(rareCount);
		Assert.assertTrue(scoreWithBigMaxRaritySum > scoreWithSmallMaxRaritySum);
	}
}
