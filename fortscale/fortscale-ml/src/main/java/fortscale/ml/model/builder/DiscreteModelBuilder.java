package fortscale.ml.model.builder;

import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.DiscreteDataModel;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

public class DiscreteModelBuilder implements IModelBuilder {
    public static final String MODEL_BUILDER_TYPE = "discrete";
    private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", GenericHistogram.class.getSimpleName());
    private static final String NULL_VALUE_ERROR_MSG = "Value cannot be null.";
    private static final String VALUE_TYPE_ERROR_MSG = String.format(
            "Value must be of type %s.", Pair.class.getSimpleName());

    @Override
    public Model build(Object modelBuilderData) {
        return new DiscreteDataModel(
                castModelBuilderData(modelBuilderData)
                .getHistogramMap()
                .values());
    }

    @Override
    public double calculateScore(Object value, Model model) {
        return model.calculateScore(
                castValue(value)
                .getValue());
    }

    private GenericHistogram castModelBuilderData(Object modelBuilderData) {
        Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (GenericHistogram)modelBuilderData;
    }

    @SuppressWarnings("unchecked")
    private Pair<String, Double> castValue(Object value) {
        Assert.notNull(value, NULL_VALUE_ERROR_MSG);
        Assert.isInstanceOf(Pair.class, value, VALUE_TYPE_ERROR_MSG);
        return (Pair<String, Double>)value;
    }
}
