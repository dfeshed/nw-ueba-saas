package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilderConf;
import org.junit.Assert;
import org.junit.Test;

public class GaussianPriorModelBuilderConfTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfDistanceBetweenSegmentsCenterIsZero() {
		new GaussianPriorModelBuilderConf(0, 1, 1, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfNumberOfNeighboursIsZero() {
		new GaussianPriorModelBuilderConf(1, 0, 1, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMaxRatioBetweenSegmentSizeToCenterIsZero() {
		new GaussianPriorModelBuilderConf(1, 1, 0, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMaxSegmentWidthToNotDiscardBecauseOfBadRatioIsNegative() {
		new GaussianPriorModelBuilderConf(1, 1, 1, -1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfPaddingIsNegative() {
		new GaussianPriorModelBuilderConf(1, 1, 1, 1, -1);
	}

	@Test
	public void shouldGetAndSetStuff() {
		double distanceBetweenSegmentsCenter = 1;
		int numberOfNeighbours = 2;
		int maxRatioBetweenSegmentSizeToCenter = 3;
		int maxSegmentWidthToNotDiscardBecauseOfBadRatio = 4;
		int padding = 5;
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConf(
				distanceBetweenSegmentsCenter,
				numberOfNeighbours,
				maxRatioBetweenSegmentSizeToCenter,
				maxSegmentWidthToNotDiscardBecauseOfBadRatio,
				padding
		);

		Assert.assertEquals(distanceBetweenSegmentsCenter, conf.getDistanceBetweenSegmentsCenter(), 0.00001);
		Assert.assertEquals(numberOfNeighbours, conf.getNumberOfNeighbours(), 0.00001);
		Assert.assertEquals(maxRatioBetweenSegmentSizeToCenter, conf.getMaxRatioBetweenSegmentSizeToCenter(), 0.00001);
		Assert.assertEquals(maxSegmentWidthToNotDiscardBecauseOfBadRatio, conf.getMaxSegmentWidthToNotDiscardBecauseOfBadRatio(), 0.00001);
		Assert.assertEquals(padding, conf.getPadding(), 0.00001);
		Assert.assertEquals("gaussian_prior_model_builder", conf.getFactoryName());
	}
}
