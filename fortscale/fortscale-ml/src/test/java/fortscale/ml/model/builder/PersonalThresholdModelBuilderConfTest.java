package fortscale.ml.model.builder;

import org.junit.Assert;
import org.junit.Test;

public class PersonalThresholdModelBuilderConfTest {
	@Test
	public void shouldGetAndSetStuff() {
		PersonalThresholdModelBuilderConf conf = new PersonalThresholdModelBuilderConf();

		Assert.assertEquals("personal_threshold_model_builder", conf.getFactoryName());
	}
}
