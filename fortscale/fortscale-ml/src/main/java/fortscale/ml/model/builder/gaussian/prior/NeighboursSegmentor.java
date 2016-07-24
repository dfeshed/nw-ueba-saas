package fortscale.ml.model.builder.gaussian.prior;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Comparator;
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

	private int findMeanIndexClosestToMean(double[] sortedMeans, double mean) {
		int meanIndexClosestToCenter = Arrays.binarySearch(sortedMeans, mean);
		if (meanIndexClosestToCenter < 0) {
			meanIndexClosestToCenter = -meanIndexClosestToCenter - 1;
			if (meanIndexClosestToCenter == sortedMeans.length) {
				meanIndexClosestToCenter--;
			} else if (meanIndexClosestToCenter > 0 &&
					mean - sortedMeans[meanIndexClosestToCenter - 1] < sortedMeans[meanIndexClosestToCenter] - mean) {
				meanIndexClosestToCenter--;
			}
		}
		return meanIndexClosestToCenter;
	}

	private void expandSegmentIndicesWithoutChangingWidth(MutablePair<Integer, Integer> segmentIndices, double[] sortedMeans) {
		while (segmentIndices.left > 0 && sortedMeans[segmentIndices.left] == sortedMeans[segmentIndices.left - 1]) {
			segmentIndices.left--;
		}
		while (segmentIndices.right < sortedMeans.length - 1 && sortedMeans[segmentIndices.right] == sortedMeans[segmentIndices.right + 1]) {
			segmentIndices.right++;
		}
	}

	@Override
	public Segment createSegment(double[] sortedMeans, double segmentCenter) {
		int meanIndexClosestToSegmentCenter = findMeanIndexClosestToMean(sortedMeans, segmentCenter);
		return IntStream.range(-numberOfNeighbours, 1)
				// create segment candidates
				.mapToObj(offset -> new ImmutablePair<>(
						meanIndexClosestToSegmentCenter + offset,
						meanIndexClosestToSegmentCenter + offset + numberOfNeighbours - 1
				))
				// filter out ones which will cause ArrayIndexOutOfBoundsException
				.filter(segmentIndices -> segmentIndices.left >= 0 && segmentIndices.right < sortedMeans.length)
				// map a segment candidate (indices of models) to its means segment (the means interval which
				// it contains). Do it such that the means will be symmetric around the center
				.map(segmentIndices -> createSymmetricSegmentMeansFromSegmentIndices(
						segmentIndices,
						segmentCenter,
						sortedMeans
				))
				// find the thinner segment
				.min(Comparator.comparingDouble(symmetricSegmentMeans -> symmetricSegmentMeans.getRight() - symmetricSegmentMeans.getLeft()))
				// make sure it's thin enough
				.filter(symmetricSegmentMeans -> symmetricSegmentMeans.getRight() - symmetricSegmentMeans.getLeft() <= Math.max(
						maxRatioBetweenSegmentSizeToCenter * segmentCenter,
						maxSegmentWidthToNotDiscardBecauseOfBadRatio
				))
				// add some padding
				.map(symmetricSegmentMeans -> new ImmutablePair<>(
						symmetricSegmentMeans.getLeft() - padding,
						symmetricSegmentMeans.getRight() + padding
				))
				// create a result Segment which contains the model indices with mean inside the padded segment
				.map(paddedSymmetricSegmentMeans -> {
					Pair<Integer, Integer> segmentIndices =
							createSegmentIndicesFromSegmentMeans(paddedSymmetricSegmentMeans, sortedMeans);
					return new Segment(
							paddedSymmetricSegmentMeans.getLeft(),
							paddedSymmetricSegmentMeans.getRight(),
							segmentIndices.getLeft(),
							segmentIndices.getRight()
					);
				})
				// resort to null if there are no candidates at all (not enough models) or they are too wide
				.orElse(null);
	}

	private Pair<Double, Double> createSymmetricSegmentMeansFromSegmentIndices(Pair<Integer, Integer> segmentIndices,
																			   double segmentCenter,
																			   double[] sortedMeans) {
		MutablePair<Double, Double> segment = new MutablePair<>(
				sortedMeans[segmentIndices.getLeft()],
				sortedMeans[segmentIndices.getRight()]
		);
		if (segmentCenter < segment.left) {
			segment.left = segmentCenter - (segment.right - segmentCenter);
		}
		if (segment.right < segmentCenter) {
			segment.right = segmentCenter + (segmentCenter - segment.left);
		}
		double radius = Math.max(segmentCenter - segment.left, segment.right - segmentCenter);
		segment.left = segmentCenter - radius;
		segment.right = segmentCenter + radius;
		return segment;
	}

	private Pair<Integer, Integer> createSegmentIndicesFromSegmentMeans(Pair<Double, Double> segmentMeans,
																		double[] sortedMeans) {
		int meanIndexClosestToLeftMean = findMeanIndexClosestToMean(sortedMeans, segmentMeans.getLeft());
		if (sortedMeans[meanIndexClosestToLeftMean] < segmentMeans.getLeft()) {
			meanIndexClosestToLeftMean++;
		}
		int meanIndexClosestToRightMean = findMeanIndexClosestToMean(sortedMeans, segmentMeans.getRight());
		if (sortedMeans[meanIndexClosestToRightMean] > segmentMeans.getRight()) {
			meanIndexClosestToRightMean--;
		}
		MutablePair<Integer, Integer> segmentIndices =
				new MutablePair<>(meanIndexClosestToLeftMean, meanIndexClosestToRightMean);
		expandSegmentIndicesWithoutChangingWidth(segmentIndices, sortedMeans);
		return segmentIndices;
	}
}
