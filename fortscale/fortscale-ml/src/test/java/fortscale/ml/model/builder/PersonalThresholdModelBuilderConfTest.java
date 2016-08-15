package fortscale.ml.model.builder;

import org.junit.Assert;
import org.junit.Test;

public class PersonalThresholdModelBuilderConfTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfDesiredNumOfIndicatorsIsZero() {
		new PersonalThresholdModelBuilderConf(0);
	}

	@Test
	public void shouldGetAndSetStuff() {
		int desiredNumOfIndicators = 1;
		PersonalThresholdModelBuilderConf conf = new PersonalThresholdModelBuilderConf(desiredNumOfIndicators);

		Assert.assertEquals(desiredNumOfIndicators, conf.getDesiredNumOfIndicators(), 0.00001);
		Assert.assertEquals("personal_threshold_model_builder", conf.getFactoryName());
	}
}
