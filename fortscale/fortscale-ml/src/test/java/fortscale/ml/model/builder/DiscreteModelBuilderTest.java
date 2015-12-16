package fortscale.ml.model.builder;

import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.ml.model.Model;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DiscreteModelBuilderTest {
	@Test(expected = IllegalArgumentException.class)
	public void should_fail_if_given_null_as_input() {
		new DiscreteModelBuilder().build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_fail_if_given_an_illegal_input_type() {
		new DiscreteModelBuilder().build("notTheCorrectType");
	}

	@Test
	public void should_build_model_and_calculate_score_correctly() {
		// Arrange
		GenericHistogram modelBuilderData = new GenericHistogram();
		modelBuilderData.add("rareValue", 1d);
		modelBuilderData.add("commonValue", 10d);
		DiscreteModelBuilder builder = new DiscreteModelBuilder();

		// Act
		Model oldModel = builder.build(modelBuilderData);
		modelBuilderData.add("yetAnotherRareValue", 1d);
		Model newModel = builder.build(modelBuilderData);

		// Assert
		Double count = 1d;
		Assert.assertTrue(oldModel.calculateScore(count) > newModel.calculateScore(count));
	}

	@Test
	public void should_delegate_values_to_model() {
		// Arrange
		Model model = Mockito.mock(Model.class);
		double count = 1;
		double expectedScore = 95;
		Mockito.when(model.calculateScore(count)).thenReturn(expectedScore);

		// Act
		DiscreteModelBuilder builder = new DiscreteModelBuilder();
		double actualScore = builder.calculateScore(new ImmutablePair<>("value", count), model);

		// Assert
		Assert.assertEquals(expectedScore, actualScore, 0);
	}
}
