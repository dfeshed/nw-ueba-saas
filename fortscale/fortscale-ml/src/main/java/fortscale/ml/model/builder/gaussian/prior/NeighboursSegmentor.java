package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * NeighboursLearningSegments implements a segmenting strategy by which for every possible discrete segment center
 * a segment is created using the center's neighborhood.
 * A neighborhood is defined to be the ContinuousDataModels whose means are the closest.
 * If the neighborhood spreads too far, the segment is discarded.
 */
public class NeighboursSegmentor implements Segmentor {
	private int numberOfNeighbours;
	private double maxRatioBetweenSegmentSizeToCenter;
	private double maxSegmentWidthToNotDiscardBecauseOfBadRatio;
	private double padding;

	public NeighboursSegmentor(int numberOfNeighbours,
							   double maxRatioBetweenSegmentSizeToCenter,
							   double maxSegmentWidthToNotDiscardBecauseOfBadRatio,
							   double padding) {
		Assert.isTrue(numberOfNeighbours > 0, "numberOfNeighbours must be positive");
		Assert.isTrue(maxRatioBetweenSegmentSizeToCenter > 0, "maxRatioBetweenSegmentSizeToCenter must be positive");
		Assert.isTrue(maxSegmentWidthToNotDiscardBecauseOfBadRatio >= 0, "maxSegmentWidthToNotDiscardBecauseOfBadRatio must be non-negative");
		Assert.isTrue(padding >= 0, "padding must be non-negative");
		this.numberOfNeighbours = numberOfNeighbours;
		this.maxRatioBetweenSegmentSizeToCenter = maxRatioBetweenSegmentSizeToCenter;
		this.maxSegmentWidthToNotDiscardBecauseOfBadRatio = maxSegmentWidthToNotDiscardBecauseOfBadRatio;
		this.padding = padding;
	}

	private int findModelIndexClosestToMean(List<ContinuousDataModel> sortedModels, double mean) {
		int meanIndexClosestToCenter = Collections.binarySearch(
				sortedModels,
				new ContinuousDataModel().setParameters(0, mean, 0, 0), Comparator.comparing(ContinuousDataModel::getMean)
		);
		if (meanIndexClosestToCenter < 0) {
			meanIndexClosestToCenter = -meanIndexClosestToCenter - 1;
			if (meanIndexClosestToCenter == sortedModels.size()) {
				meanIndexClosestToCenter--;
			} else if (meanIndexClosestToCenter > 0 &&
					mean - sortedModels.get(meanIndexClosestToCenter - 1).getMean() < sortedModels.get(meanIndexClosestToCenter).getMean() - mean) {
				meanIndexClosestToCenter--;
			}
		}
		return meanIndexClosestToCenter;
	}

	@Override
	public Segment createSegment(List<ContinuousDataModel> sortedModels, double segmentCenter) {
		int meanIndexClosestToSegmentCenter = findModelIndexClosestToMean(sortedModels, segmentCenter);
		Double segmentMeansRadius = IntStream.range(-numberOfNeighbours + 1, 1)
				// create segment candidates
				.mapToObj(offset -> new ImmutablePair<>(
						meanIndexClosestToSegmentCenter + offset,
						meanIndexClosestToSegmentCenter + offset + numberOfNeighbours - 1
				))
				// filter out ones which will cause ArrayIndexOutOfBoundsException
				.filter(segmentIndices -> segmentIndices.left >= 0 && segmentIndices.right < sortedModels.size())
				// map a segment candidate (indices of models) to the radius of its means segment
				// (the means interval which it contains). The means segment is be symmetric around the center
				.map(segmentIndices -> Math.max(
						segmentCenter - sortedModels.get(segmentIndices.getLeft()).getMean(),
						sortedModels.get(segmentIndices.getRight()).getMean() - segmentCenter
				))
				// find the thinnest segment
				.min(Double::compare)
				// make sure it's thin enough
				.filter(radius -> 2 * radius <= Math.max(
						maxRatioBetweenSegmentSizeToCenter * segmentCenter,
						maxSegmentWidthToNotDiscardBecauseOfBadRatio
				))
				.orElse(null);
		if (segmentMeansRadius == null) {
			// there are no candidates at all (not enough models) or they are too wide
			return null;
		}
		Pair<Double, Double> paddedSegmentMeans =
				new ImmutablePair<>(segmentCenter - segmentMeansRadius - padding, segmentCenter + segmentMeansRadius + padding);
		Pair<Integer, Integer> segmentIndices = createSegmentIndicesFromSegmentMeans(paddedSegmentMeans, sortedModels);
		return new Segment(
				paddedSegmentMeans.getLeft(),
				paddedSegmentMeans.getRight(),
				sortedModels.subList(segmentIndices.getLeft(), segmentIndices.getRight() + 1)
		);
	}

	private Pair<Integer, Integer> createSegmentIndicesFromSegmentMeans(Pair<Double, Double> segmentMeans,
																		List<ContinuousDataModel> sortedModels) {
		int meanIndexClosestToLeftMean = findModelIndexClosestToMean(sortedModels, segmentMeans.getLeft() - Double.MIN_VALUE);
		if (sortedModels.get(meanIndexClosestToLeftMean).getMean() < segmentMeans.getLeft()) {
			meanIndexClosestToLeftMean++;
		}
		int meanIndexClosestToRightMean = findModelIndexClosestToMean(sortedModels, segmentMeans.getRight() + Double.MIN_VALUE);
		if (sortedModels.get(meanIndexClosestToRightMean).getMean() > segmentMeans.getRight()) {
			meanIndexClosestToRightMean--;
		}
		return new ImmutablePair<>(meanIndexClosestToLeftMean, meanIndexClosestToRightMean);
	}
}
