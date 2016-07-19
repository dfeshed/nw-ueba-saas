package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.builder.gaussian.prior.NeighboursLearningSegments;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        new NeighboursLearningSegments(null, 100, Collections.emptyList(), 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenZeroAsNumberOfNeighbours() {
        new NeighboursLearningSegments(createModels(), 0, Collections.emptyList(), 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsSegmentCentersIterator() {
        new NeighboursLearningSegments(createModels(), 100, null, 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenZeroAsValidRatio() {
        new NeighboursLearningSegments(createModels(), 100, Collections.emptyList(), 0);
    }

    @Test
	public void shouldCreateNoSegmentIfThereAreNoSegmentCenters() {
		NeighboursLearningSegments segments = new NeighboursLearningSegments(
				createModels(0.0, 0.0, 0.0, 0.0, 0.0), 1, Collections.emptyList(), 100000);
		Assert.assertEquals(0, segments.size());
	}

	@Test
	public void shouldCreateOneSegmentPerSegmentCenterIfEnoughData() {
		Double[] segmentCenters = {0D, 1D, 2D, 3D};
		NeighboursLearningSegments segments = new NeighboursLearningSegments(
				createModels(segmentCenters), 1, Arrays.asList(segmentCenters), 100000);
		Assert.assertEquals(segmentCenters.length, segments.size());
	}

	@Test
	public void shouldCreateSegmentsOnlyForCentersWhichContainEnoughConcentrationOfMeans() {
		Iterable<Double> segmentCentersIterator = Arrays.asList(0D, 1D, 2D, 3D);
		Double[] means = {0D, 3D};
		NeighboursLearningSegments segments = new NeighboursLearningSegments(
				createModels(means), 1, segmentCentersIterator, 0.000001);
		Assert.assertEquals(means.length, segments.size());
	}

	@Test
	public void shouldCreateSegmentBigEnoughToContainDesiredNumberOfNeighbours() {
		Double[] segmentCenters = {1D};
		List<ContinuousDataModel> models = createModels(0.0, 0.5, 1.0, 1.5, 2.0);
		NeighboursLearningSegments segments = new NeighboursLearningSegments(
				models, 3, Arrays.asList(segmentCenters), 100000);
		Assert.assertEquals(1, segments.size());
		Pair<Double, Double> segment = segments.get(0);
		Assert.assertEquals(0.5, segment.getLeft(), 0.00001);
		Assert.assertEquals(1.5, segment.getRight(), 0.00001);
	}

	@Test
	public void shouldCreateSegmentSymmetricallyAroundCenter() {
		Double[] segmentCenters = {1D};
		List<ContinuousDataModel> models = createModels(0.0, 1.0, 1.1);
		NeighboursLearningSegments segments = new NeighboursLearningSegments(
				models, 3, Arrays.asList(segmentCenters), 100000);
		Assert.assertEquals(1, segments.size());
		Pair<Double, Double> segment = segments.get(0);
		Assert.assertEquals(0.0, segment.getLeft(), 0.00001);
		Assert.assertEquals(2.0, segment.getRight(), 0.00001);
	}
}
