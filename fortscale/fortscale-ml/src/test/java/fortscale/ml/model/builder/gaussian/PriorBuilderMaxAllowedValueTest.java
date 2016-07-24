package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.builder.gaussian.prior.PriorBuilderMaxAllowedValue;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PriorBuilderMaxAllowedValueTest {
	private void assertPrior(double expectedMaxValueUsed, double meanOfReference, Double actualPrior) {
		Assert.assertEquals((expectedMaxValueUsed - meanOfReference) / 2.0, actualPrior, 0.000001);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeQuantile() {
		new PriorBuilderMaxAllowedValue(-1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenQuantileBiggerThanOne() {
		new PriorBuilderMaxAllowedValue(1.1, null);
	}

	@Test
	public void shouldReturnNullGivenNoModels() {
		Double prior = new PriorBuilderMaxAllowedValue(0.9, null).calcPrior(Collections.emptyList(), 0);

		Assert.assertNull(prior);
	}

	@Test
	public void shouldReturnTheDifferenceBetweenTheModelMaxValueToTheReferenceMeanDividedByTwo() {
		double maxValue = 12;
		double meanOfReference = 8;
		ContinuousDataModel model = new ContinuousDataModel().setParameters(10, 10, 0.1, maxValue);
		Double prior = new PriorBuilderMaxAllowedValue(0.9, null).calcPrior(Collections.singletonList(model), meanOfReference);

		assertPrior(maxValue, meanOfReference, prior);
	}

	@Test
	public void shouldReturnTheDifferenceBetweenTheMaxValueOfTheQuantileModelToTheReferenceMeanDividedByTwo() {
		double quantile = 0.9;
		double meanOfReference = 8;
		List<ContinuousDataModel> models = IntStream.range(0, 100)
				.mapToObj(maxValue -> new ContinuousDataModel().setParameters(10, 10, 0.1, maxValue))
				.collect(Collectors.toList());
		Double prior = new PriorBuilderMaxAllowedValue(quantile, null).calcPrior(models, meanOfReference);

		double maxValueOfQuantile = models.get((int) ((models.size() - 1) * quantile)).getMaxValue();
		assertPrior(maxValueOfQuantile, meanOfReference, prior);
	}

	@Test
	public void shouldUseMinimalMaxValue() {
		double quantile = 0.9;
		double meanOfReference = 0;
		double minMaxValue = 3;
		ContinuousDataModel model = new ContinuousDataModel().setParameters(0, 0, 0, 0);
		Double prior = new PriorBuilderMaxAllowedValue(quantile, minMaxValue).calcPrior(Collections.singletonList(model), meanOfReference);

		assertPrior(minMaxValue, meanOfReference, prior);
	}
}
