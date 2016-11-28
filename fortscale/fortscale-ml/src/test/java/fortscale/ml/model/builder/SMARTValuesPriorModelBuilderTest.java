package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.SMARTValuesPriorModel;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class SMARTValuesPriorModelBuilderTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsConf() {
		new SMARTValuesPriorModelBuilder(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInput() {
		new SMARTValuesPriorModelBuilder(new SMARTValuesPriorModelBuilderConf(0.99)).build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputType() {
		new SMARTValuesPriorModelBuilder(new SMARTValuesPriorModelBuilderConf(0.99)).build("");
	}

	@Test
	public void shouldNotBeAffectedByQuantileIfOnlyOneSample() {
		double[] smartValues = {0.1};

		Arrays.asList(0.0, 0.5, 1.0).forEach(quantile ->
				Assert.assertEquals(0.1, buildModel(quantile, smartValues).getPrior(), 0.000001));
	}

	@Test
	public void shouldNotBeAffectedByQuantileIfAllSamplesAreIdentical() {
		double[] smartValues = {0.1, 0.1, 0.1, 0.1, 0.1};

		Arrays.asList(0.0, 0.5, 1.0).forEach(quantile ->
				Assert.assertEquals(0.1, buildModel(quantile, smartValues).getPrior(), 0.000001));
	}

	@Test
	public void shouldBuildModelCorrectlyGivenEvenNumberOfSamples() {
		double[] smartValues = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

		Assert.assertEquals(0.1, buildModel(0.0, smartValues).getPrior(), 0.000001);
		Assert.assertEquals(0.1, buildModel(0.1, smartValues).getPrior(), 0.000001);
		Assert.assertEquals(0.5, buildModel(0.5, smartValues).getPrior(), 0.000001);
		Assert.assertEquals(0.9, buildModel(0.9, smartValues).getPrior(), 0.000001);
		Assert.assertEquals(0.9, buildModel(0.99999, smartValues).getPrior(), 0.000001);
		Assert.assertEquals(1.0, buildModel(1.0, smartValues).getPrior(), 0.000001);
	}

	@Test
	public void shouldBuildModelCorrectlyGivenOddNumberOfSamples() {
		double[] smartValues = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};

		Assert.assertEquals(0.1, buildModel(0.0, smartValues).getPrior(), 0.000001);
		Assert.assertEquals(0.5, buildModel(0.5, smartValues).getPrior(), 0.000001);
		Assert.assertEquals(0.9, buildModel(1.0, smartValues).getPrior(), 0.000001);
	}

	@Test
	public void shouldDiscardZeros() {
		double[] smartValues = {0.0, 0.0, 1.0};

		Assert.assertEquals(1.0, buildModel(0.0, smartValues).getPrior(), 0.000001);
	}

	@Test
	public void shouldHandleEmptyData() {
		double[] smartValues = {};

		Assert.assertEquals(0.0, buildModel(0.0, smartValues).getPrior(), 0.000001);
	}

	private SMARTValuesPriorModel buildModel(double quantile, double[] SMARTValues) {
		GenericHistogram SMARTValuesHist = createSMARTValuesHist(SMARTValues);
		SMARTValuesPriorModelBuilderConf conf = new SMARTValuesPriorModelBuilderConf(quantile);
		return (SMARTValuesPriorModel) new SMARTValuesPriorModelBuilder(conf).build(SMARTValuesHist);
	}

	private GenericHistogram createSMARTValuesHist(double... values) {
		return Arrays.stream(values)
				.boxed()
				.reduce(
						new GenericHistogram(),
						(hist, SMARTValue) -> {
							hist.add(SMARTValue, 1D);
							return hist;
						},
						GenericHistogram::add
				);
	}
}
