package fortscale.ml.model.builder;

import org.junit.Assert;
import org.junit.Test;

public class SMARTValuesPriorModelBuilderConfTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfQuantileIsNegative() {
		new SMARTValuesPriorModelBuilderConf(-0.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfQuantileIsGreaterThan1() {
		new SMARTValuesPriorModelBuilderConf(1.1);
	}

	@Test
	public void shouldGetAndSetStuff() {
		double quantile = 0.8;
		SMARTValuesPriorModelBuilderConf conf = new SMARTValuesPriorModelBuilderConf(quantile);

		Assert.assertEquals(quantile, conf.getQuantile(), 0.00001);
		Assert.assertEquals("smart_values_prior_model_builder", conf.getFactoryName());
	}

	@Test
	public void shouldUseDefaults() {
		SMARTValuesPriorModelBuilderConf conf = new SMARTValuesPriorModelBuilderConf(null);

		Assert.assertEquals(0.99, conf.getQuantile(), 0.00001);
	}
}
