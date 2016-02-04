package fortscale.ml.model.builder;

import fortscale.ml.model.Model;
import fortscale.ml.model.SMARTThresholdModel;
import org.springframework.util.Assert;

import java.util.Map;

public class SMARTThresholdModelBuilder implements IModelBuilder {
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", Map.class.getSimpleName());

    @Override
    public Model build(Object modelBuilderData) {
        Map<SMARTThresholdModel, Double> modelToNthHighestScore = castModelBuilderData(modelBuilderData);
        SMARTThresholdModel model = new SMARTThresholdModel();
        int maxSeenScore = 100; //TODO: use the real max seen score
        model.init(calcThreshold(modelToNthHighestScore), maxSeenScore);
        return model;
    }

    private double calcThreshold(Map<SMARTThresholdModel, Double> modelToNthHighestScore) {
        return modelToNthHighestScore.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().restoreOriginalScore(entry.getValue()))
                .min()
                .getAsDouble();
    }

    protected Map<SMARTThresholdModel, Double> castModelBuilderData(Object modelBuilderData) {
        Assert.isInstanceOf(Map.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (Map<SMARTThresholdModel, Double>) modelBuilderData;
    }
}
