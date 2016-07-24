package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilderConf;
import org.junit.Assert;
import org.junit.Test;

public class GaussianPriorModelBuilderConfTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfDistanceBetweenSegmentsCenterIsZero() {
		new GaussianPriorModelBuilderConf(0, 1, 1, 1, 1, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfNumberOfNeighboursIsZero() {
		new GaussianPriorModelBuilderConf(1, 0, 1, 1, 1, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMaxRatioBetweenSegmentSizeToCenterIsZero() {
		new GaussianPriorModelBuilderConf(1, 1, 0, 1, 1, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMaxSegmentWidthToNotDiscardBecauseOfBadRatioIsNegative() {
		new GaussianPriorModelBuilderConf(1, 1, 1, -1, 1, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfPaddingIsNegative() {
		new GaussianPriorModelBuilderConf(1, 1, 1, 1, -1, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinNumOfSamplesToLearnFromIsNegative() {
		new GaussianPriorModelBuilderConf(1, 1, 1, 1, 1, -1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfQuantileIsNegative() {
		new GaussianPriorModelBuilderConf(1, 1, 1, 1, 1, -1, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfQuantileIsGreaterThanOne() {
		new GaussianPriorModelBuilderConf(1, 1, 1, 1, 1, -1, 1.1);
	}

	@Test
	public void shouldGetAndSetStuff() {
		double distanceBetweenSegmentsCenter = 1;
		int numberOfNeighbours = 2;
		int maxRatioBetweenSegmentSizeToCenter = 3;
		int maxSegmentWidthToNotDiscardBecauseOfBadRatio = 4;
		int padding = 5;
		int minNumOfSamplesToLearnFrom = 6;
		double quantile = 0.9;
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConf(
				distanceBetweenSegmentsCenter,
				numberOfNeighbours,
				maxRatioBetweenSegmentSizeToCenter,
				maxSegmentWidthToNotDiscardBecauseOfBadRatio,
				padding,
				minNumOfSamplesToLearnFrom,
				quantile
		);

		Assert.assertEquals(distanceBetweenSegmentsCenter, conf.getDistanceBetweenSegmentsCenter(), 0.00001);
		Assert.assertEquals(numberOfNeighbours, conf.getNumberOfNeighbours(), 0.00001);
		Assert.assertEquals(maxRatioBetweenSegmentSizeToCenter, conf.getMaxRatioBetweenSegmentSizeToCenter(), 0.00001);
		Assert.assertEquals(maxSegmentWidthToNotDiscardBecauseOfBadRatio, conf.getMaxSegmentWidthToNotDiscardBecauseOfBadRatio(), 0.00001);
		Assert.assertEquals(padding, conf.getPadding(), 0.00001);
		Assert.assertEquals(minNumOfSamplesToLearnFrom, conf.getMinNumOfSamplesToLearnFrom(), 0.00001);
		Assert.assertEquals(quantile, conf.getQuantile(), 0.00001);
		Assert.assertEquals("gaussian_prior_model_builder", conf.getFactoryName());
	}
}
