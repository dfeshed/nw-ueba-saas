package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.builder.gaussian.prior.LearningSegments;
import fortscale.ml.model.builder.gaussian.prior.SegmentCenters;
import fortscale.ml.model.builder.gaussian.prior.Segmentor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@RunWith(MockitoJUnitRunner.class)
public class LearningSegmentsTest {
	private List<ContinuousDataModel> createModels(double... means) {
		return DoubleStream.of(means)
				.mapToObj(mean -> new ContinuousDataModel().setParameters(0, mean, 0, 0))
				.collect(Collectors.toList());
	}

	@Mock
	private Segmentor segmentorMock;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	private SegmentCenters createSegmentCenters(double ... centers) {
		return (models) -> Arrays.stream(centers).iterator();
	}

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsModels() {
        new LearningSegments(null, createSegmentCenters(), segmentorMock);
    }

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfGivenNullAsSegmentCentersIterator() {
		new LearningSegments(createModels(), null, segmentorMock);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfGivenNullAsSegmentor() {
		new LearningSegments(createModels(), createSegmentCenters(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfGivenNegativeSegmentCenter() {
		new LearningSegments(createModels(), createSegmentCenters(-1.0), segmentorMock).forEach(doubleDoublePair -> {});
	}

    @Test
	public void shouldCreateNoSegmentIfThereAreNoSegmentCenters() {
		LearningSegments segments = new LearningSegments(createModels(0.0, 1.0, 2.0),
				createSegmentCenters(), segmentorMock);
		Assert.assertFalse(segments.iterator().hasNext());
	}

	@Test
	public void shouldSaveAllSegmentsTheSegmentorCreates() {
		double segmentCenterResultingInSegment = 1;
		double[] segmentCenters = {0.0, segmentCenterResultingInSegment, 2.0};
		List<ContinuousDataModel> models = createModels(0.0, 1.0, 2.0);
		Segmentor.Segment segment = new Segmentor.Segment(0, 2, models.subList(0, 2));
		Mockito.when(segmentorMock.createSegment(models, segmentCenterResultingInSegment)).thenReturn(segment);
		LearningSegments segments = new LearningSegments(models, createSegmentCenters(segmentCenters), segmentorMock);

		Iterator<LearningSegments.Segment> it = segments.iterator();
		LearningSegments.Segment expectedSegment = new LearningSegments.Segment(segmentCenterResultingInSegment, segment);
		Assert.assertEquals(expectedSegment, it.next());
		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void shouldPassSortedMeansToSegmentor() {
		List<ContinuousDataModel> models = createModels(1, 3, 2, 0);
		double segmentCenter = 1;
		new LearningSegments(models, createSegmentCenters(segmentCenter), segmentorMock)
				.forEach(segment -> {});

		List<ContinuousDataModel> sortedModels = models.stream()
				.sorted(Comparator.comparing(ContinuousDataModel::getMean))
				.collect(Collectors.toList());

		Mockito.verify(segmentorMock).createSegment(sortedModels, segmentCenter);
	}
}
