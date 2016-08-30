package fortscale.ml.model.builder;

import org.junit.Assert;
import org.junit.Test;

public class SMARTScoreMappingModelBuilderConfTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinThresholdIsGreaterThanMinMaximalScore() {
		new SMARTScoreMappingModelBuilderConf(50D, 100D, 100D, 50D);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfDefaultThresholdIsGreaterThanDefaultMaximalScore() {
		new SMARTScoreMappingModelBuilderConf(100D, 50D, 50D, 100D);
	}

	@Test
	public void shouldGetAndSetStuff() {
		double defaultThreshold = 30;
		double defaultMaximalScore = 40;
		double minThreshold = 50;
		double minMaximalScore = 60;
		SMARTScoreMappingModelBuilderConf model =
				new SMARTScoreMappingModelBuilderConf(defaultThreshold, defaultMaximalScore, minThreshold, minMaximalScore);

		Assert.assertEquals(defaultThreshold, model.getDefaultThreshold(), 0.00001);
		Assert.assertEquals(defaultMaximalScore, model.getDefaultMaximalScore(), 0.00001);
		Assert.assertEquals(minThreshold, model.getMinThreshold(), 0.00001);
		Assert.assertEquals(minMaximalScore, model.getMinMaximalScore(), 0.00001);
		Assert.assertEquals("smart_score_mapping_model_builder", model.getFactoryName());
	}

	@Test
	public void shouldUseDefaults() {
		SMARTScoreMappingModelBuilderConf model = new SMARTScoreMappingModelBuilderConf(null, null, 0D, 0D);

		Assert.assertEquals(50, model.getDefaultThreshold(), 0.00001);
		Assert.assertEquals(100, model.getDefaultMaximalScore(), 0.00001);
		Assert.assertEquals("smart_score_mapping_model_builder", model.getFactoryName());
	}
}
