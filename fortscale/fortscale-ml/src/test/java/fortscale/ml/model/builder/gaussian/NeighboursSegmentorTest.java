package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.builder.gaussian.prior.NeighboursSegmentor;
import fortscale.ml.model.builder.gaussian.prior.Segmentor;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

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

	private void assertSegmentEquals(double[] sortedMeans,
									 double expectedPadding,
									 double expectedLeftMean,
									 double expectedRightMean,
									 Segmentor.Segment actualSegment) {
		Assert.assertEquals(new Segmentor.Segment(
				expectedLeftMean,
				expectedRightMean,
				Arrays.binarySearch(sortedMeans, expectedLeftMean + expectedPadding),
				Arrays.binarySearch(sortedMeans, expectedRightMean - expectedPadding)
		), actualSegment);
	}

	private void assertSegmentEquals(double expectedLeftMean,
									 double expectedRightMean,
									 int leftModelIndex,
									 int rightModelIndex,
									 Segmentor.Segment actualSegment) {
		Assert.assertEquals(new Segmentor.Segment(
				expectedLeftMean,
				expectedRightMean,
				leftModelIndex,
				rightModelIndex
		), actualSegment);
	}

	@Test
	public void shouldCreateSegmentOfZeroWidthWhenNumberOfNeighboursIsOne() {
		double segmentCenter = 1;
		double[] sortedMeans = {segmentCenter - 1, segmentCenter, segmentCenter + 1};
		int padding = 0;
		Segmentor.Segment segment = new NeighboursSegmentor(1, 0.1, 10, padding)
				.createSegment(sortedMeans, segmentCenter);
		assertSegmentEquals(sortedMeans, padding, segmentCenter, segmentCenter, segment);
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
		double[] sortedMeans = {
				segmentCenter - 2 * segmentRadius,
				segmentCenter - segmentRadius,
				segmentCenter,
				segmentCenter + segmentRadius,
				segmentCenter + 2 * segmentRadius
		};
		int padding = 0;
		NeighboursSegmentor segments = new NeighboursSegmentor(3, 100000, 0, padding);
		Segmentor.Segment segment = segments.createSegment(sortedMeans, segmentCenter);

		assertSegmentEquals(sortedMeans, padding, segmentCenter - segmentRadius, segmentCenter + segmentRadius, segment);
	}

	@Test
	public void shouldCreateSmallestSymmetricPossibleSegmentWhichContainDesiredNumberOfNeighbours() {
		double segmentCenter = 10;
		double segmentRadius = 1;
		double[] sortedMeans = {
				segmentCenter - 2 * segmentRadius,
				segmentCenter,
				segmentCenter + segmentRadius / 2,
				segmentCenter + segmentRadius
		};
		int padding = 0;
		NeighboursSegmentor segments = new NeighboursSegmentor(3, 100000, 0, padding);
		Segmentor.Segment segment = segments.createSegment(sortedMeans, segmentCenter);

		assertSegmentEquals(segmentCenter - segmentRadius, segmentCenter + segmentRadius, 1, 3, segment);
	}

	@Test
	public void shouldAddPaddingToTheCreatedSegment() {
		double segmentCenter = 1;
		double padding = 1;
		double[] sortedMeans = {segmentCenter};
		Segmentor.Segment segment = new NeighboursSegmentor(1, 0.1, 10, padding)
				.createSegment(sortedMeans, segmentCenter);
		assertSegmentEquals(sortedMeans, padding, segmentCenter - padding, segmentCenter + padding, segment);
	}

	@Test
	public void shouldDiscardSegmentIfTooBigRelativeToItsCenter() {
		double[] sortedMeans = {94.9, 100.0, 105.1, 950.0, 1000.0, 1050.0};
		int padding = 0;
		NeighboursSegmentor segments = new NeighboursSegmentor(3, 0.1, 0, padding);

		Assert.assertNull(segments.createSegment(sortedMeans, 100));
		assertSegmentEquals(sortedMeans, padding, 950, 1050, segments.createSegment(sortedMeans, 1000));
	}

	@Test
	public void shouldDiscardSegmentIfTooBigRelativeToItsCenterOnlyIfBiggerThanMaxSegmentWidthToNotDiscardBecauseOfBadRatio() {
		double[] sortedMeans = {94.9, 100.0, 105.1, 950.0, 1000.0, 1050.0};
		int padding = 0;
		NeighboursSegmentor segments = new NeighboursSegmentor(3, 0.1, 100000, padding);

		assertSegmentEquals(sortedMeans, padding,94.9, 105.1, segments.createSegment(sortedMeans, 100));
	}

	@Test
	public void shouldCreateSegmentContainingTheCenterEvenIfModelsAreOnOneSideOfTheCenter() {
		int mean = 5;
		NeighboursSegmentor segments = new NeighboursSegmentor(1, 0.1, 100000, 0);
		Segmentor.Segment segment = segments.createSegment(new double[]{mean}, 0);

		assertSegmentEquals(-mean, mean, 0, 0, segment);
	}
}
