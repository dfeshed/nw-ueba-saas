package fortscale.ml.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SMARTValuesPriorModelTest {
	@Test
	public void shouldBuildModelAccordingToData() {
		SMARTValuesPriorModel model = new SMARTValuesPriorModel();
		double prior = 0.3;
		model.init(prior);

		Assert.assertEquals(prior, model.getPrior(), 0.0001);
	}
}
