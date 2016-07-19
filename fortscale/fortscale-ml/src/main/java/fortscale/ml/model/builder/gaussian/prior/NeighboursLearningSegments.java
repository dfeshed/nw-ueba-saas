package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

import java.util.ArrayList;
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
									  double validRatioBetweenSegmentSizeAndMean) {
		Assert.notNull(models, "models can't be null");
		Assert.isTrue(numberOfNeighbours > 0, "numberOfNeighbours must be positive");
		Assert.notNull(segmentCenters, "segmentCenters can't be null");
		Assert.isTrue(validRatioBetweenSegmentSizeAndMean > 0, "validRatioBetweenSegmentSizeAndMean must be positive");
		segments = new ArrayList<>();
		for (double segmentCenter : segmentCenters) {
			Pair<Double, Double> segment = createSegmentAroundCenter(models, segmentCenter, numberOfNeighbours);
			if (segment != null && (segment.getRight() - segment.getLeft()) / Math.max(0.000001, segmentCenter) < validRatioBetweenSegmentSizeAndMean) {
				segments.add(segment);
			}
		}
	}

	private Pair<Double, Double> createSegmentAroundCenter(List<ContinuousDataModel> models,
														   double segmentCenter,
														   int numberOfNeighbours) {
		int firstModelToTheRightOfCenterIndex = 0;
		while (firstModelToTheRightOfCenterIndex < models.size() - 1 &&
				models.get(firstModelToTheRightOfCenterIndex).getMean() < segmentCenter) {
			firstModelToTheRightOfCenterIndex++;
		}
		MutablePair<Integer, Integer> segmentIndices = new MutablePair<>(
				(int) Math.floor(firstModelToTheRightOfCenterIndex - (numberOfNeighbours - 1) / 2),
				(int) Math.ceil(firstModelToTheRightOfCenterIndex + (numberOfNeighbours - 1) / 2)
		);
		if (segmentCenter < models.get(segmentIndices.getLeft()).getMean()) {
			// Make sure the segment center is inside the segment
			segmentIndices.setLeft(segmentIndices.getLeft() - 1);
		}
		cropSegmentAccordingToBounds(segmentIndices, models);
		if (segmentIndices.getRight() - segmentIndices.getLeft() + 1 < numberOfNeighbours) {
			// not enough neighbours
			return null;
		}
		ImmutablePair<Double, Double> segment = new ImmutablePair<>(
				models.get(segmentIndices.getLeft()).getMean(),
				models.get(segmentIndices.getRight()).getMean()
		);
		if (segmentCenter < segment.getLeft() || segment.getRight() < segmentCenter) {
			// the segment's center must be inside the segment
			return null;
		}
		return segment;
	}

	private void cropSegmentAccordingToBounds(MutablePair<Integer, Integer> segmentIndices,
											  List<ContinuousDataModel> models) {
		if (segmentIndices.getLeft() < 0) {
			// there aren't enough neighbours to the left of the segment center - so extend the segment's right end
			segmentIndices.setRight(segmentIndices.getRight() + Math.abs(segmentIndices.getLeft()));
			segmentIndices.setLeft(0);
		}
		if (segmentIndices.getRight() >= models.size()) {
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
