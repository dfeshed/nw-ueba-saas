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
	public void shouldBuildPriorFromOneModel() {
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConf(1, 1, 1, 1, 0, 0);
		ContinuousDataModel model = new ContinuousDataModel().setParameters(0, 5, 0, 6);
		GaussianPriorModel priorModel = (GaussianPriorModel) new GaussianPriorModelBuilder(conf)
				.build(Collections.singletonList(model));

		GaussianPriorModel.SegmentPrior expectedSegmentPrior =
				new GaussianPriorModel.SegmentPrior(model.getMean(), model.getMaxValue(), 0, 0);
		Assert.assertArrayEquals(new GaussianPriorModel.SegmentPrior[]{expectedSegmentPrior}, priorModel.getSegmentPriors());
	}

	@Test
	public void shouldBuildPriorFromTwoModels() {
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConf(1, 1, 1, 1, 0, 0);
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
		GaussianPriorModelBuilderConf conf = new GaussianPriorModelBuilderConf(1, 1, 1, 1, 0, minNumOfSamplesToLearnFrom);
		double mean = 5;
		ContinuousDataModel model1 = new ContinuousDataModel().setParameters(minNumOfSamplesToLearnFrom, mean, 0, 5);
		ContinuousDataModel model2 = new ContinuousDataModel().setParameters(0, mean, 0, 6);
		GaussianPriorModel priorModel = (GaussianPriorModel) new GaussianPriorModelBuilder(conf)
				.build(Arrays.asList(model1, model2));

		GaussianPriorModel.SegmentPrior expectedSegmentPrior =
				new GaussianPriorModel.SegmentPrior(mean, model1.getMaxValue(), 0, 0);
		Assert.assertArrayEquals(new GaussianPriorModel.SegmentPrior[]{expectedSegmentPrior}, priorModel.getSegmentPriors());
	}
}
