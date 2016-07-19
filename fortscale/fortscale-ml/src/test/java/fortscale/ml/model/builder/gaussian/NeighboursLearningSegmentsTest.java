package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.builder.gaussian.prior.NeighboursLearningSegments;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class NeighboursLearningSegmentsTest {
	private List<ContinuousDataModel> createModels(Double... means) {
		List<ContinuousDataModel> models = new ArrayList<>();
		for (Double mean : means) {
			ContinuousDataModel model = new ContinuousDataModel();
			model.setParameters(0, mean, 0, 0);
			models.add(model);
		}
		return models;
	}

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsModels() {
        new NeighboursLearningSegments(null, 100, Collections.emptyIterator(), 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenZeroAsNumberOfNeighbours() {
        new NeighboursLearningSegments(createModels(), 0, Collections.emptyIterator(), 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsSegmentCentersIterator() {
        new NeighboursLearningSegments(createModels(), 100, null, 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenZeroAsValidRatio() {
        new NeighboursLearningSegments(createModels(), 100, Collections.emptyIterator(), 0);
    }

    @Test
	public void shouldCreateNoSegmentIfThereAreNoSegmentCenters() {
		NeighboursLearningSegments segments = new NeighboursLearningSegments(
				createModels(0.0, 0.0, 0.0, 0.0, 0.0), 1, Collections.emptyIterator(), 100000);
		Assert.assertEquals(0, segments.size());
	}

    @Test
	public void shouldCreateOneSegmentPerSegmentCenterIfEnoughData() {
		Double[] segmentCenters = {0D, 1D, 2D, 3D};
		Iterator<Double> segmentCentersIterator = Arrays.asList(segmentCenters).iterator();
		NeighboursLearningSegments segments = new NeighboursLearningSegments(
				createModels(segmentCenters), 1, segmentCentersIterator, 100000);
		Assert.assertEquals(segmentCenters.length, segments.size());
	}
}
