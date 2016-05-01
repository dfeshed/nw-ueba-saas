package fortscale.ml.model.prevalance.field;

import fortscale.ml.model.SMARTThresholdModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Map;

@RunWith(JUnit4.class)
public class SMARTThresholdModelTest {
	@Test
	public void shouldMapThresholdTo50AndMaxSeenScoreTo100() {
		SMARTThresholdModel model = new SMARTThresholdModel();
		double threshold = 90;
		double maxSeenScore = 95;
		model.init(threshold, maxSeenScore);
		Map<Double, Double> mapping = model.getScoreMappingConf().getMapping();

		Assert.assertEquals(0, mapping.get(0D), 0.0001);
		Assert.assertEquals(50, mapping.get(threshold), 0.0001);
		Assert.assertEquals(100, mapping.get(maxSeenScore), 0.0001);
		Assert.assertEquals(100, mapping.get(100D), 0.0001);
	}

	@Test
	public void shouldMap0To50IfThereIsNoData() {
		SMARTThresholdModel model = new SMARTThresholdModel();
		double threshold = 0;
		double maxSeenScore = threshold;
		model.init(threshold, maxSeenScore);
		Map<Double, Double> mapping = model.getScoreMappingConf().getMapping();

		Assert.assertEquals(50, mapping.get(threshold), 0.0001);
		Assert.assertEquals(100, mapping.get(100D), 0.0001);
	}
}
