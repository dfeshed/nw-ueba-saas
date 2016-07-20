package fortscale.ml.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

@RunWith(JUnit4.class)
public class GaussianPriorModelTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsPriors() {
		new GaussianPriorModel().init(null);
	}

	@Test
	public void shouldReturnTheSamePriorAsWasPassedToInitWhenOnlyOneSegment() {
		GaussianPriorModel model = new GaussianPriorModel();
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean = 1;
		double prior = 2;
		priors.add(new GaussianPriorModel.SegmentPrior(mean, prior, 0, 0));
		model.init(priors);

		Assert.assertEquals(prior, model.getPrior(mean), 0.00001);
	}

	@Test
	public void shouldReturnTheSamePriorAsWasPassedToInitWhenInTheSupportWhenOnlyOneSegment() {
		GaussianPriorModel model = new GaussianPriorModel();
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean = 1;
		double prior = 2;
		double supportRadiusAroundMean = 0.5;
		priors.add(new GaussianPriorModel.SegmentPrior(mean, prior, supportRadiusAroundMean));
		model.init(priors);

		Assert.assertEquals(prior, model.getPrior(mean + supportRadiusAroundMean), 0.0000);
		Assert.assertNull(model.getPrior(mean + supportRadiusAroundMean + 0.00001));
	}
}
