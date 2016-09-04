package fortscale.ml.model.builder;

import org.junit.Assert;
import org.junit.Test;

public class SMARTScoreMappingModelBuilderConfTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinThresholdIsNegative() {
		new SMARTScoreMappingModelBuilderConf(-1, 100, 0.1, 0.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinThresholdIsGreaterThan100() {
		new SMARTScoreMappingModelBuilderConf(101, 100, 0.1, 0.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinMaximalScoreIsNegative() {
		new SMARTScoreMappingModelBuilderConf(1, -1, 0.1, 0.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinMaximalScoreIsGreaterThan100() {
		new SMARTScoreMappingModelBuilderConf(0, 101, 0.1, 0.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinThresholdIsGreaterThanMinMaximalScore() {
		new SMARTScoreMappingModelBuilderConf(10, 5, 0.1, 0.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfLowOutliersFractionIsNegative() {
		new SMARTScoreMappingModelBuilderConf(0, 100, -0.1, 0.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfLowOutliersFractionIsMoreThanOne() {
		new SMARTScoreMappingModelBuilderConf(0, 100, 1.1, 0.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfHhighOutliersFractionIsNegative() {
		new SMARTScoreMappingModelBuilderConf(0, 100, 0.1, -0.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfHighOutliersFractionIsMoreThanOne() {
		new SMARTScoreMappingModelBuilderConf(0, 100, 0.1, 1.1);
	}

	@Test
	public void shouldGetAndSetStuff() {
		int minThreshold = 5;
		int minMaximalScore = 10;
		double lowOutliersFraction = 0.2;
		double highOutliersFraction = 0.1;
		SMARTScoreMappingModelBuilderConf conf = new SMARTScoreMappingModelBuilderConf(
				minThreshold,
				minMaximalScore,
				lowOutliersFraction,
				highOutliersFraction
		);

		Assert.assertEquals(minThreshold, conf.getMinThreshold(), 0.00001);
		Assert.assertEquals(minMaximalScore, conf.getMinMaximalScore(), 0.00001);
		Assert.assertEquals(lowOutliersFraction, conf.getLowOutliersFraction(), 0.00001);
		Assert.assertEquals(highOutliersFraction, conf.getHighOutliersFraction(), 0.00001);
		Assert.assertEquals("smart_score_mapping_model_builder", conf.getFactoryName());
	}
}