package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilderConf;
import org.junit.Assert;
import org.junit.Test;

public class GaussianPriorModelBuilderConfTest {
	private static class GaussianPriorModelBuilderConfBuilder {
		private Double distanceBetweenSegmentCenters;
		private Integer numberOfNeighbours;
		private Double maxRatioBetweenSegmentSizeToCenter;
		private Double maxSegmentWidthToNotDiscardBecauseOfBadRatio;
		private Double padding;
		private Integer minNumOfSamplesToLearnFrom;
		private Double quantile;
		private Integer minQuantileComplementSize;
		private Double minAllowedDistFromMean;

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

		public GaussianPriorModelBuilderConfBuilder setMinQuantileComplementSize(int minQuantileComplementSize) {
			this.minQuantileComplementSize = minQuantileComplementSize;
			return this;
		}

		public GaussianPriorModelBuilderConfBuilder setMinAllowedDistFromMean(Double minAllowedDistFromMean) {
			this.minAllowedDistFromMean = minAllowedDistFromMean;
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
					minQuantileComplementSize,
					minAllowedDistFromMean
			);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfDistanceBetweenSegmentCentersIsZero() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(0)
				.setMinNumOfSamplesToLearnFrom(1)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfNumberOfNeighboursIsZero() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setNumberOfNeighbours(0)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMaxRatioBetweenSegmentSizeToCenterIsZero() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setMaxRatioBetweenSegmentSizeToCenter(0)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMaxSegmentWidthToNotDiscardBecauseOfBadRatioIsNegative() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(-1)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfPaddingIsNegative() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setPadding(-1)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinNumOfSamplesToLearnFromIsNegative() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setMinNumOfSamplesToLearnFrom(-1)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfQuantileIsNegative() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setQuantile(-1)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfQuantileIsGreaterThanOne() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setQuantile(1.1)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinQuantileComplementSizeIsNegative() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setMinQuantileComplementSize(-1)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfMinAllowedDistFromMeanIsNegative() {
		new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.setMinAllowedDistFromMean(-1.0)
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
		int minQuantileComplementSize = 7;
		Double minAllowedDistFromMean = 7.0;
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(distanceBetweenSegmentCenters)
				.setMinNumOfSamplesToLearnFrom(minNumOfSamplesToLearnFrom)
				.setNumberOfNeighbours(numberOfNeighbours)
				.setMaxRatioBetweenSegmentSizeToCenter(maxRatioBetweenSegmentSizeToCenter)
				.setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(maxSegmentWidthToNotDiscardBecauseOfBadRatio)
				.setPadding(padding)
				.setQuantile(quantile)
				.setMinQuantileComplementSize(minQuantileComplementSize)
				.setMinAllowedDistFromMean(minAllowedDistFromMean)
				.build();

		Assert.assertEquals(distanceBetweenSegmentCenters, conf.getDistanceBetweenSegmentCenters(), 0.00001);
		Assert.assertEquals(numberOfNeighbours, conf.getNumberOfNeighbours());
		Assert.assertEquals(maxRatioBetweenSegmentSizeToCenter, conf.getMaxRatioBetweenSegmentSizeToCenter(), 0.00001);
		Assert.assertEquals(maxSegmentWidthToNotDiscardBecauseOfBadRatio, conf.getMaxSegmentWidthToNotDiscardBecauseOfBadRatio(), 0.00001);
		Assert.assertEquals(padding, conf.getPadding(), 0.00001);
		Assert.assertEquals(minNumOfSamplesToLearnFrom, conf.getMinNumOfSamplesToLearnFrom());
		Assert.assertEquals(quantile, conf.getQuantile(), 0.00001);
		Assert.assertEquals(minQuantileComplementSize, conf.getMinQuantileComplementSize());
		Assert.assertEquals(minAllowedDistFromMean, conf.getMinAllowedDistFromMean(), 0.00001);
		Assert.assertEquals("gaussian_prior_model_builder", conf.getFactoryName());
	}

	@Test
	public void shouldUseProperDefaults() {
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConfBuilder()
				.setDistanceBetweenSegmentCenters(1)
				.setMinNumOfSamplesToLearnFrom(1)
				.build();

		Assert.assertEquals(10, conf.getNumberOfNeighbours());
		Assert.assertEquals(0.1, conf.getMaxRatioBetweenSegmentSizeToCenter(), 0.00001);
		Assert.assertEquals(10, conf.getMaxSegmentWidthToNotDiscardBecauseOfBadRatio(), 0.00001);
		Assert.assertEquals(1, conf.getPadding(), 0.00001);
		Assert.assertEquals(0.99, conf.getQuantile(), 0.00001);
		Assert.assertEquals(30, conf.getMinQuantileComplementSize());
		Assert.assertEquals(1, conf.getMinAllowedDistFromMean(), 0.00001);
	}
}
