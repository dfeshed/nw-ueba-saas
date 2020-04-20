package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import fortscale.ml.model.IContinuousDataModel;
import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilder;
import fortscale.ml.model.builder.gaussian.prior.PriorBuilder;
import fortscale.ml.model.builder.gaussian.prior.SegmentCenters;
import fortscale.ml.model.builder.gaussian.prior.Segmentor;
import fortscale.ml.model.metrics.GaussianPriorModelBuilderMetricsContainer;
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
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GaussianPriorModelBuilderTest {
	private GaussianPriorModelBuilderMetricsContainer gaussianPriorModelBuilderMetricsContainer = mock(GaussianPriorModelBuilderMetricsContainer.class);
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
		new GaussianPriorModelBuilder(null, segmentor, priorBuilder, minNumOfSamplesToLearnFrom, gaussianPriorModelBuilderMetricsContainer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsSegmentor() {
		new GaussianPriorModelBuilder(segmentCenters, null, priorBuilder, minNumOfSamplesToLearnFrom, gaussianPriorModelBuilderMetricsContainer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsGaussianPrior() {
		new GaussianPriorModelBuilder(segmentCenters, segmentor, null, minNumOfSamplesToLearnFrom, gaussianPriorModelBuilderMetricsContainer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsMinNumOfSamplesToLearnFrom() {
		new GaussianPriorModelBuilder(segmentCenters, segmentor, priorBuilder, -1, gaussianPriorModelBuilderMetricsContainer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenDataOfTheWrongType() {
		new GaussianPriorModelBuilder(segmentCenters, segmentor, priorBuilder, minNumOfSamplesToLearnFrom, gaussianPriorModelBuilderMetricsContainer)
				.build("bad data");
	}

	@Test
	public void shouldBuildNoPriorIfGivenNoData() {
		when(segmentCenters.iterate(Collections.emptyList())).thenReturn(IteratorUtils.emptyIterator());
		GaussianPriorModel priorModel = (GaussianPriorModel) new GaussianPriorModelBuilder(segmentCenters, segmentor, priorBuilder, minNumOfSamplesToLearnFrom, gaussianPriorModelBuilderMetricsContainer)
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
		PriorBuilder priorBuilder = new PriorBuilderTest(meanWithSuccessfulPrior, prior);
		GaussianPriorModel gaussianPriorModel = (GaussianPriorModel) new GaussianPriorModelBuilder(
				models -> IteratorUtils.arrayIterator(new double[]{meanWithSuccessfulPrior, meanWithoutSuccessfulPrior}),
				(sortedModels, segmentCenter) -> new Segmentor.Segment(segmentLeftMean, segmentRightMean, sortedModels),
				priorBuilder,
				minNumOfSamplesToLearnFrom,
				gaussianPriorModelBuilderMetricsContainer
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
				(sortedModels, segmentCenter) -> new Segmentor.Segment(
						sortedModels.get(0).getMean(),
						sortedModels.get(sortedModels.size() - 1).getMean(),
						sortedModels
				),
				priorBuilder,
				minNumOfSamplesToLearnFrom,
				gaussianPriorModelBuilderMetricsContainer
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
				(sortedModels, segmentCenter) -> new Segmentor.Segment(sortedModels.get(0).getMean(), sortedModels.get(0).getMean(), sortedModels.subList(0, 1)),
				priorBuilder,
				minNumOfSamplesToLearnFrom,
				gaussianPriorModelBuilderMetricsContainer
		).build(Arrays.asList(modelInsideTheLearningSegment, modelOutsideTheLearningSegment));

		Mockito.verify(priorBuilder).calcPrior(Collections.singletonList(modelInsideTheLearningSegment), mean);
	}

	public static class PriorBuilderTest implements PriorBuilder{
		private double meanWithSuccessfulPrior;
		private double prior;

		public PriorBuilderTest(double meanWithSuccessfulPrior, double prior){
			this.meanWithSuccessfulPrior = meanWithSuccessfulPrior;
			this.prior = prior;
		}

		@Override
		public Double calcPrior(List<IContinuousDataModel> models, double mean) {
			if (mean == meanWithSuccessfulPrior) {
				return prior;
			}
			return null;
		}

		@Override
		public Double getMinAllowedPrior() {
			return null;
		}
	}
}
