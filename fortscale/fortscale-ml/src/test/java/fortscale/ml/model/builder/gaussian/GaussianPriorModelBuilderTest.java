package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilder;
import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilderConf;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class GaussianPriorModelBuilderTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsConf() {
        new GaussianPriorModelBuilder(null);
    }

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenDataOfTheWrongType() {
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConf(1, 1, 1, 1, 1, 1);
		new GaussianPriorModelBuilder(conf).build("bad data");
	}

	@Test
	public void shouldBuildNoPriorIdGivenNoData() {
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConf(1, 1, 1, 1, 1, 1);
		GaussianPriorModel priorModel = (GaussianPriorModel) new GaussianPriorModelBuilder(conf)
				.build(Collections.emptyList());

		Assert.assertEquals(0, priorModel.getSegmentPriors().length);
	}

	@Test
	public void shouldBuildOneSegmentPriorFromOneModel() {
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConf(1, 1, 0.0001, 0, 0, 0);
		int mean = 2;
		ContinuousDataModel model = new ContinuousDataModel().setParameters(0, mean, 0, 3);
		GaussianPriorModel priorModel = (GaussianPriorModel) new GaussianPriorModelBuilder(conf)
				.build(Collections.singletonList(model));

		GaussianPriorModel.SegmentPrior expectedSegmentPrior =
				new GaussianPriorModel.SegmentPrior(model.getMean(), model.getMaxValue(), 0, 0);
		Assert.assertArrayEquals(new GaussianPriorModel.SegmentPrior[]{expectedSegmentPrior}, priorModel.getSegmentPriors());
	}

	@Test
	public void shouldBuildMultipleSegmentPriorsFromOneModelIfMaxRatioBetweenSegmentSizeToCenterAllowsIt() {
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConf(1, 1, 0.0001, 1000, 0, 0);
		int mean = 2;
		ContinuousDataModel model = new ContinuousDataModel().setParameters(0, mean, 0, 3);
		GaussianPriorModel priorModel = (GaussianPriorModel) new GaussianPriorModelBuilder(conf)
				.build(Collections.singletonList(model));

		Assert.assertArrayEquals(new GaussianPriorModel.SegmentPrior[]{
				new GaussianPriorModel.SegmentPrior(0, model.getMaxValue(), 2, 2),
				new GaussianPriorModel.SegmentPrior(1, model.getMaxValue(), 1, 1),
				new GaussianPriorModel.SegmentPrior(2, model.getMaxValue(), 0, 0)
		}, priorModel.getSegmentPriors());
	}

	@Test
	public void shouldBuildPriorFromTwoModelsInTheSameLearningSegment() {
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConf(1, 1, 0.0001, 0, 0, 0);
		double mean = 5;
		ContinuousDataModel model1 = new ContinuousDataModel().setParameters(0, mean, 0, 5);
		ContinuousDataModel model2 = new ContinuousDataModel().setParameters(0, mean, 0, 6);
		GaussianPriorModel priorModel = (GaussianPriorModel) new GaussianPriorModelBuilder(conf)
				.build(Arrays.asList(model1, model2));

		GaussianPriorModel.SegmentPrior expectedSegmentPrior =
				new GaussianPriorModel.SegmentPrior(mean, Math.max(model1.getMaxValue(), model2.getMaxValue()), 0, 0);
		Assert.assertArrayEquals(new GaussianPriorModel.SegmentPrior[]{expectedSegmentPrior}, priorModel.getSegmentPriors());
	}

	@Test
	public void shouldDiscardModelsWithoutEnoughSamples() {
		int minNumOfSamplesToLearnFrom = 10;
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConf(1, 1, 0.0001, 0, 0, minNumOfSamplesToLearnFrom);
		double mean = 5;
		ContinuousDataModel model1 = new ContinuousDataModel().setParameters(minNumOfSamplesToLearnFrom, mean, 0, 5);
		ContinuousDataModel model2 = new ContinuousDataModel().setParameters(0, mean, 0, 6);
		GaussianPriorModel priorModel = (GaussianPriorModel) new GaussianPriorModelBuilder(conf)
				.build(Arrays.asList(model1, model2));

		GaussianPriorModel.SegmentPrior expectedSegmentPrior =
				new GaussianPriorModel.SegmentPrior(mean, model1.getMaxValue(), 0, 0);
		Assert.assertArrayEquals(new GaussianPriorModel.SegmentPrior[]{expectedSegmentPrior}, priorModel.getSegmentPriors());
	}

	@Test
	public void shouldDiscardModelIfOutsideTheLearningSegment() {
		int padding = 1;
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConf(10000, 1, 0.00001, 0, padding, 0);
		double mean = 0;
		ContinuousDataModel model1 = new ContinuousDataModel().setParameters(0, mean, 0, 5);
		ContinuousDataModel model2 = new ContinuousDataModel().setParameters(0, mean + 1, 0, 6);
		ContinuousDataModel modelOutsideLearningSegment = new ContinuousDataModel().setParameters(0, mean + 10, 0, 7);
		GaussianPriorModel priorModel = (GaussianPriorModel) new GaussianPriorModelBuilder(conf)
				.build(Arrays.asList(model1, model2, modelOutsideLearningSegment));

		GaussianPriorModel.SegmentPrior expectedSegmentPrior =
				new GaussianPriorModel.SegmentPrior(mean, Math.max(model1.getMaxValue(), model2.getMaxValue()), padding, padding);
		Assert.assertArrayEquals(new GaussianPriorModel.SegmentPrior[]{expectedSegmentPrior}, priorModel.getSegmentPriors());
	}
}
