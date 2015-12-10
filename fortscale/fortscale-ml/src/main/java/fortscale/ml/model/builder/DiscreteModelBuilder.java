package fortscale.ml.model.builder;

import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.DiscreteDataModel;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

public class DiscreteModelBuilder implements IModelBuilder {
    public static final String MODEL_BUILDER_TYPE = "discrete";

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
        Assert.notNull(modelBuilderData, "Model builder data cannot be null.");

        String errorMsg = String.format("Model builder data must be of type %s.",
                GenericHistogram.class.getSimpleName());
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, errorMsg);

        return (GenericHistogram)modelBuilderData;
    }

    @SuppressWarnings("unchecked")
    private Pair<String, Double> castValue(Object value) {
        Assert.notNull(value, "Value cannot be null.");

        String errorMsg = String.format("Value must be of type %s.",
                Pair.class.getSimpleName());
        Assert.isInstanceOf(Pair.class, value, errorMsg);

        return (Pair<String, Double>)value;
    }
}
