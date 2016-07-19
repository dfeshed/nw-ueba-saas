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
					(segment.getRight() - segment.getLeft()) / Math.max(0.000001, segmentCenter) <= maxRatioBetweenSegmentSizeToCenter)) {
				segment.setLeft(segment.getLeft() - padding);
				segment.setRight(segment.getRight() + padding);
				segments.add(segment);
			}
		}
	}

	private MutablePair<Double, Double> createSegmentAroundCenter(double[] sortedMeans,
																  double segmentCenter,
																  int numberOfNeighbours) {
		int firstModelToTheRightOfCenterIndex = Arrays.binarySearch(sortedMeans, segmentCenter);
		if (firstModelToTheRightOfCenterIndex < 0) {
			firstModelToTheRightOfCenterIndex = -firstModelToTheRightOfCenterIndex - 1;
		}
		MutablePair<Integer, Integer> segmentIndices = new MutablePair<>(
				(int) Math.floor(firstModelToTheRightOfCenterIndex - (numberOfNeighbours - 1) / 2),
				(int) Math.ceil(firstModelToTheRightOfCenterIndex + (numberOfNeighbours - 1) / 2)
		);
		if (segmentCenter < sortedMeans[segmentIndices.getLeft()]) {
			// Make sure the segment center is inside the segment
			segmentIndices.setLeft(segmentIndices.getLeft() - 1);
		}
		cropSegmentAccordingToBounds(segmentIndices, sortedMeans);
		if (segmentIndices.getRight() - segmentIndices.getLeft() + 1 < numberOfNeighbours) {
			// not enough neighbours
			return null;
		}
		MutablePair<Double, Double> segment = new MutablePair<>(
				sortedMeans[segmentIndices.getLeft()],
				sortedMeans[segmentIndices.getRight()]
		);
		if (segmentCenter < segment.getLeft() || segment.getRight() < segmentCenter) {
			// the segment's center must be inside the segment
			return null;
		}
		double radius = Math.max(segmentCenter - segment.getLeft(), segment.getRight() - segmentCenter);
		segment.setLeft(segmentCenter - radius);
		segment.setRight(segmentCenter + radius);
		return segment;
	}

	private void cropSegmentAccordingToBounds(MutablePair<Integer, Integer> segmentIndices, double[] sortedMeans) {
		if (segmentIndices.getLeft() < 0) {
			// there aren't enough neighbours to the left of the segment center - so extend the segment's right end
			segmentIndices.setRight(segmentIndices.getRight() + Math.abs(segmentIndices.getLeft()));
			segmentIndices.setLeft(0);
		}
		if (segmentIndices.getRight() >= sortedMeans.length) {
			// there aren't enough neighbours to the right of the segment center - so extend the segment's left end
			segmentIndices.setLeft(segmentIndices.getLeft() - Math.abs(segmentIndices.getRight()));
			segmentIndices.setRight(0);
		}
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
