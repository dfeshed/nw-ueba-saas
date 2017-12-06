package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.*;
import fortscale.ml.model.builder.IModelBuilder;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Full documentation can be found here: https://fortscale.atlassian.net/wiki/display/FSC/Gaussian+model
 */
public class GaussianPriorModelBuilder implements IModelBuilder {
	private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
	private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
			"Model builder data must be of type %s of %s",
			List.class.getSimpleName(),
			ContinuousDataModel.class.getSimpleName()
	);

	private SegmentCenters segmentCenters;
	private Segmentor segmentor;
	private PriorBuilder priorBuilder;
	private int minNumOfSamplesToLearnFrom;

	public GaussianPriorModelBuilder(SegmentCenters segmentCenters,
									 Segmentor segmentor,
									 PriorBuilder priorBuilder,
									 int minNumOfSamplesToLearnFrom) {
		Assert.notNull(segmentCenters);
		Assert.notNull(priorBuilder);
		Assert.notNull(segmentor);
		Assert.isTrue(minNumOfSamplesToLearnFrom >= 0);
		this.segmentCenters = segmentCenters;
		this.segmentor = segmentor;
		this.priorBuilder = priorBuilder;
		this.minNumOfSamplesToLearnFrom = minNumOfSamplesToLearnFrom;
	}

	@Override
	public Model build(Object modelBuilderData) {
		List<IContinuousDataModel> models = castModelBuilderData(modelBuilderData);
		models = getModelsWithEnoughSamples(models);
		List<GaussianPriorModel.SegmentPrior> segmentPriors = new ArrayList<>();
		LearningSegments segments = new LearningSegments(models, segmentCenters, segmentor);
		for (LearningSegments.Segment segment : segments) {
			Double priorAtMean = priorBuilder.calcPrior(
					segment.getModels(),
					segment.getCenter()
			);
			if (priorAtMean != null) {
				segmentPriors.add(new GaussianPriorModel.SegmentPrior().init(
						segment.getCenter(),
						priorAtMean,
						segment.getCenter() - segment.getLeftMean(),
						segment.getRightMean() - segment.getCenter())
				);
			}
		}
		return new GaussianPriorModel().init(segmentPriors);
	}

	private List<IContinuousDataModel> getModelsWithEnoughSamples(List<IContinuousDataModel> models) {
		return models.stream()
				.filter(model -> getNumOfSamples(model) >= minNumOfSamplesToLearnFrom)
				.collect(Collectors.toList());
	}

	private static long getNumOfSamples(IContinuousDataModel model){
		return model instanceof ContinuousMaxDataModel ? ((ContinuousMaxDataModel) model).getNumOfPartitions() : model.getNumOfSamples();
	}

	private List<IContinuousDataModel> castModelBuilderData(Object modelBuilderData) {
		Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
		Assert.isInstanceOf(List.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
		return (List<IContinuousDataModel>) modelBuilderData;
	}
}
