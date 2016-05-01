package fortscale.ml.model.builder;

import fortscale.ml.model.SMARTThresholdModel;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SMARTThresholdModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", Map.class.getSimpleName());
    static final double EPSILON = 0.00000001;

    private double minThreshold;
    private double minMaximalScore;

    public SMARTThresholdModelBuilder(SMARTThresholdModelBuilderConf config) {
        minThreshold = config.getMinThreshold();
        minMaximalScore = config.getMinMaximalScore();
    }

    @Override
    public SMARTThresholdModel build(Object modelBuilderData) {
        Map<Long, List<Double>> dateToHighestScores = castModelBuilderData(modelBuilderData);
        SMARTThresholdModel model = new SMARTThresholdModel();
        double threshold = Math.max(minThreshold, calcThreshold(filterEmptyDays(dateToHighestScores)) + EPSILON);
        double maximalScore = Math.max(minMaximalScore, calcMaximalScore(filterEmptyDays(dateToHighestScores)));
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
                .min()
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
