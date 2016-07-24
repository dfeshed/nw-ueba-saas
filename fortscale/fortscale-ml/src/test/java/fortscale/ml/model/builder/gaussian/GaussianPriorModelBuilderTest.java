package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilder;
import fortscale.ml.model.builder.gaussian.prior.PriorBuilder;
import fortscale.ml.model.builder.gaussian.prior.SegmentCenters;
import fortscale.ml.model.builder.gaussian.prior.Segmentor;
import org.apache.commons.collections.IteratorUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GaussianPriorModelBuilderTest {
	@Mock
	private Segmentor segmentor;
	@Mock
	private SegmentCenters segmentCenters;
	@Mock
	private PriorBuilder priorBuilder;
	private int minNumOfSamplesToLearnFrom = 0;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsSegmentCenters() {
		new GaussianPriorModelBuilder(null, segmentor, priorBuilder, minNumOfSamplesToLearnFrom);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsSegmentor() {
		new GaussianPriorModelBuilder(segmentCenters, null, priorBuilder, minNumOfSamplesToLearnFrom);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsGaussianPrior() {
		new GaussianPriorModelBuilder(segmentCenters, segmentor, null, minNumOfSamplesToLearnFrom);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsMinNumOfSamplesToLearnFrom() {
		new GaussianPriorModelBuilder(segmentCenters, segmentor, priorBuilder, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenDataOfTheWrongType() {
		new GaussianPriorModelBuilder(segmentCenters, segmentor, priorBuilder, minNumOfSamplesToLearnFrom)
				.build("bad data");
	}

	@Test
	public void shouldBuildNoPriorIfGivenNoData() {
		when(segmentCenters.iterate(Collections.emptyList())).thenReturn(IteratorUtils.emptyIterator());
		GaussianPriorModel priorModel = (GaussianPriorModel) new GaussianPriorModelBuilder(segmentCenters, segmentor, priorBuilder, minNumOfSamplesToLearnFrom)
				.build(Collections.emptyList());

		Assert.assertEquals(0, priorModel.getSegmentPriors().length);
	}

	@Test
	public void shouldCreateSegmentPriorsAsStatedByThePriorBuilderWithSupportAsStatedByTheSegmentsSize() {
		int minNumOfSamplesToLearnFrom = 0;
		double meanWithSuccessfulPrior = 5;
		double meanWithoutSuccessfulPrior = 10;
		double prior = 3;
		ContinuousDataModel modelWithSuccessfulPrior = new ContinuousDataModel().setParameters(minNumOfSamplesToLearnFrom, meanWithSuccessfulPrior, 0, 5);
		ContinuousDataModel modelWithoutSuccessfulPrior = new ContinuousDataModel().setParameters(minNumOfSamplesToLearnFrom, meanWithoutSuccessfulPrior, 0, 5);
		double segmentLeftMean = 0;
		double segmentRightMean = 100;
		GaussianPriorModel gaussianPriorModel = (GaussianPriorModel) new GaussianPriorModelBuilder(
				models -> IteratorUtils.arrayIterator(new double[]{meanWithSuccessfulPrior, meanWithoutSuccessfulPrior}),
				(sortedMeans, segmentCenter) -> new Segmentor.Segment(segmentLeftMean, segmentRightMean, 0, sortedMeans.length - 1),
				(models, mean) -> {
					if (mean == meanWithSuccessfulPrior) {
						return prior;
					}
					return null;
				},
				minNumOfSamplesToLearnFrom
		).build(Arrays.asList(modelWithSuccessfulPrior, modelWithoutSuccessfulPrior));

		GaussianPriorModel.SegmentPrior[] segmentPriors = gaussianPriorModel.getSegmentPriors();
		Assert.assertEquals(1, segmentPriors.length);
		Assert.assertEquals(meanWithSuccessfulPrior, segmentPriors[0].mean, 0.00001);
		Assert.assertEquals(prior, segmentPriors[0].priorAtMean, 0.00001);
		Assert.assertEquals(meanWithSuccessfulPrior - segmentLeftMean, segmentPriors[0].supportFromLeftOfMean, 0.00001);
		Assert.assertEquals(segmentRightMean - meanWithSuccessfulPrior, segmentPriors[0].supportFromRightOfMean, 0.00001);
	}

	@Test
	public void shouldPassOnlyTheModelsWithEnoughSamplesToThePriorBuilder() {
		int minNumOfSamplesToLearnFrom = 10;
		double mean = 5;
		ContinuousDataModel modelWithEnoughSamples = new ContinuousDataModel().setParameters(minNumOfSamplesToLearnFrom, mean, 0, 5);
		ContinuousDataModel modelWithouEnoughSamples = new ContinuousDataModel().setParameters(0, mean, 0, 6);
		new GaussianPriorModelBuilder(
				models -> IteratorUtils.singletonIterator(mean),
				(sortedMeans, segmentCenter) -> new Segmentor.Segment(sortedMeans[0], sortedMeans[sortedMeans.length - 1], 0, sortedMeans.length - 1),
				priorBuilder,
				minNumOfSamplesToLearnFrom
		).build(Arrays.asList(modelWithEnoughSamples, modelWithouEnoughSamples));

		Mockito.verify(priorBuilder).calcPrior(Collections.singletonList(modelWithEnoughSamples), mean);
	}

	@Test
	public void shouldPassOnlyTheModelsInsideTheLearningSegmentToThePriorBuilder() {
		int minNumOfSamplesToLearnFrom = 0;
		double mean = 5;
		ContinuousDataModel modelInsideTheLearningSegment = new ContinuousDataModel().setParameters(minNumOfSamplesToLearnFrom, mean, 0, 5);
		ContinuousDataModel modelOutsideTheLearningSegment = new ContinuousDataModel().setParameters(minNumOfSamplesToLearnFrom, mean + 10, 0, 6);
		new GaussianPriorModelBuilder(
				models -> IteratorUtils.singletonIterator(mean),
				(sortedMeans, segmentCenter) -> new Segmentor.Segment(sortedMeans[0], sortedMeans[0], 0, 0),
				priorBuilder,
				minNumOfSamplesToLearnFrom
		).build(Arrays.asList(modelInsideTheLearningSegment, modelOutsideTheLearningSegment));

		Mockito.verify(priorBuilder).calcPrior(Collections.singletonList(modelInsideTheLearningSegment), mean);
	}
}
