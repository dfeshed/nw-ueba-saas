package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NeighboursLearningSegments implements a segmenting strategy by which for every possible discrete segment center
 * a segment is created using the center's neighborhood.
 * A neighborhood is defined to be the ContinuousDataModels whose means are the closest.
 * If the neighborhood spreads too far, the segment is discarded.
 */
public class NeighboursLearningSegments implements LearningSegments {
	private List<Pair<Double, Double>> segments;

	public NeighboursLearningSegments(List<ContinuousDataModel> models,
									  int numberOfNeighbours,
									  Iterable<Double> segmentCenters,
									  double maxRatioBetweenSegmentSizeToCenter,
									  double maxCenterToNotDiscardBecauseOfBadRatio,
									  double padding) {
		Assert.notNull(models, "models can't be null");
		Assert.isTrue(numberOfNeighbours > 0, "numberOfNeighbours must be positive");
		Assert.notNull(segmentCenters, "segmentCenters can't be null");
		Assert.isTrue(maxRatioBetweenSegmentSizeToCenter > 0, "maxRatioBetweenSegmentSizeToCenter must be positive");
		Assert.isTrue(maxCenterToNotDiscardBecauseOfBadRatio >= 0, "maxCenterToNotDiscardBecauseOfBadRatio must be non-negative");
		Assert.isTrue(padding >= 0, "padding must be non-negative");
		segments = new ArrayList<>();
		for (double segmentCenter : segmentCenters) {
			double[] sortedMeans = models.stream()
					.mapToDouble(ContinuousDataModel::getMean)
					.sorted()
					.toArray();
			MutablePair<Double, Double> segment = createSegmentAroundCenter(sortedMeans, segmentCenter, numberOfNeighbours);
			if (segment != null && (segmentCenter <= maxCenterToNotDiscardBecauseOfBadRatio ||
					(segment.right - segment.left) / Math.max(0.000001, segmentCenter) <= maxRatioBetweenSegmentSizeToCenter)) {
				segment.left -= padding;
				segment.right += padding;
				segments.add(segment);
			}
		}
	}

	private MutablePair<Double, Double> createSegmentAroundCenter(double[] sortedMeans,
																  double segmentCenter,
																  int numberOfNeighbours) {
		int modelIndexClosestToSegmentCenter = findModelIndexClosestToSegmentCenter(sortedMeans, segmentCenter);
		MutablePair<Integer, Integer> segmentIndices = new MutablePair<>(
				modelIndexClosestToSegmentCenter,
				modelIndexClosestToSegmentCenter
		);
		expandSegmentIndicesWithoutChangingWidth(segmentIndices, sortedMeans);
		while (getNumOfModelsInsideSegment(segmentIndices) < numberOfNeighbours) {
			segmentIndices = enlargeSegmentBySmallestPossibleAddition(sortedMeans, segmentCenter, segmentIndices);
			if (segmentIndices == null) {
				return null;
			}
			expandSegmentIndicesWithoutChangingWidth(segmentIndices, sortedMeans);
		}
		return createSegment(segmentIndices, segmentCenter, sortedMeans);
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
					(getNumOfModelsInsideSegment(segmentIndicesAdvancedLeft) < getNumOfModelsInsideSegment(segmentIndicesAdvancedRight)) ?
							segmentIndicesAdvancedLeft :
							segmentIndicesAdvancedRight;
		}
		return segmentIndices;
	}

	private MutablePair<Double, Double> createSegment(MutablePair<Integer, Integer> segmentIndices,
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
		return segment;
	}

	private int findModelIndexClosestToSegmentCenter(double[] sortedMeans, double segmentCenter) {
		int modelIndexClosestToCenter = Arrays.binarySearch(sortedMeans, segmentCenter);
		if (modelIndexClosestToCenter < 0) {
			modelIndexClosestToCenter = -modelIndexClosestToCenter - 1;
			if (modelIndexClosestToCenter == sortedMeans.length) {
				modelIndexClosestToCenter--;
			} else if (segmentCenter - sortedMeans[modelIndexClosestToCenter - 1] < sortedMeans[modelIndexClosestToCenter] - segmentCenter) {
				modelIndexClosestToCenter--;
			}
		}
		return modelIndexClosestToCenter;
	}

	private void expandSegmentIndicesUntilSymmetricAroundCenter(MutablePair<Integer, Integer> segmentIndices,
																double segmentCenter,
																double[] sortedMeans) {
		double distFromLeft = segmentCenter - sortedMeans[segmentIndices.left];
		double distFromRight = sortedMeans[segmentIndices.right] - segmentCenter;
		if (distFromLeft < distFromRight) {
			while (segmentIndices.left > 0 && segmentCenter - sortedMeans[segmentIndices.left - 1] < distFromRight) {
				segmentIndices.left--;
			}
		} else if (distFromLeft > distFromRight) {
			while (segmentIndices.right < sortedMeans.length - 1 && distFromLeft > sortedMeans[segmentIndices.right + 1] - segmentCenter) {
				segmentIndices.right++;
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

	private int getNumOfModelsInsideSegment(MutablePair<Integer, Integer> segmentIndices) {
		return segmentIndices.right - segmentIndices.left + 1;
	}

	@Override
	public int size() {
		return segments.size();
	}

	@Override
	public Pair<Double, Double> get(int index) {
		return segments.get(index);
	}
}
