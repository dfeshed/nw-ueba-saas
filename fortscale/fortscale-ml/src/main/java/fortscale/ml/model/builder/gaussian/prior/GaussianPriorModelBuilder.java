package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.builder.IModelBuilder;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GaussianPriorModelBuilder implements IModelBuilder {
	private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
	private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
			"Model builder data must be of type %s of %s",
			List.class.getSimpleName(),
			ContinuousDataModel.class.getSimpleName()
	);

	private static Comparator<ContinuousDataModel> MODELS_COMPARATOR = (model1, model2) -> {
		if (model1.getMean() < model2.getMean()) {
			return -1;
		} else if (model1.getMean() > model2.getMean()) {
			return 1;
		} else {
			return 0;
		}
	};

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
		List<ContinuousDataModel> models = castModelBuilderData(modelBuilderData);
		models = getModelsWithEnoughSamples(models);
		models.sort(MODELS_COMPARATOR);
		List<GaussianPriorModel.SegmentPrior> segmentPriors = new ArrayList<>();
		for (LearningSegments.Segment segment : new LearningSegments(models, segmentCenters, segmentor)) {
			Double priorAtMean = priorBuilder.calcPrior(
					segment.getModels(),
					segment.getCenter()
			);
			if (priorAtMean != null) {
				segmentPriors.add(new GaussianPriorModel.SegmentPrior(
						segment.getCenter(),
						priorAtMean,
						segment.getCenter() - segment.getLeftMean(),
						segment.getRightMean() - segment.getCenter())
				);
			}
		}
		return new GaussianPriorModel().init(segmentPriors);
	}

	private List<ContinuousDataModel> getModelsWithEnoughSamples(List<ContinuousDataModel> models) {
		return models.stream()
				.filter(model -> model.getNumOfSamples() >= minNumOfSamplesToLearnFrom)
				.collect(Collectors.toList());
	}

	private List<ContinuousDataModel> castModelBuilderData(Object modelBuilderData) {
		Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
		Assert.isInstanceOf(List.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
		return (List<ContinuousDataModel>) modelBuilderData;
	}
}
