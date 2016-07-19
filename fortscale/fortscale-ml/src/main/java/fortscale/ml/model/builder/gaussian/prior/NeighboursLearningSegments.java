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
			Pair<Double, Double> segment = createSegmentAroundCenter(models, segmentCenter);
			if (segment != null) {
				segments.add(segment);
			}
		}
	}

	private Pair<Double, Double> createSegmentAroundCenter(List<ContinuousDataModel> models, double segmentCenter) {
		if (models.stream()
				.mapToDouble(ContinuousDataModel::getMean)
				.filter(mean -> mean == segmentCenter)
				.findFirst()
				.isPresent()) {
			return new ImmutablePair<>(segmentCenter, segmentCenter);
		}
		return null;
	}

	@Override
	public int size() {
		return segments.size();
	}
}
