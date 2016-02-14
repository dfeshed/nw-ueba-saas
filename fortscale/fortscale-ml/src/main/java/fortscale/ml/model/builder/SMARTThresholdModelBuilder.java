package fortscale.ml.model.builder;

import fortscale.ml.model.SMARTThresholdModel;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

public class SMARTThresholdModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", Map.class.getSimpleName());

    @Override
    public SMARTThresholdModel build(Object modelBuilderData) {
        Map<Long, List<Double>> dateToHighestScores = castModelBuilderData(modelBuilderData);
        SMARTThresholdModel model = new SMARTThresholdModel();
        model.init(calcThreshold(dateToHighestScores), calcMaxSeenScore(dateToHighestScores));
        return model;
    }

    private double calcThreshold(Map<Long, List<Double>> dateToHighestScores) {
        return dateToHighestScores.values().stream()
                .mapToDouble(scores -> scores.get(0))
                .min()
                .getAsDouble();
    }

    private double calcMaxSeenScore(Map<Long, List<Double>> dateToHighestScores) {
        return dateToHighestScores.values().stream()
                .mapToDouble(scores -> scores.get(scores.size() - 1))
                .max()
                .getAsDouble();
    }

    protected Map<Long, List<Double>> castModelBuilderData(Object modelBuilderData) {
        Assert.isInstanceOf(Map.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (Map<Long, List<Double>>) modelBuilderData;
    }
}
