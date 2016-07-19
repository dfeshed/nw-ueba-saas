package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
														   double numberOfNeighbours) {
		int firstModelToTheRightOfCenterIndex = 0;
		while (firstModelToTheRightOfCenterIndex < models.size() - 1 &&
				models.get(firstModelToTheRightOfCenterIndex).getMean() < segmentCenter) {
			firstModelToTheRightOfCenterIndex++;
		}
		int leftModelIndex = (int) Math.floor(firstModelToTheRightOfCenterIndex - (numberOfNeighbours - 1) / 2);
		int rightModelIndex = (int) Math.ceil(firstModelToTheRightOfCenterIndex + (numberOfNeighbours - 1) / 2);
		if (segmentCenter < models.get(leftModelIndex).getMean()) {
			// Make sure the segment center is inside the segment
			leftModelIndex--;
		}
		if (leftModelIndex < 0) {
			// there aren't enough neighbours to the left of the segment center - so extend the segment's right end
			rightModelIndex += Math.abs(leftModelIndex);
			leftModelIndex = 0;
		}
		if (rightModelIndex >= models.size()) {
			// there aren't enough neighbours to the right of the segment center - so extend the segment's left end
			leftModelIndex -= Math.abs(rightModelIndex);
			rightModelIndex = 0;
		}
		if (rightModelIndex - leftModelIndex + 1 < numberOfNeighbours) {
			// not enough neighbours
			return null;
		}
		ImmutablePair<Double, Double> segment = new ImmutablePair<>(models.get(leftModelIndex).getMean(), models.get(rightModelIndex).getMean());
		if (segmentCenter < segment.getLeft() || segment.getRight() < segmentCenter) {
			// the segment's center must be inside the segment
			return null;
		}
		return segment;
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
