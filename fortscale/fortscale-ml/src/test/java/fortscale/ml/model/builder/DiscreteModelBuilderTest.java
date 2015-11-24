package fortscale.ml.model.builder;

import fortscale.ml.model.Model;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class DiscreteModelBuilderTest {
	@Test
	public void shouldBuildUsingOnlyFeaturesThatAreNotIgnored() {
		String ignore = "ignore";
		List<String> modelBuilderData = new ArrayList<>();
		modelBuilderData.add("rareValue");
		for (int i = 0; i < 10; i++) {
			modelBuilderData.add("commonValue");
		}
		DiscreteModelBuilder builderWithIgnore = new DiscreteModelBuilder(ignore);
		DiscreteModelBuilder builderWithoutIgnore = new DiscreteModelBuilder(null);

		Model modelWithoutIgnoredValue = builderWithIgnore.build(modelBuilderData);
		modelBuilderData.add(ignore);
		Model modelWithIgnoredValue = builderWithIgnore.build(modelBuilderData);
		Model modelWithoutIgnoring = builderWithoutIgnore.build(modelBuilderData);

		Double count = 1d;
		Assert.assertTrue(modelWithIgnoredValue.calculateScore(count) > modelWithoutIgnoring.calculateScore(count));
		Assert.assertEquals(modelWithoutIgnoredValue.calculateScore(count), modelWithIgnoredValue.calculateScore(count), 0);
	}

	@Test
	public void shouldScore0ToEmptyString() throws Exception {
		DiscreteModelBuilder builder = new DiscreteModelBuilder(null);
		double score = builder.calculateScore(new ImmutablePair<Object, Double>("", 1d), null);
		Assert.assertEquals(0d, score, 0.000001);
	}

	@Test
	public void shouldScore0ToIgnoredValues() throws Exception {
		final String ignore = "ignore";
		DiscreteModelBuilder builder = new DiscreteModelBuilder(ignore);
		double score = builder.calculateScore(new ImmutablePair<Object, Double>(ignore, 1d), null);
		Assert.assertEquals(0d, score, 0.000001);
	}

	@Test
	public void shouldDelegateNotIgnoredValuesToModel() throws Exception {
		DiscreteModelBuilder builder = new DiscreteModelBuilder("ignore");
		Model modelMock = Mockito.mock(Model.class);
		double count = 1d;
		double score = 95;
		Mockito.when(modelMock.calculateScore(count)).thenReturn(score);
		Assert.assertEquals(score, builder.calculateScore(new ImmutablePair<Object, Double>("do not ignore", count), modelMock), 0.000001);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInput() {
		new DiscreteModelBuilder(null).build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputType() {
		new DiscreteModelBuilder(null).build("");
	}
}
