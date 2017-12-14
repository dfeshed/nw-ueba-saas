package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.TimeModel;
import fortscale.ml.model.metrics.TimeModelBuilderMetricsContainer;
import org.springframework.util.Assert;

import java.util.Map;

public class TimeModelBuilder implements IModelBuilder {
    private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", GenericHistogram.class.getSimpleName());

    private final int timeResolution;
    private final int bucketSize;
    private final int maxRareTimestampCount;
    private TimeModelBuilderMetricsContainer timeModelBuilderMetricsContainer;

    public TimeModelBuilder(TimeModelBuilderConf config, TimeModelBuilderMetricsContainer timeModelBuilderMetricsContainer) {
        timeResolution = config.getTimeResolution();
        bucketSize = config.getBucketSize();
        maxRareTimestampCount = config.getMaxRareTimestampCount();
        this.timeModelBuilderMetricsContainer = timeModelBuilderMetricsContainer;
    }

    @Override
    public Model build(Object modelBuilderData) {
        TimeModel timeModel = new TimeModel();
        GenericHistogram genericHistogram = castModelBuilderData(modelBuilderData);
        timeModel.init(
                timeResolution, bucketSize, maxRareTimestampCount,
                genericHistogram.getHistogramMap(), genericHistogram.getNumberOfPartitions(), timeModelBuilderMetricsContainer);
        return timeModel;
    }

    private GenericHistogram castModelBuilderData(Object modelBuilderData) {
        Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (GenericHistogram)modelBuilderData;
    }
}
