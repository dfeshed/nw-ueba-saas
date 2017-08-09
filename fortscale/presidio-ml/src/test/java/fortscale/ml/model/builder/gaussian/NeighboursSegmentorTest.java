package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.builder.gaussian.prior.NeighboursSegmentor;
import fortscale.ml.model.builder.gaussian.prior.Segmentor;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

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

	private List<ContinuousDataModel> createModels(double... means) {
		return DoubleStream.of(means)
				.mapToObj(mean -> new ContinuousDataModel().setParameters(0, mean, 0, 0))
				.collect(Collectors.toList());
	}

	@Test
	public void shouldCreateSegmentOfZeroWidthWhenNumberOfNeighboursIsOne() {
		double segmentCenter = 1;
		List<ContinuousDataModel> sortedModels = createModels(segmentCenter - 1, segmentCenter, segmentCenter + 1);
		Segmentor.Segment segment = new NeighboursSegmentor(1, 0.1, 10, 0)
				.createSegment(sortedModels, segmentCenter);
		Assert.assertEquals(segmentCenter, segment.leftMean, 0.00001);
		Assert.assertEquals(segmentCenter, segment.rightMean, 0.00001);
		Assert.assertEquals(sortedModels.subList(1, 2), segment.models);
	}

	@Test
	public void shouldNotCreateSegmentIfNotBigEnoughConcentrationOfMeans() {
		NeighboursSegmentor segments = new NeighboursSegmentor(1, 0.000001, 0, 0);
		List<ContinuousDataModel> sortedModels = createModels(0, 1);
		Assert.assertNull(segments.createSegment(sortedModels, 100.0));
	}

	@Test
	public void shouldCreateSegmentBigEnoughToContainDesiredNumberOfNeighbours() {
		double segmentCenter = 10;
		double segmentRadius = 1;
		List<ContinuousDataModel> sortedModels = createModels(
				segmentCenter - 2 * segmentRadius,
				segmentCenter - segmentRadius,
				segmentCenter,
				segmentCenter + segmentRadius,
				segmentCenter + 2 * segmentRadius
		);
		NeighboursSegmentor segments = new NeighboursSegmentor(3, 100000, 0, 0);
		Segmentor.Segment segment = segments.createSegment(sortedModels, segmentCenter);

		Assert.assertEquals(segmentCenter - segmentRadius, segment.leftMean, 0.00001);
		Assert.assertEquals(segmentCenter + segmentRadius, segment.rightMean, 0.00001);
		Assert.assertEquals(sortedModels.subList(1, 4), segment.models);
	}

	@Test
	public void shouldCreateSmallestSymmetricPossibleSegmentWhichContainDesiredNumberOfNeighbours() {
		double segmentCenter = 10;
		double segmentRadius = 1;
		List<ContinuousDataModel> sortedModels = createModels(
				segmentCenter - 2 * segmentRadius,
				segmentCenter,
				segmentCenter + segmentRadius / 2,
				segmentCenter + segmentRadius
		);
		NeighboursSegmentor segments = new NeighboursSegmentor(3, 100000, 0, 0);
		Segmentor.Segment segment = segments.createSegment(sortedModels, segmentCenter);

		Assert.assertEquals(segmentCenter - segmentRadius, segment.leftMean, 0.00001);
		Assert.assertEquals(segmentCenter + segmentRadius, segment.rightMean, 0.00001);
		Assert.assertEquals(sortedModels.subList(1, 4), segment.models);
	}

	@Test
	public void shouldAddPaddingToTheCreatedSegment() {
		double segmentCenter = 1;
		double padding = 1;
		List<ContinuousDataModel> sortedModels = createModels(segmentCenter);
		Segmentor.Segment segment = new NeighboursSegmentor(1, 0.1, 10, padding)
				.createSegment(sortedModels, segmentCenter);

		Assert.assertEquals(segmentCenter - padding, segment.leftMean, 0.00001);
		Assert.assertEquals(segmentCenter + padding, segment.rightMean, 0.00001);
		Assert.assertEquals(sortedModels, segment.models);
	}

	@Test
	public void shouldDiscardSegmentIfTooBigRelativeToItsCenter() {
		List<ContinuousDataModel> sortedModelsCluster1 = createModels(94.9, 100.0, 105.1);
		List<ContinuousDataModel> sortedModelsCluster2 = createModels(950.0, 1000.0, 1050.0);
		List<ContinuousDataModel> sortedModelsAll = Stream
				.concat(sortedModelsCluster1.stream(), sortedModelsCluster2.stream())
				.collect(Collectors.toList());
		NeighboursSegmentor segments = new NeighboursSegmentor(3, 0.1, 0, 0);

		Assert.assertNull(segments.createSegment(sortedModelsAll, 100));
		Segmentor.Segment segment = segments.createSegment(sortedModelsAll, 1000);
		Assert.assertEquals(950, segment.leftMean, 0.00001);
		Assert.assertEquals(1050, segment.rightMean, 0.00001);
		Assert.assertEquals(sortedModelsCluster2, segment.models);
	}

	@Test
	public void shouldDiscardSegmentIfTooBigRelativeToItsCenterOnlyIfBiggerThanMaxSegmentWidthToNotDiscardBecauseOfBadRatio() {
		List<ContinuousDataModel> sortedModelsCluster1 = createModels(94.9, 100.0, 105.1);
		List<ContinuousDataModel> sortedModelsCluster2 = createModels(950.0, 1000.0, 1050.0);
		List<ContinuousDataModel> sortedModelsAll = Stream
				.concat(sortedModelsCluster1.stream(), sortedModelsCluster2.stream())
				.collect(Collectors.toList());
		NeighboursSegmentor segments = new NeighboursSegmentor(3, 0.1, 100000, 0);
		Segmentor.Segment segment = segments.createSegment(sortedModelsAll, 100);

		Assert.assertEquals(94.9, segment.leftMean, 0.00001);
		Assert.assertEquals(105.1, segment.rightMean, 0.00001);
		Assert.assertEquals(sortedModelsCluster2, segment.models);
	}

	@Test
	public void shouldCreateSegmentContainingTheCenterEvenIfModelsAreOnOneSideOfTheCenter() {
		int mean = 5;
		List<ContinuousDataModel> sortedModels = createModels(mean);
		NeighboursSegmentor segments = new NeighboursSegmentor(1, 0.1, 100000, 0);
		Segmentor.Segment segment = segments.createSegment(sortedModels, 0);

		Assert.assertEquals(-mean, segment.leftMean, 0.00001);
		Assert.assertEquals(mean, segment.rightMean, 0.00001);
		Assert.assertEquals(sortedModels, segment.models);
	}
}
