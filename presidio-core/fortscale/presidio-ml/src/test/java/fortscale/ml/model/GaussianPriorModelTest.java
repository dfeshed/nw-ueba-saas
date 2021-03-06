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
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean, 1, 0));
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean, 2, 0));
		new GaussianPriorModel().init(priors);
	}

	@Test
	public void shouldReturnNullWhenContainsNoSegmentPriors() {
		GaussianPriorModel model = new GaussianPriorModel();

		Assert.assertNull(model.getPrior(0));
	}

	@Test
	public void shouldReturnTheSamePriorAsWasPassedToInitWhenOnlyOneSegment() {
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean = 1;
		double prior = 2;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean, prior, 0, 0));
		GaussianPriorModel model = new GaussianPriorModel().init(priors);

		Assert.assertEquals(prior, model.getPrior(mean), 0.00001);
	}

	@Test
	public void shouldReturnTheSamePriorAsWasPassedToInitWhenInTheSupportWhenOnlyOneSegment() {
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean = 1;
		double prior = 2;
		double supportRadiusAroundMean = 0.5;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean, prior, supportRadiusAroundMean));
		GaussianPriorModel model = new GaussianPriorModel().init(priors);

		Assert.assertEquals(prior, model.getPrior(mean + supportRadiusAroundMean), 0.00001);
		Assert.assertNull(model.getPrior(mean + supportRadiusAroundMean + 0.00001));
	}

	@Test
	public void shouldReturnThePriorOfFirstSegmentIfOutsideTheSecondSegment() {
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean1 = 1;
		double prior1 = 2;
		double supportRadiusAroundMean1 = 0.5;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean1, prior1, supportRadiusAroundMean1));
		double mean2 = 1000;
		double prior2 = 2000;
		double supportRadiusAroundMean2 = 0.5;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean2, prior2, supportRadiusAroundMean2));
		GaussianPriorModel model = new GaussianPriorModel().init(priors);

		Assert.assertEquals(prior1, model.getPrior(mean1 + supportRadiusAroundMean1), 0.00001);
	}

	@Test
	public void shouldReturnThePriorsWeightedAverageWhenInsideTwoSegments() {
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean1 = 1;
		double prior1 = 12;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean1, prior1, 10));
		double mean2 = 4;
		double prior2 = 3;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean2, prior2, 10));
		GaussianPriorModel model = new GaussianPriorModel().init(priors);

		double weightOn2 = 2.0/3;
		Assert.assertEquals(weightOn2 * prior2 + (1 - weightOn2) * prior1, model.getPrior(mean1 + (mean2 - mean1) * weightOn2), 0.00001);
	}

	@Test
	public void shouldReturnThePriorOfTheCloserSegmentIfThereAreTwoContainingSegmentsFromTheSameSide() {
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean = 1;
		double prior1 = 12;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean + 1, prior1, 10));
		double prior2 = 3;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean + 2, prior2, 10));
		GaussianPriorModel model = new GaussianPriorModel().init(priors);

		Assert.assertEquals(prior1, model.getPrior(mean), 0.00001);
	}

	@Test
	public void shouldReturnNullIfOutsideTheCloserSegmentSupportButInsideTheFarSegmentSupport() {
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean = 1;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean + 1, 1, 0));
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean + 2, 2, 10));
		GaussianPriorModel model = new GaussianPriorModel().init(priors);

		Assert.assertNull(model.getPrior(mean));
	}

	@Test
	public void shouldHandleUnsortedSegmentPriors() {
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean1 = 4;
		double prior1 = 3;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean1, prior1, 10));
		double mean2 = 1;
		double prior2 = 12;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean2, prior2, 10));
		GaussianPriorModel model = new GaussianPriorModel().init(priors);

		Assert.assertEquals(prior2, model.getPrior(mean2), 0.00001);
	}

	@Test
	public void shouldRetrieveTheMinimalOrganizationPrior() {
		ArrayList<GaussianPriorModel.SegmentPrior> priors = new ArrayList<>();
		double mean1 = 1;
		double prior1 = 12;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean1, prior1, 10));
		double mean2 = 4;
		double prior2 = 3;
		priors.add(new GaussianPriorModel.SegmentPrior().init(mean2, prior2, 10));
		GaussianPriorModel model = new GaussianPriorModel().init(priors);

		Assert.assertEquals(Math.min(prior1, prior2), model.getMinPrior(), 0.00001);
	}
}
