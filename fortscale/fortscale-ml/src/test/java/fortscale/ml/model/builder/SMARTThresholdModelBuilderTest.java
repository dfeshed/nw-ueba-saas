package fortscale.ml.model.builder;

import fortscale.ml.model.Model;
import fortscale.ml.model.SMARTThresholdModel;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.HashMap;
import java.util.Map;

public class SMARTThresholdModelBuilderTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInput() {
		new SMARTValuesModelBuilder().build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputType() {
		new SMARTValuesModelBuilder().build("");
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenDataOfOneDay() {
		Map<SMARTThresholdModel, Double> modelToNthHighestScore = new HashMap<>();

		SMARTThresholdModel yesterdayModel = new SMARTThresholdModel();
		yesterdayModel.init(95, 100);
		double yesterdayNthHighestScore = 55;
		modelToNthHighestScore.put(yesterdayModel, yesterdayNthHighestScore);

		Model newModel = new SMARTThresholdModelBuilder().build(modelToNthHighestScore);
		double newThreshold = (double) Whitebox.getInternalState(newModel, "threshold");

		Assert.assertEquals(yesterdayModel.restoreOriginalScore(yesterdayNthHighestScore), newThreshold, 0.0001);
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenDataOfTwoDays() {
		Map<SMARTThresholdModel, Double> modelToNthHighestScore = new HashMap<>();

		SMARTThresholdModel pastModel1 = new SMARTThresholdModel();
		pastModel1.init(95, 100);
		double pastNthHighestScore1 = 55;
		modelToNthHighestScore.put(pastModel1, pastNthHighestScore1);

		SMARTThresholdModel pastModel2 = new SMARTThresholdModel();
		pastModel2.init(90, 100);
		double pastNthHighestScore2 = 75;
		modelToNthHighestScore.put(pastModel2, pastNthHighestScore2);

		Model newModel = new SMARTThresholdModelBuilder().build(modelToNthHighestScore);
		double newThreshold = (double) Whitebox.getInternalState(newModel, "threshold");

		Assert.assertEquals(Math.min(pastModel1.restoreOriginalScore(pastNthHighestScore1),
				pastModel2.restoreOriginalScore(pastNthHighestScore2)), newThreshold, 0.0001);
	}
}
