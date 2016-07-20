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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class LearningSegmentsTest {
	private List<ContinuousDataModel> createModels(double... means) {
		List<ContinuousDataModel> models = new ArrayList<>();
		for (Double mean : means) {
			ContinuousDataModel model = new ContinuousDataModel();
			model.setParameters(0, mean, 0, 0);
			models.add(model);
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

    @Test
	public void shouldCreateNoSegmentIfThereAreNoSegmentCenters() {
		LearningSegments segments = new LearningSegments(createModels(0.0, 1.0, 2.0),
				Collections.emptyList(), segmentorMock);
		Assert.assertEquals(0, segments.size());
	}

	@Test
	public void shouldPassAllSegmentCentersToSegmentor() {
		List<Double> segmentCenters = Arrays.asList(0.0, 1.0, 2.0);
		double[] means = {0.0, 1.0, 2.0};
		new LearningSegments(createModels(means), segmentCenters, segmentorMock);

		segmentCenters.forEach(segmentCenter -> Mockito.verify(segmentorMock).createSegment(means, segmentCenter));
		Mockito.verifyNoMoreInteractions(segmentorMock);
	}

	@Test
	public void shouldSaveAllSegmentsTheSegmentorCreates() {
		List<Double> segmentCenters = Arrays.asList(0.0, 1.0, 2.0);
		double[] means = {0.0, 1.0, 2.0};
		Pair<Double, Double> segment = new ImmutablePair<>(0.0, 2.0);
		Mockito.when(segmentorMock.createSegment(means, segmentCenters.get(1))).thenReturn(segment);
		LearningSegments segments = new LearningSegments(createModels(means), segmentCenters, segmentorMock);

		Assert.assertEquals(1, segments.size());
		Assert.assertEquals(segment, segments.get(0));
	}

	@Test
	public void shouldPassSortedMeansToSegmentor() {
		double[] means = {1, 3, 2, 0};
		double segmentCenter = 1;
		new LearningSegments(createModels(means), Collections.singletonList(segmentCenter), segmentorMock);

		Mockito.verify(segmentorMock).createSegment(new double[] {0, 1, 2, 3}, segmentCenter);
	}
}
