package fortscale.ml.model.builder;

import org.junit.Assert;
import org.junit.Test;

public class SMARTScoreMappingModelBuilderConfTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinThresholdIsNegative() {
		new SMARTScoreMappingModelBuilderConf(null, null, -1D, 100D, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinThresholdIsGreaterThan100() {
		new SMARTScoreMappingModelBuilderConf(null, null, 101D, 100D, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinMaximalScoreIsNegative() {
		new SMARTScoreMappingModelBuilderConf(null, null, 1D, -1D, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinMaximalScoreIsGreaterThan100() {
		new SMARTScoreMappingModelBuilderConf(null, null, 0D, 101D, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinThresholdIsGreaterThanMinMaximalScore() {
		new SMARTScoreMappingModelBuilderConf(null, null, 100D, 50D, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfDefaultThresholdIsNegative() {
		new SMARTScoreMappingModelBuilderConf(-1D, null, 0D, 100D, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfDefaultThresholdIsGreaterThan100() {
		new SMARTScoreMappingModelBuilderConf(101D, null, 0D, 100D, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfDefaultMaximalScoreIsNegative() {
		new SMARTScoreMappingModelBuilderConf(null, -1D, 0D, 100D, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfDefaultMaximalScoreIsGreaterThan100() {
		new SMARTScoreMappingModelBuilderConf(null, 101D, 0D, 100D, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfDefaultThresholdIsGreaterThanDefaultMaximalScore() {
		new SMARTScoreMappingModelBuilderConf(100D, 50D, 50D, 100D, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfLowOutliersFractionIsNegative() {
		new SMARTScoreMappingModelBuilderConf(null, null, 0D, 100D, -0.1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfLowOutliersFractionIsMoreThanOne() {
		new SMARTScoreMappingModelBuilderConf(null, null, 0D, 100D, 1.1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfHhighOutliersFractionIsNegative() {
		new SMARTScoreMappingModelBuilderConf(null, null, 0D, 100D, null, -0.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfHighOutliersFractionIsMoreThanOne() {
		new SMARTScoreMappingModelBuilderConf(null, null, 0D, 100D, null, 1.1);
	}

	@Test
	public void shouldGetAndSetStuff() {
		double defaultThreshold = 30;
		double defaultMaximalScore = 40;
		double minThreshold = 50;
		double minMaximalScore = 60;
		double lowOutliersFraction = 0.2;
		double highOutliersFraction = 0.1;
		SMARTScoreMappingModelBuilderConf conf = new SMARTScoreMappingModelBuilderConf(
				defaultThreshold,
				defaultMaximalScore,
				minThreshold,
				minMaximalScore,
				lowOutliersFraction,
				highOutliersFraction
		);

		Assert.assertEquals(defaultThreshold, conf.getDefaultThreshold(), 0.00001);
		Assert.assertEquals(defaultMaximalScore, conf.getDefaultMaximalScore(), 0.00001);
		Assert.assertEquals(minThreshold, conf.getMinThreshold(), 0.00001);
		Assert.assertEquals(minMaximalScore, conf.getMinMaximalScore(), 0.00001);
		Assert.assertEquals(lowOutliersFraction, conf.getLowOutliersFraction(), 0.00001);
		Assert.assertEquals(highOutliersFraction, conf.getHighOutliersFraction(), 0.00001);
		Assert.assertEquals("smart_score_mapping_model_builder", conf.getFactoryName());
	}

	@Test
	public void shouldUseDefaults() {
		SMARTScoreMappingModelBuilderConf conf = new SMARTScoreMappingModelBuilderConf(null, null, 0D, 0D, null, null);

		Assert.assertEquals(50, conf.getDefaultThreshold(), 0.00001);
		Assert.assertEquals(100, conf.getDefaultMaximalScore(), 0.00001);
		Assert.assertEquals(2.0 / 7, conf.getLowOutliersFraction(), 0.00001);
		Assert.assertEquals(1.0 / 7, conf.getHighOutliersFraction(), 0.00001);
	}
}
