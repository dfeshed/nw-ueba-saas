package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.TimeModel;
import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.ml.model.metrics.TimeModelBuilderMetricsContainer;
import fortscale.ml.model.metrics.TimeModelBuilderPartitionsMetricsContainer;
import org.springframework.util.Assert;

public class TimeModelBuilder implements IModelBuilder {
    private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s.", GenericHistogram.class.getSimpleName());

    private final int timeResolution;
    private final int bucketSize;
    private final int categoryRarityModelNumOfBuckets;
    private final CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer;
    private TimeModelBuilderMetricsContainer timeModelBuilderMetricsContainer;
    private TimeModelBuilderPartitionsMetricsContainer timeModelBuilderPartitionsMetricsContainer;

    public TimeModelBuilder(TimeModelBuilderConf config, TimeModelBuilderMetricsContainer timeModelBuilderMetricsContainer,
                            TimeModelBuilderPartitionsMetricsContainer partitionsMetricsContainer, CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer) {
        timeResolution = config.getTimeResolution();
        bucketSize = config.getBucketSize();
        categoryRarityModelNumOfBuckets = config.getCategoryRarityModelNumOfBuckets();
        this.timeModelBuilderMetricsContainer = timeModelBuilderMetricsContainer;
        this.timeModelBuilderPartitionsMetricsContainer = partitionsMetricsContainer;
        this.categoryRarityModelBuilderMetricsContainer = categoryRarityModelBuilderMetricsContainer;
    }

    @Override
    public Model build(Object modelBuilderData) {
        TimeModel timeModel = new TimeModel();
        GenericHistogram genericHistogram = castModelBuilderData(modelBuilderData);
        timeModel.init(
                timeResolution, bucketSize, categoryRarityModelNumOfBuckets,
                genericHistogram.getHistogramMap(), genericHistogram.getNumberOfPartitions(), timeModelBuilderMetricsContainer, timeModelBuilderPartitionsMetricsContainer,categoryRarityModelBuilderMetricsContainer);
        return timeModel;
    }

    private GenericHistogram castModelBuilderData(Object modelBuilderData) {
        Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
        Assert.isInstanceOf(GenericHistogram.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (GenericHistogram)modelBuilderData;
    }
}
