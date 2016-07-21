package fortscale.ml.model.builder.gaussian.prior;

import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.util.Assert;

import java.util.Arrays;

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

	private MutablePair<Integer, Integer> enlargeSegmentBySmallestPossibleAddition(double[] sortedMeans, double segmentCenter, MutablePair<Integer, Integer> segmentIndices) {
		if (segmentIndices.left == 0 && segmentIndices.right < sortedMeans.length - 1) {
			// nothing to add from the left - we must add from the right
			segmentIndices.right++;
		} else if (segmentIndices.left > 0 && segmentIndices.right == sortedMeans.length - 1) {
			// nothing to add from the right - we must add from the left
			segmentIndices.left--;
		} else if (segmentIndices.left == 0 && segmentIndices.right == sortedMeans.length - 1) {
			// nothing to add from the left or right - fail
			return null;
		} else {
			// try expanding left
			MutablePair<Integer, Integer> segmentIndicesAdvancedLeft =
					new MutablePair<>(segmentIndices.left - 1, segmentIndices.right);
			expandSegmentIndicesUntilSymmetricAroundCenter(segmentIndicesAdvancedLeft, segmentCenter, sortedMeans);
			// try expanding right
			MutablePair<Integer, Integer> segmentIndicesAdvancedRight =
					new MutablePair<>(segmentIndices.left, segmentIndices.right + 1);
			expandSegmentIndicesUntilSymmetricAroundCenter(segmentIndicesAdvancedRight, segmentCenter, sortedMeans);
			// pick the thinner segment option
			segmentIndices =
					(getNumOfMeansInsideSegment(segmentIndicesAdvancedLeft) < getNumOfMeansInsideSegment(segmentIndicesAdvancedRight)) ?
							segmentIndicesAdvancedLeft :
							segmentIndicesAdvancedRight;
		}
		return segmentIndices;
	}

	private int findMeanIndexClosestToSegmentCenter(double[] sortedMeans, double segmentCenter) {
		int meanIndexClosestToCenter = Arrays.binarySearch(sortedMeans, segmentCenter);
		if (meanIndexClosestToCenter < 0) {
			meanIndexClosestToCenter = -meanIndexClosestToCenter - 1;
			if (meanIndexClosestToCenter == sortedMeans.length) {
				meanIndexClosestToCenter--;
			} else if (meanIndexClosestToCenter > 0 &&
					segmentCenter - sortedMeans[meanIndexClosestToCenter - 1] < sortedMeans[meanIndexClosestToCenter] - segmentCenter) {
				meanIndexClosestToCenter--;
			}
		}
		return meanIndexClosestToCenter;
	}

	private void expandSegmentIndicesUntilSymmetricAroundCenter(MutablePair<Integer, Integer> segmentIndices,
																double segmentCenter,
																double[] sortedMeans) {
		double distFromLeft = segmentCenter - sortedMeans[segmentIndices.left];
		double distFromRight = sortedMeans[segmentIndices.right] - segmentCenter;
		if (distFromLeft < distFromRight) {
			segmentIndices.left = Arrays.binarySearch(sortedMeans, segmentCenter - distFromRight);
			if (segmentIndices.left < 0) {
				segmentIndices.left = -segmentIndices.left - 1;
			}
		} else if (distFromLeft > distFromRight) {
			segmentIndices.right = Arrays.binarySearch(sortedMeans, segmentCenter + distFromLeft);
			if (segmentIndices.right < 0) {
				segmentIndices.right = -segmentIndices.right - 1;
				if (segmentIndices.right == sortedMeans.length) {
					segmentIndices.right--;
				}
			}
		}
	}

	private void expandSegmentIndicesWithoutChangingWidth(MutablePair<Integer, Integer> segmentIndices, double[] sortedMeans) {
		while (segmentIndices.left > 0 && sortedMeans[segmentIndices.left] == sortedMeans[segmentIndices.left - 1]) {
			segmentIndices.left--;
		}
		while (segmentIndices.right < sortedMeans.length - 1 && sortedMeans[segmentIndices.right] == sortedMeans[segmentIndices.right + 1]) {
			segmentIndices.right++;
		}
	}

	private int getNumOfMeansInsideSegment(MutablePair<Integer, Integer> segmentIndices) {
		return segmentIndices.right - segmentIndices.left + 1;
	}

	private MutablePair<Double, Double> createMeansSegmentFromIndices(MutablePair<Integer, Integer> segmentIndices,
																	  double segmentCenter,
																	  double[] sortedMeans) {
		MutablePair<Double, Double> segment = new MutablePair<>(
				sortedMeans[segmentIndices.left],
				sortedMeans[segmentIndices.right]
		);
		if (segmentCenter < segment.left || segment.right < segmentCenter) {
			// the segment's center must be inside the segment
			return null;
		}
		double radius = Math.max(segmentCenter - segment.left, segment.right - segmentCenter);
		segment.left = segmentCenter - radius;
		segment.right = segmentCenter + radius;

		if (segment.right - segment.left > Math.max(
				maxRatioBetweenSegmentSizeToCenter * segmentCenter,
				maxSegmentWidthToNotDiscardBecauseOfBadRatio
		)) {
			return null;
		}
		segment.left -= padding;
		segment.right += padding;

		return segment;
	}

	@Override
	public Segment createSegment(double[] sortedMeans, double segmentCenter) {
		int meanIndexClosestToSegmentCenter = findMeanIndexClosestToSegmentCenter(sortedMeans, segmentCenter);
		MutablePair<Integer, Integer> segmentIndices = new MutablePair<>(
				meanIndexClosestToSegmentCenter,
				meanIndexClosestToSegmentCenter
		);
		expandSegmentIndicesWithoutChangingWidth(segmentIndices, sortedMeans);
		while (getNumOfMeansInsideSegment(segmentIndices) < numberOfNeighbours) {
			segmentIndices = enlargeSegmentBySmallestPossibleAddition(sortedMeans, segmentCenter, segmentIndices);
			if (segmentIndices == null) {
				return null;
			}
			expandSegmentIndicesWithoutChangingWidth(segmentIndices, sortedMeans);
		}
		MutablePair<Double, Double> meansSegment = createMeansSegmentFromIndices(segmentIndices, segmentCenter, sortedMeans);
		if (meansSegment == null) {
			return null;
		}
		return new Segment(meansSegment.left, meansSegment.right, segmentIndices.left, segmentIndices.right);
	}
}
