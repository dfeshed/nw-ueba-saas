package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;

/**
 * NeighboursLearningSegments implements a segmenting strategy by which for every possible discrete segment center
 * a segment is created using the center's neighborhood.
 * A neighborhood is defined to be the ContinuousDataModels whose means are the closest.
 * If the neighborhood spreads too far, the segment is discarded.
 */
public class NeighboursLearningSegments implements LearningSegments {
	public NeighboursLearningSegments(List<ContinuousDataModel> models,
									  int numberOfNeighbours,
									  Iterator<Double> segmentCentersIterator,
									  double validRatioBetweenSegmentSizeAndMean) {
		Assert.notNull(models, "models can't be null");
		Assert.isTrue(numberOfNeighbours > 0, "numberOfNeighbours must be positive");
		Assert.notNull(segmentCentersIterator, "segmentCentersIterator can't be null");
		Assert.isTrue(validRatioBetweenSegmentSizeAndMean > 0, "validRatioBetweenSegmentSizeAndMean must be positive");
	}
}
