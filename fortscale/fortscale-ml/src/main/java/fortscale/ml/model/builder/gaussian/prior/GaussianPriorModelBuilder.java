package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.builder.IModelBuilder;
import org.springframework.util.Assert;

import java.util.*;
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

	private GaussianPriorModelBuilderConf conf;

	public GaussianPriorModelBuilder(GaussianPriorModelBuilderConf conf) {
		Assert.notNull(conf);
		this.conf = conf;
	}

	@Override
	public Model build(Object modelBuilderData) {
		List<ContinuousDataModel> models = castModelBuilderData(modelBuilderData);
		models = getModelsWithEnoughSamples(models);
		List<GaussianPriorModel.SegmentPrior> segmentPriors = new ArrayList<>();
		for (LearningSegments.Segment segment : createLearningSegments(models)) {
			OptionalDouble maxValueInsideSegment = getModelsInsideSegment(models, segment).stream()
					.mapToDouble(ContinuousDataModel::getMaxValue)
					.max();
			if (maxValueInsideSegment.isPresent()) {
				segmentPriors.add(new GaussianPriorModel.SegmentPrior(
						segment.center,
						maxValueInsideSegment.getAsDouble(),
						segment.center - segment.left,
						segment.right - segment.center)
				);
			}
		}
		return new GaussianPriorModel().init(segmentPriors);
	}

	private List<ContinuousDataModel> getModelsWithEnoughSamples(List<ContinuousDataModel> models) {
		return models.stream()
				.filter(model -> model.getNumOfSamples() >= conf.getMinNumOfSamplesToLearnFrom())
				.collect(Collectors.toList());
	}

	private List<ContinuousDataModel> getModelsInsideSegment(List<ContinuousDataModel> models, LearningSegments.Segment segment) {
		int leftIndex = Collections.binarySearch(
				models,
				new ContinuousDataModel().setParameters(0, segment.left, 0, 0),
				MODELS_COMPARATOR
		);
		if (leftIndex < 0) {
			leftIndex = -leftIndex - 1;
		}
		while (leftIndex > 0 && models.get(leftIndex - 1).getMean() == models.get(leftIndex).getMean()) {
			leftIndex--;
		}
		int rightIndex = Collections.binarySearch(
				models,
				new ContinuousDataModel().setParameters(0, segment.right, 0, 0),
				MODELS_COMPARATOR
		);
		if (rightIndex < 0) {
			rightIndex = -rightIndex - 1;
			rightIndex = Math.max(0, rightIndex - 1);
		}
		while (rightIndex < models.size() - 1 && models.get(rightIndex + 1).getMean() == models.get(rightIndex).getMean()) {
			rightIndex++;
		}
		return models.subList(leftIndex, rightIndex + 1);
	}

	private List<ContinuousDataModel> castModelBuilderData(Object modelBuilderData) {
		Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
		Assert.isInstanceOf(List.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
		return (List<ContinuousDataModel>) modelBuilderData;
	}

	private LearningSegments createLearningSegments(List<ContinuousDataModel> models) {
		UniformSegmentCenters segmentsCenter = new UniformSegmentCenters(models, conf.getDistanceBetweenSegmentsCenter());
		Segmentor segmentor = new NeighboursSegmentor(
				conf.getNumberOfNeighbours(),
				conf.getMaxRatioBetweenSegmentSizeToCenter(),
				conf.getMaxSegmentWidthToNotDiscardBecauseOfBadRatio(),
				conf.getPadding()
		);
		models.sort(MODELS_COMPARATOR);
		return new LearningSegments(models, segmentsCenter, segmentor);
	}
}
