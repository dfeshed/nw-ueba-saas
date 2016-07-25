package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilderConf;
import org.junit.Assert;
import org.junit.Test;

public class GaussianPriorModelBuilderConfTest {
	private static class GaussianPriorModelBuilderConfBuilder {
		private double distanceBetweenSegmentCenters;
		private int numberOfNeighbours;
		private double maxRatioBetweenSegmentSizeToCenter;
		private double maxSegmentWidthToNotDiscardBecauseOfBadRatio;
		private double padding;
		private int minNumOfSamplesToLearnFrom;
		private double quantile;
		private Double minMaxValue;

		public GaussianPriorModelBuilderConfBuilder setDistanceBetweenSegmentCenters(double distanceBetweenSegmentCenters) {
			this.distanceBetweenSegmentCenters = distanceBetweenSegmentCenters;
			return this;
		}

		public GaussianPriorModelBuilderConfBuilder setNumberOfNeighbours(int numberOfNeighbours) {
			this.numberOfNeighbours = numberOfNeighbours;
			return this;
		}

		public GaussianPriorModelBuilderConfBuilder setMaxRatioBetweenSegmentSizeToCenter(double maxRatioBetweenSegmentSizeToCenter) {
			this.maxRatioBetweenSegmentSizeToCenter = maxRatioBetweenSegmentSizeToCenter;
			return this;
		}

		public GaussianPriorModelBuilderConfBuilder setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(double maxSegmentWidthToNotDiscardBecauseOfBadRatio) {
			this.maxSegmentWidthToNotDiscardBecauseOfBadRatio = maxSegmentWidthToNotDiscardBecauseOfBadRatio;
			return this;
		}

		public GaussianPriorModelBuilderConfBuilder setPadding(double padding) {
			this.padding = padding;
			return this;
		}

		public GaussianPriorModelBuilderConfBuilder setMinNumOfSamplesToLearnFrom(int minNumOfSamplesToLearnFrom) {
			this.minNumOfSamplesToLearnFrom = minNumOfSamplesToLearnFrom;
			return this;
		}

		public GaussianPriorModelBuilderConfBuilder setQuantile(double quantile) {
			this.quantile = quantile;
			return this;
		}

		public GaussianPriorModelBuilderConfBuilder setMinMaxValue(Double minMaxValue) {
			this.minMaxValue = minMaxValue;
			return this;
		}

		public GaussianPriorModelBuilderConf build() {
			return new GaussianPriorModelBuilderConf(
					distanceBetweenSegmentCenters,
					numberOfNeighbours,
					maxRatioBetweenSegmentSizeToCenter,
					maxSegmentWidthToNotDiscardBecauseOfBadRatio,
					padding,
					minNumOfSamplesToLearnFrom,
					quantile,
					minMaxValue
			);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfDistanceBetweenSegmentCentersIsZero() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(0)
				.setNumberOfNeighbours(1)
				.setMaxRatioBetweenSegmentSizeToCenter(1)
				.setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(1)
				.setPadding(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setQuantile(1)
				.setMinMaxValue(null)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfNumberOfNeighboursIsZero() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setNumberOfNeighbours(0)
				.setMaxRatioBetweenSegmentSizeToCenter(1)
				.setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(1)
				.setPadding(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setQuantile(1)
				.setMinMaxValue(null)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMaxRatioBetweenSegmentSizeToCenterIsZero() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setNumberOfNeighbours(1)
				.setMaxRatioBetweenSegmentSizeToCenter(0)
				.setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(1)
				.setPadding(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setQuantile(1)
				.setMinMaxValue(null)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMaxSegmentWidthToNotDiscardBecauseOfBadRatioIsNegative() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setNumberOfNeighbours(1)
				.setMaxRatioBetweenSegmentSizeToCenter(1)
				.setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(-1)
				.setPadding(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setQuantile(1)
				.setMinMaxValue(null)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfPaddingIsNegative() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setNumberOfNeighbours(1)
				.setMaxRatioBetweenSegmentSizeToCenter(1)
				.setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(1)
				.setPadding(-1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setQuantile(1)
				.setMinMaxValue(null)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinNumOfSamplesToLearnFromIsNegative() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setNumberOfNeighbours(1)
				.setMaxRatioBetweenSegmentSizeToCenter(1)
				.setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(1)
				.setPadding(1)
				.setMinNumOfSamplesToLearnFrom(-1)
				.setQuantile(1)
				.setMinMaxValue(null)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfQuantileIsNegative() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setNumberOfNeighbours(1)
				.setMaxRatioBetweenSegmentSizeToCenter(1)
				.setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(1)
				.setPadding(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setQuantile(-1)
				.setMinMaxValue(null)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfQuantileIsGreaterThanOne() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setNumberOfNeighbours(1)
				.setMaxRatioBetweenSegmentSizeToCenter(1)
				.setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(1)
				.setPadding(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setQuantile(1.1)
				.setMinMaxValue(null)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinMaxValueIsNegative() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setNumberOfNeighbours(1)
				.setMaxRatioBetweenSegmentSizeToCenter(1)
				.setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(1)
				.setPadding(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setQuantile(1)
				.setMinMaxValue(-1.0)
				.build();
	}

	@Test
	public void shouldGetAndSetStuff() {
		double distanceBetweenSegmentCenters = 1;
		int numberOfNeighbours = 2;
		int maxRatioBetweenSegmentSizeToCenter = 3;
		int maxSegmentWidthToNotDiscardBecauseOfBadRatio = 4;
		int padding = 5;
		int minNumOfSamplesToLearnFrom = 6;
		double quantile = 0.9;
		Double minMaxValue = 7.0;
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(distanceBetweenSegmentCenters)
				.setNumberOfNeighbours(numberOfNeighbours)
				.setMaxRatioBetweenSegmentSizeToCenter(maxRatioBetweenSegmentSizeToCenter)
				.setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(maxSegmentWidthToNotDiscardBecauseOfBadRatio)
				.setPadding(padding)
				.setMinNumOfSamplesToLearnFrom(minNumOfSamplesToLearnFrom)
				.setQuantile(quantile)
				.setMinMaxValue(minMaxValue)
				.build();

		Assert.assertEquals(distanceBetweenSegmentCenters, conf.getDistanceBetweenSegmentCenters(), 0.00001);
		Assert.assertEquals(numberOfNeighbours, conf.getNumberOfNeighbours(), 0.00001);
		Assert.assertEquals(maxRatioBetweenSegmentSizeToCenter, conf.getMaxRatioBetweenSegmentSizeToCenter(), 0.00001);
		Assert.assertEquals(maxSegmentWidthToNotDiscardBecauseOfBadRatio, conf.getMaxSegmentWidthToNotDiscardBecauseOfBadRatio(), 0.00001);
		Assert.assertEquals(padding, conf.getPadding(), 0.00001);
		Assert.assertEquals(minNumOfSamplesToLearnFrom, conf.getMinNumOfSamplesToLearnFrom(), 0.00001);
		Assert.assertEquals(quantile, conf.getQuantile(), 0.00001);
		Assert.assertEquals("gaussian_prior_model_builder", conf.getFactoryName());
	}
}
