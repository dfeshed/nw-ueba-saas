package fortscale.ml.model.builder;

import org.junit.Assert;
import org.junit.Test;

public class SMARTScoreMappingModelBuilderConfTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinThresholdIsGreaterThanMinMaximalScore() {
		new SMARTScoreMappingModelBuilderConf(10, 5);
	}

	@Test
	public void shouldGetAndSetStuff() {
		int minThreshold = 5;
		int minMaximalScore = 10;
		SMARTScoreMappingModelBuilderConf model = new SMARTScoreMappingModelBuilderConf(minThreshold, minMaximalScore);

		Assert.assertEquals(minThreshold, model.getMinThreshold(), 0.00001);
		Assert.assertEquals(minMaximalScore, model.getMinMaximalScore(), 0.00001);
		Assert.assertEquals("smart_score_mapping_model_builder", model.getFactoryName());
	}
}
