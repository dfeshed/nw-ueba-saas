package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.builder.gaussian.prior.NeighboursSegmentor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

public class NeighboursSegmentorTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenZeroAsNumberOfNeighbours() {
        new NeighboursSegmentor(0, 0.1, 0, 0);
    }

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfGivenZeroAsMaxRatio() {
		new NeighboursSegmentor(100, 0, 0, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfGivenNegativeAsMaxSegmentWidthToNotDiscardBecauseOfBadRatio() {
		new NeighboursSegmentor(100, 0.1, -1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfGivenNegativePadding() {
		new NeighboursSegmentor(100, 0.1, 0, -1);
	}

	@Test
	public void shouldCreateSegmentOfZeroWidthWhenNumberOfNeighboursIsOne() {
		double segmentCenter = 1;
		Pair<Double, Double> segment = new NeighboursSegmentor(1, 0.1, 10, 0)
				.createSegment(new double[]{segmentCenter - 1, segmentCenter, segmentCenter + 1}, segmentCenter);
		Assert.assertEquals(new ImmutablePair<>(segmentCenter, segmentCenter), segment);
	}

	@Test
	public void shouldNotCreateSegmentIfNotBigEnoughConcentrationOfMeans() {
		double[] means = {0.0, 1.0};
		NeighboursSegmentor segments = new NeighboursSegmentor(1, 0.000001, 0, 0);
		Assert.assertNull(segments.createSegment(means, 100.0));
	}

	@Test
	public void shouldCreateSegmentBigEnoughToContainDesiredNumberOfNeighbours() {
		double segmentCenter = 10;
		double segmentRadius = 1;
		double[] means = {
				segmentCenter - 2 * segmentRadius,
				segmentCenter - segmentRadius,
				segmentCenter,
				segmentCenter + segmentRadius,
				segmentCenter + 2 * segmentRadius
		};
		NeighboursSegmentor segments = new NeighboursSegmentor(3, 100000, 0, 0);
		Pair<Double, Double> segment = segments.createSegment(means, segmentCenter);

		Assert.assertEquals(new ImmutablePair<>(segmentCenter - segmentRadius, segmentCenter + segmentRadius),
				segment);
	}

	@Test
	public void shouldCreateSmallestSymmetricPossibleSegmentWhichContainDesiredNumberOfNeighbours() {
		double segmentCenter = 10;
		double segmentRadius = 1;
		double[] means = {
				segmentCenter - 2 * segmentRadius,
				segmentCenter,
				segmentCenter + segmentRadius / 2,
				segmentCenter + segmentRadius
		};
		NeighboursSegmentor segments = new NeighboursSegmentor(3, 100000, 0, 0);
		Pair<Double, Double> segment = segments.createSegment(means, segmentCenter);

		Assert.assertEquals(new ImmutablePair<>(segmentCenter - segmentRadius, segmentCenter + segmentRadius),
				segment);
	}

	@Test
	public void shouldAddPaddingToTheCreatedSegment() {
		double segmentCenter = 1;
		double padding = 1;
		Pair<Double, Double> segment = new NeighboursSegmentor(1, 0.1, 10, padding)
				.createSegment(new double[]{segmentCenter}, segmentCenter);
		Assert.assertEquals(new ImmutablePair<>(segmentCenter - padding, segmentCenter + padding), segment);
	}

	@Test
	public void shouldDiscardSegmentIfTooBigRelativeToItsCenter() {
		double[] means = {94.9, 100.0, 105.1, 950.0, 1000.0, 1050.0};
		NeighboursSegmentor segments = new NeighboursSegmentor(3, 0.1, 0, 0);

		Assert.assertNull(segments.createSegment(means, 100));
		Assert.assertEquals(new ImmutablePair<>(950.0, 1050.0), segments.createSegment(means, 1000));
	}

	@Test
	public void shouldDiscardSegmentIfTooBigRelativeToItsCenterOnlyIfBiggerThanMaxSegmentWidthToNotDiscardBecauseOfBadRatio() {
		double[] means = {94.9, 100.0, 105.1, 950.0, 1000.0, 1050.0};
		NeighboursSegmentor segments = new NeighboursSegmentor(3, 0.1, 100000, 0);

		Assert.assertEquals(new ImmutablePair<>(94.9, 105.1), segments.createSegment(means, 100));
	}
}
