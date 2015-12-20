package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.TimeModel;
import org.springframework.util.Assert;

public class TimeModelBuilder implements IModelBuilder {
    public static final String MODEL_BUILDER_TYPE = "time";
    private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", GenericHistogram.class.getSimpleName());

    private final int timeResolution;
    private final int bucketSize;

    @JsonCreator
    public TimeModelBuilder(
            @JsonProperty("timeResolution") Integer timeResolution,
            @JsonProperty("bucketSize") Integer bucketSize) {

        Assert.notNull(timeResolution);
        Assert.notNull(bucketSize);
        Assert.isTrue(timeResolution > 0);
        Assert.isTrue(bucketSize > 0);

        this.timeResolution = timeResolution;
        this.bucketSize = bucketSize;
    }

    @Override
    public Model build(Object modelBuilderData) {
        return new TimeModel(timeResolution, bucketSize,
                castModelBuilderData(modelBuilderData)
                .getHistogramMap());
    }

    @Override
    public double calculateScore(Object value, Model model) {
        return model.calculateScore(value);
    }

    private GenericHistogram castModelBuilderData(Object modelBuilderData) {
        Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (GenericHistogram)modelBuilderData;
    }
}
