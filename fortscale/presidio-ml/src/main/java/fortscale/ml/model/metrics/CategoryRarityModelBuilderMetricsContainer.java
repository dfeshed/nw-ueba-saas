package fortscale.ml.model.metrics;

import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import org.apache.commons.lang3.builder.ToStringBuilder;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Metrics on category rarity model builder
 */
public class CategoryRarityModelBuilderMetricsContainer extends ModelMetricsContainer {
    public static final String METRIC_NAME = "category_rarity_model_builder";

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public CategoryRarityModelBuilderMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        super(metricCollectingService, metricsExporter);
    }


    /**
     * Updates modeling metrics by provided data
     * @param sizeOfFeatureValues
     * @param numOfPartitions
     * @param buckets
     */
    public void updateMetric(int sizeOfFeatureValues, long numOfPartitions, double[] buckets) {
        Metric metric = getMetric();
        metric.getValue().compute(MetricEnums.MetricValues.MAX_SIZE_OF_FEATURE_VALUES, (k, v) -> Math.max(v.doubleValue(), sizeOfFeatureValues));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_SIZE_OF_FEATURE_VALUES, (k, v) -> v.doubleValue() + sizeOfFeatureValues);
        metric.getValue().compute(MetricEnums.MetricValues.MAX_NUM_OF_PARTITIONS, (k, v) -> Math.max(v.doubleValue(), numOfPartitions));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_NUM_OF_PARTITIONS, (k, v) -> v.longValue() + numOfPartitions);
        metric.getValue().compute(MetricEnums.MetricValues.AMOUNT_OF_WRITE_DATA, (k, v) -> v.longValue() + 1);

        if (numOfContexts != 0) {
            metric.getValue().compute(MetricEnums.MetricValues.AVG_SIZE_OF_FEATURE_VALUES, (k, v) -> metric.getValue().get(MetricEnums.MetricValues.SUM_SIZE_OF_FEATURE_VALUES).intValue() / numOfContexts);
            metric.getValue().compute(MetricEnums.MetricValues.AVG_NUM_OF_PARTITIONS, (k, v) -> metric.getValue().get(MetricEnums.MetricValues.SUM_NUM_OF_PARTITIONS).intValue() / numOfContexts);
        }

        if (Arrays.stream(buckets).limit(5).sum() > 0.0) {
            metric.getValue().compute(MetricEnums.MetricValues.AMOUNT_OF_CONTEXTS_WITH_POSITIVE_BUCKET_VALUES, (k, v) -> v.longValue() + 1);
        }
    }

    /**
     * @param tags - tags of the metrics
     * @return new Metric object for logical time
     */
    protected Metric createNewMetric(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(MetricEnums.MetricValues.MAX_SIZE_OF_FEATURE_VALUES, 0L);
        values.put(MetricEnums.MetricValues.AVG_SIZE_OF_FEATURE_VALUES, 0L);
        values.put(MetricEnums.MetricValues.SUM_SIZE_OF_FEATURE_VALUES, 0L);
        values.put(MetricEnums.MetricValues.AMOUNT_OF_CONTEXTS_WITH_POSITIVE_BUCKET_VALUES, 0L);
        values.put(MetricEnums.MetricValues.MAX_NUM_OF_PARTITIONS, 0L);
        values.put(MetricEnums.MetricValues.AVG_NUM_OF_PARTITIONS, 0L);
        values.put(MetricEnums.MetricValues.SUM_NUM_OF_PARTITIONS, 0L);
        values.put(MetricEnums.MetricValues.AMOUNT_OF_WRITE_DATA, 0L);
        return new Metric.MetricBuilder()
                .setMetricName(METRIC_NAME)
                .setMetricReportOnce(true)
                .setMetricUnit(MetricEnums.MetricUnitType.NUMBER)
                .setMetricTags(tags)
                .setMetricLogicTime(logicalTime)
                .setMetricMultipleValues(values)
                .build();
    }

    @Override
    public String getFactoryName() {
        return CategoryRarityModelBuilderConf.CATEGORY_RARITY_MODEL_BUILDER;
    }
}
