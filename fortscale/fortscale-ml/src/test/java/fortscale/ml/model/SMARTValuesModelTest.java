package fortscale.ml.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SMARTValuesModelTest {
	@Test
	public void shouldBuildModelAccordingToData() {
		SMARTValuesModel model = new SMARTValuesModel();
		long numOfZeroValues = 10;
		long numOfPositiveValues = 20;
		double sumOfValues = 1.5;
		model.init(numOfZeroValues, numOfPositiveValues, sumOfValues);

		Assert.assertEquals(numOfPositiveValues + numOfZeroValues, model.getNumOfSamples());
		Assert.assertEquals(numOfPositiveValues, model.getNumOfPositiveValues());
		Assert.assertEquals(sumOfValues, model.getSumOfValues(), 0.0001);
	}
}
