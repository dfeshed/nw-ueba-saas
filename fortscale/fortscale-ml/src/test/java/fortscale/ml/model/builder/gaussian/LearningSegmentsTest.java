package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.builder.gaussian.prior.LearningSegments;
import fortscale.ml.model.builder.gaussian.prior.Segmentor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class LearningSegmentsTest {
	private List<ContinuousDataModel> createModels(double... means) {
		List<ContinuousDataModel> models = new ArrayList<>();
		for (Double mean : means) {
			models.add(new ContinuousDataModel().setParameters(0, mean, 0, 0));
		}
		return models;
	}

	@Mock
	private Segmentor segmentorMock;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsModels() {
        new LearningSegments(null, Collections.emptyList(), segmentorMock);
    }

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfGivenNullAsSegmentCentersIterator() {
		new LearningSegments(createModels(), null, segmentorMock);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfGivenNullAsSegmentor() {
		new LearningSegments(createModels(), Collections.emptyList(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfGivenNegativeSegmentCenter() {
		new LearningSegments(createModels(), Collections.singletonList(-1.0), segmentorMock).forEach(doubleDoublePair -> {});
	}

    @Test
	public void shouldCreateNoSegmentIfThereAreNoSegmentCenters() {
		LearningSegments segments = new LearningSegments(createModels(0.0, 1.0, 2.0),
				Collections.emptyList(), segmentorMock);
		Assert.assertFalse(segments.iterator().hasNext());
	}

	@Test
	public void shouldSaveAllSegmentsTheSegmentorCreates() {
		double segmentCenterResultingInSegment = 1;
		List<Double> segmentCenters = Arrays.asList(0.0, segmentCenterResultingInSegment, 2.0);
		double[] means = {0.0, 1.0, 2.0};
		Pair<Double, Double> segment = new ImmutablePair<>(0.0, 2.0);
		Mockito.when(segmentorMock.createSegment(means, segmentCenterResultingInSegment)).thenReturn(segment);
		LearningSegments segments = new LearningSegments(createModels(means), segmentCenters, segmentorMock);

		Iterator<Pair<Double, Pair<Double, Double>>> it = segments.iterator();
		Assert.assertEquals(new ImmutablePair<>(segmentCenterResultingInSegment, segment), it.next());
		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void shouldPassSortedMeansToSegmentor() {
		double[] means = {1, 3, 2, 0};
		double segmentCenter = 1;
		new LearningSegments(createModels(means), Collections.singletonList(segmentCenter), segmentorMock)
				.forEach(doubleDoublePair -> {});

		Mockito.verify(segmentorMock).createSegment(new double[] {0, 1, 2, 3}, segmentCenter);
	}
}
