package fortscale.ml.model.builder;

import fortscale.ml.model.SMARTScoreMappingModel;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SMARTScoreMappingModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", Map.class.getSimpleName());
    static final double EPSILON = Double.MIN_VALUE;

    private double minThreshold;
    private double minMaximalScore;

    public SMARTScoreMappingModelBuilder(SMARTScoreMappingModelBuilderConf config) {
        minThreshold = config.getMinThreshold();
        minMaximalScore = config.getMinMaximalScore();
    }

    @Override
    public SMARTScoreMappingModel build(Object modelBuilderData) {
        Map<Long, List<Double>> dateToHighestScores = castModelBuilderData(modelBuilderData);
        SMARTScoreMappingModel model = new SMARTScoreMappingModel();
		double threshold;
		double maximalScore;
		if (filterEmptyDays(dateToHighestScores).findAny().isPresent()) {
			threshold = Math.max(minThreshold, calcThreshold(filterEmptyDays(dateToHighestScores)) + EPSILON);
			maximalScore = Math.max(minMaximalScore, calcMaximalScore(filterEmptyDays(dateToHighestScores)));
		} else {
			//TODO: get these values from the user (through the ASL)
			threshold = 100;
			maximalScore = 100;
		}
		if (threshold > maximalScore) {
			maximalScore = threshold;
		}
		model.init(threshold, maximalScore);
        return model;
    }

    private Stream<List<Double>> filterEmptyDays(Map<Long, List<Double>> dateToHighestScores) {
        return dateToHighestScores.values().stream()
                .filter(scores -> scores.size() > 0);
    }

    private double calcThreshold(Stream<List<Double>> dateToHighestScores) {
        return dateToHighestScores
                .mapToDouble(scores -> scores.get(0))
                .average()
                .orElse(50 - EPSILON);
    }

    private double calcMaximalScore(Stream<List<Double>> dateToHighestScores) {
        return dateToHighestScores
                .mapToDouble(scores -> scores.get(scores.size() - 1))
                .max()
                .orElse(100);
    }

    protected Map<Long, List<Double>> castModelBuilderData(Object modelBuilderData) {
        Assert.isInstanceOf(Map.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (Map<Long, List<Double>>) modelBuilderData;
    }
}
