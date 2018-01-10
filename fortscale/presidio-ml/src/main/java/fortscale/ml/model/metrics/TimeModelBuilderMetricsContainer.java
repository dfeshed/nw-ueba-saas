package fortscale.ml.model.metrics;

import fortscale.ml.model.builder.TimeModelBuilderConf;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Metrics on time model builder
 */
public class TimeModelBuilderMetricsContainer extends ModelMetricsContainer {
    public static final String METRIC_NAME = "time_model_builder";

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public TimeModelBuilderMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        super(metricCollectingService, metricsExporter);
    }


    /**
     * Updates modeling metrics by provided data
     *
     * @param sizeOfFeatureValues
     * @param numOfPartitions
     * @param buckets
     */
    public void updateMetric(long sizeOfFeatureValues, long numOfPartitions, List<Double> buckets,
                             long amountOfBucketHits, long amountOfSmoothedBuckets) {
        Metric metric = getMetric();
        metric.getValue().compute(MetricEnums.MetricValues.MAX_SIZE_OF_FEATURE_VALUES, (k, v) -> Math.max(v.doubleValue(), sizeOfFeatureValues));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_SIZE_OF_FEATURE_VALUES, (k, v) -> v.doubleValue() + sizeOfFeatureValues);
        metric.getValue().compute(MetricEnums.MetricValues.MAX_NUM_OF_PARTITIONS, (k, v) -> Math.max(v.doubleValue(), numOfPartitions));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_NUM_OF_PARTITIONS, (k, v) -> v.longValue() + numOfPartitions);
        metric.getValue().compute(MetricEnums.MetricValues.WRITES, (k, v) -> v.longValue() + 1);

        metric.getValue().compute(MetricEnums.MetricValues.MAX_OF_BUCKET_HITS, (k, v) -> Math.max(v.doubleValue(), amountOfBucketHits));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_OF_BUCKET_HITS, (k, v) -> v.doubleValue() + amountOfBucketHits);
        metric.getValue().compute(MetricEnums.MetricValues.MAX_OF_SMOOTHED_BUCKETS, (k, v) -> Math.max(v.doubleValue(), amountOfSmoothedBuckets));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_OF_SMOOTHED_BUCKETS, (k, v) -> v.longValue() + amountOfSmoothedBuckets);

        if (numOfContexts != 0) {
            metric.getValue().compute(MetricEnums.MetricValues.AVG_SIZE_OF_FEATURE_VALUES, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_SIZE_OF_FEATURE_VALUES).intValue() / numOfContexts);
            metric.getValue().compute(MetricEnums.MetricValues.AVG_NUM_OF_PARTITIONS, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_NUM_OF_PARTITIONS).intValue() / numOfContexts);
            metric.getValue().compute(MetricEnums.MetricValues.AVG_OF_BUCKET_HITS, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_NUM_OF_PARTITIONS).intValue() / numOfContexts);
            metric.getValue().compute(MetricEnums.MetricValues.AVG_OF_SMOOTHED_BUCKETS, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_NUM_OF_PARTITIONS).intValue() / numOfContexts);
        }

        if (buckets.stream().mapToDouble(Double::doubleValue).sum() > 0.0) {
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
        values.put(MetricEnums.MetricValues.WRITES, 0L);

        values.put(MetricEnums.MetricValues.AVG_OF_BUCKET_HITS, 0L);
        values.put(MetricEnums.MetricValues.SUM_OF_BUCKET_HITS, 0L);
        values.put(MetricEnums.MetricValues.MAX_OF_BUCKET_HITS, 0L);

        values.put(MetricEnums.MetricValues.AVG_OF_SMOOTHED_BUCKETS, 0L);
        values.put(MetricEnums.MetricValues.SUM_OF_SMOOTHED_BUCKETS, 0L);
        values.put(MetricEnums.MetricValues.MAX_OF_SMOOTHED_BUCKETS, 0L);

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
        return TimeModelBuilderConf.TIME_MODEL_BUILDER;
    }
}
