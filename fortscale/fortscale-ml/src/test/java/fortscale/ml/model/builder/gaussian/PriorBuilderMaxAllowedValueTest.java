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
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeQuantile() {
		new PriorBuilderMaxAllowedValue(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenQuantileBiggerThanOne() {
		new PriorBuilderMaxAllowedValue(1.1);
	}

	@Test
	public void shouldReturnNullGivenNoModels() {
		Double prior = new PriorBuilderMaxAllowedValue(0.9).calcPrior(Collections.emptyList(), 0);

		Assert.assertNull(prior);
	}

	@Test
	public void shouldReturnTheDifferenceBetweenTheModelMaxValueToTheReferenceMeanDividedByTwo() {
		double maxValue = 12;
		double meanOfReference = 8;
		ContinuousDataModel model = new ContinuousDataModel().setParameters(10, 10, 0.1, maxValue);
		Double prior = new PriorBuilderMaxAllowedValue(0.9).calcPrior(Collections.singletonList(model), meanOfReference);

		Assert.assertEquals((maxValue - meanOfReference) / 2.0, prior, 0.000001);
	}

	@Test
	public void shouldReturnTheDifferenceBetweenTheMaxValueOfTheQuantileModelToTheReferenceMeanDividedByTwo() {
		double quantile = 0.9;
		double meanOfReference = 8;
		List<ContinuousDataModel> models = IntStream.range(0, 100)
				.mapToObj(maxValue -> new ContinuousDataModel().setParameters(10, 10, 0.1, maxValue))
				.collect(Collectors.toList());
		Double prior = new PriorBuilderMaxAllowedValue(quantile).calcPrior(models, meanOfReference);

		double maxValueOfQuantie = models.get((int) ((models.size() - 1) * quantile)).getMaxValue();
		Assert.assertEquals((maxValueOfQuantie - meanOfReference) / 2.0, prior, 0.000001);
	}
}
