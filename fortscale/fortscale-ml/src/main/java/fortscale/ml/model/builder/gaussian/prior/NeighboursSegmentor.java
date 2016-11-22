package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
		int meanIndexClosestToMean = Collections.binarySearch(
				sortedModels,
				new ContinuousDataModel().setParameters(0, mean, 0, 0), Comparator.comparing(ContinuousDataModel::getMean)
		);
		if (meanIndexClosestToMean < 0) {
			meanIndexClosestToMean = -meanIndexClosestToMean - 1;
			if (meanIndexClosestToMean == sortedModels.size()) {
				meanIndexClosestToMean--;
			} else if (meanIndexClosestToMean > 0 &&
					mean - sortedModels.get(meanIndexClosestToMean - 1).getMean() < sortedModels.get(meanIndexClosestToMean).getMean() - mean) {
				meanIndexClosestToMean--;
			}
		}
		return meanIndexClosestToMean;
	}

	@Override
	public Segment createSegment(List<ContinuousDataModel> sortedModels, double segmentCenter) {
		Double segmentWidth = findBestSegmentWidth(sortedModels, segmentCenter);
		if (segmentWidth == null) {
			return null;
		}
		double leftMean = segmentCenter - (segmentWidth / 2 + padding);
		double rightMean = segmentCenter + segmentWidth / 2 + padding;
		int meanIndexClosestToLeftMean = findModelIndexClosestToMean(sortedModels, leftMean - Double.MIN_VALUE);
		if (sortedModels.get(meanIndexClosestToLeftMean).getMean() < leftMean) {
			meanIndexClosestToLeftMean++;
		}
		int meanIndexClosestToRightMean = findModelIndexClosestToMean(sortedModels, rightMean + Double.MIN_VALUE);
		if (sortedModels.get(meanIndexClosestToRightMean).getMean() > rightMean) {
			meanIndexClosestToRightMean--;
		}
		return new Segment(
				leftMean,
				rightMean,
				sortedModels.subList(meanIndexClosestToLeftMean, meanIndexClosestToRightMean + 1)
		);
	}

	private Double findBestSegmentWidth(List<ContinuousDataModel> sortedModels, double segmentCenter) {
		int meanIndexClosestToSegmentCenter = findModelIndexClosestToMean(sortedModels, segmentCenter);
		return IntStream.range(-numberOfNeighbours + 1, 1)
				// create segment candidates (which contain meanIndexClosestToSegmentCenter neighbours)
				.mapToObj(offset -> new ImmutablePair<>(
						meanIndexClosestToSegmentCenter + offset,
						meanIndexClosestToSegmentCenter + offset + numberOfNeighbours - 1
				))
				// filter out ones which will cause ArrayIndexOutOfBoundsException
				.filter(segmentIndices -> segmentIndices.left >= 0 && segmentIndices.right < sortedModels.size())
				// map a segment candidate (indices of models) to the width of its means segment
				// (the means interval which it contains). The means segment is symmetric around the center
				.map(segmentIndices -> 2 * Math.max(
						segmentCenter - sortedModels.get(segmentIndices.getLeft()).getMean(),
						sortedModels.get(segmentIndices.getRight()).getMean() - segmentCenter
				))
				// find the thinnest segment
				.min(Double::compare)
				// make sure it's thin enough
				.filter(width -> width <= Math.max(
						maxRatioBetweenSegmentSizeToCenter * segmentCenter,
						maxSegmentWidthToNotDiscardBecauseOfBadRatio
				))
				// there are no candidates at all (not enough models) or they are too wide
				.orElse(null);
	}
}
