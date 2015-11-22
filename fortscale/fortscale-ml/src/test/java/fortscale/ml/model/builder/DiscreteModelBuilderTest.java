package fortscale.ml.model.builder;

import fortscale.ml.model.Model;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class DiscreteModelBuilderTest {
	@Test
	public void shouldBuildModelWithGivenIgnorePattern() {
		String ignorePattern = "ignorePattern";
		DiscreteModelBuilder builder = new DiscreteModelBuilder(ignorePattern);
		Model model = builder.build(Collections.emptyList());
		Assert.assertEquals(ignorePattern, ((Pattern) Whitebox.getInternalState(model, "ignoreValues")).pattern());
	}

	@Test
	public void shouldBuildModelWithIgnorePatternSetToNull() {
		DiscreteModelBuilder builder = new DiscreteModelBuilder(null);
		Model model = builder.build(Collections.emptyList());
		Assert.assertNull(Whitebox.getInternalState(model, "ignoreValues"));
	}

	@Test
	public void shouldBuildUsingOnlyFeaturesThatAreNotIgnored() {
		String ignore = "ignore";
		String rareValue = "rareValue";
		String commonValue = "commonValue";
		List<String> modelBuilderData = new ArrayList<>();
		modelBuilderData.add(rareValue);
		for (int i = 0; i < 10; i++) {
			modelBuilderData.add(commonValue);
		}
		DiscreteModelBuilder builderWithIgnore = new DiscreteModelBuilder(ignore);
		DiscreteModelBuilder builderWithoutIgnore = new DiscreteModelBuilder(null);

		Model modelWithoutIgnoredValue = builderWithIgnore.build(modelBuilderData);
		modelBuilderData.add(ignore);
		Model modelWithIgnoredValue = builderWithIgnore.build(modelBuilderData);
		Model modelWithoutIgnoring = builderWithoutIgnore.build(modelBuilderData);

		ImmutablePair<Object, Double> value = new ImmutablePair<Object, Double>(rareValue, 1d);
		Assert.assertTrue(modelWithIgnoredValue.calculateScore(value) > modelWithoutIgnoring.calculateScore(value));
		Assert.assertEquals(modelWithoutIgnoredValue.calculateScore(value), modelWithIgnoredValue.calculateScore(value), 0);
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
