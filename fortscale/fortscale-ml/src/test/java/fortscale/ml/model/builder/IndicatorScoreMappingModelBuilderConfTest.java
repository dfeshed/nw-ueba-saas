package fortscale.ml.model.builder;

import org.junit.Assert;
import org.junit.Test;

public class IndicatorScoreMappingModelBuilderConfTest {
	@Test
	public void shouldHaveTheRightFactoryName() {
		Assert.assertEquals("indicator_score_mapping_model_builder", new IndicatorScoreMappingModelBuilderConf().getFactoryName());
	}

	@Test
	public void shouldEnforceMinThresholdAndMinMaximalScore() {
		IndicatorScoreMappingModelBuilderConf model = new IndicatorScoreMappingModelBuilderConf();

		Assert.assertEquals(50, model.getMinThreshold(), 0.00001);
		Assert.assertEquals(100, model.getMinMaximalScore(), 0.00001);
	}
}
