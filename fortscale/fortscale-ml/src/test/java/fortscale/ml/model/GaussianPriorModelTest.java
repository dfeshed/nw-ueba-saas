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

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenTwoPriorsForTheSameMean() {
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean = 1;
		priors.add(new GaussianPriorModel.SegmentPrior(mean, 1, 0));
		priors.add(new GaussianPriorModel.SegmentPrior(mean, 2, 0));
		new GaussianPriorModel().init(priors);
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

	@Test
	public void shouldReturnThePriorOfFirstSegmentIfOutsideTheSecondSegment() {
		GaussianPriorModel model = new GaussianPriorModel();
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean1 = 1;
		double prior1 = 2;
		double supportRadiusAroundMean1 = 0.5;
		priors.add(new GaussianPriorModel.SegmentPrior(mean1, prior1, supportRadiusAroundMean1));
		double mean2 = 1000;
		double prior2 = 2000;
		double supportRadiusAroundMean2 = 0.5;
		priors.add(new GaussianPriorModel.SegmentPrior(mean2, prior2, supportRadiusAroundMean2));
		model.init(priors);

		Assert.assertEquals(prior1, model.getPrior(mean1 + supportRadiusAroundMean1), 0.0000);
	}

	@Test
	public void shouldReturnThePriorsAverageWhenInsideTwoSegments() {
		GaussianPriorModel model = new GaussianPriorModel();
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean1 = 1;
		double prior1 = 12;
		double supportRadiusAroundMean1 = 10;
		priors.add(new GaussianPriorModel.SegmentPrior(mean1, prior1, supportRadiusAroundMean1));
		double mean2 = 4;
		double prior2 = 3;
		double supportRadiusAroundMean2 = 10;
		priors.add(new GaussianPriorModel.SegmentPrior(mean2, prior2, supportRadiusAroundMean2));
		model.init(priors);

		double weightOn2 = 2.0/3;
		Assert.assertEquals(weightOn2 * prior2 + (1 - weightOn2) * prior1, model.getPrior(mean1 + (mean2 - mean1) * weightOn2), 0.0000);
	}
}
