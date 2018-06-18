package fortscale.ml.model.metrics;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.builder.gaussian.ContinuousMaxHistogramModelBuilderConf;
import presidio.monitoring.sdk.api.services.model.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.util.HashMap;
import java.util.Map;

/**
 * Metrics on max continuous model builder
 */
public class MaxContinuousModelBuilderMetricsContainer extends ModelMetricsContainer {
    public static final String METRIC_NAME = "max_continuous_model_builder";

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public MaxContinuousModelBuilderMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        super(metricCollectingService, metricsExporter);
    }


    /**
     * Updates modeling metrics by provided data
     *
     * @param numOfPartitions
     * @param continuous
     * @param maxContinuous
     */
    public void updateMetric(long numOfPartitions, ContinuousDataModel continuous, ContinuousDataModel maxContinuous) {

        double continuousMean = continuous.getMean();
        double maxContinuousMean = maxContinuous.getMean();
        double continuousSd = continuous.getSd();
        double maxContinuousSd = maxContinuous.getSd();
        long continuousN = continuous.getN();
        long maxContinuousN = maxContinuous.getN();
        double continuousMaxValue = continuous.getMaxValue();
        double maxContinuousMaxValue = maxContinuous.getMaxValue();

        Metric metric = getMetric();

        metric.getValue().compute(MetricEnums.MetricValues.MAX_NUM_OF_PARTITIONS, (k, v) -> Math.max(v.doubleValue(), numOfPartitions));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_NUM_OF_PARTITIONS, (k, v) -> v.doubleValue() + numOfPartitions);

        metric.getValue().compute(MetricEnums.MetricValues.MAX_CONTINUOUS_MEAN, (k, v) -> Math.max(v.doubleValue(), continuousMean));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_CONTINUOUS_MEAN, (k, v) -> v.doubleValue() + continuousMean);
        metric.getValue().compute(MetricEnums.MetricValues.MAX_MAX_CONTINUOUS_MEAN, (k, v) -> Math.max(v.doubleValue(), maxContinuousMean));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_MAX_CONTINUOUS_MEAN, (k, v) -> v.doubleValue() + maxContinuousMean);

        metric.getValue().compute(MetricEnums.MetricValues.MAX_CONTINUOUS_SD, (k, v) -> Math.max(v.doubleValue(), continuousSd));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_CONTINUOUS_SD, (k, v) -> v.doubleValue() + continuousSd);
        metric.getValue().compute(MetricEnums.MetricValues.MAX_MAX_CONTINUOUS_SD, (k, v) -> Math.max(v.doubleValue(), maxContinuousSd));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_MAX_CONTINUOUS_SD, (k, v) -> v.doubleValue() + maxContinuousSd);

        metric.getValue().compute(MetricEnums.MetricValues.MAX_CONTINUOUS_N, (k, v) -> Math.max(v.doubleValue(), continuousN));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_CONTINUOUS_N, (k, v) -> v.doubleValue() + continuousN);
        metric.getValue().compute(MetricEnums.MetricValues.MAX_MAX_CONTINUOUS_N, (k, v) -> Math.max(v.doubleValue(), maxContinuousN));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_MAX_CONTINUOUS_N, (k, v) -> v.doubleValue() + maxContinuousN);

        metric.getValue().compute(MetricEnums.MetricValues.MAX_CONTINUOUS_MAX_VALUE, (k, v) -> Math.max(v.doubleValue(), continuousMaxValue));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_CONTINUOUS_MAX_VALUE, (k, v) -> v.doubleValue() + continuousMaxValue);
        metric.getValue().compute(MetricEnums.MetricValues.MAX_MAX_CONTINUOUS_MAX_VALUE, (k, v) -> Math.max(v.doubleValue(), maxContinuousMaxValue));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_MAX_CONTINUOUS_MAX_VALUE, (k, v) -> v.doubleValue() + maxContinuousMaxValue);

        metric.getValue().compute(MetricEnums.MetricValues.WRITES, (k, v) -> v.longValue() + 1);

        if (numOfContexts != 0) {
            metric.getValue().compute(MetricEnums.MetricValues.AVG_NUM_OF_PARTITIONS, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_NUM_OF_PARTITIONS).intValue() / numOfContexts);
            metric.getValue().compute(MetricEnums.MetricValues.AVG_CONTINUOUS_MEAN, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_CONTINUOUS_MEAN).intValue() / numOfContexts);
            metric.getValue().compute(MetricEnums.MetricValues.AVG_MAX_CONTINUOUS_MEAN, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_MAX_CONTINUOUS_MEAN).intValue() / numOfContexts);
            metric.getValue().compute(MetricEnums.MetricValues.AVG_CONTINUOUS_SD, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_CONTINUOUS_SD).intValue() / numOfContexts);
            metric.getValue().compute(MetricEnums.MetricValues.AVG_MAX_CONTINUOUS_SD, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_MAX_CONTINUOUS_SD).intValue() / numOfContexts);
            metric.getValue().compute(MetricEnums.MetricValues.AVG_CONTINUOUS_N, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_CONTINUOUS_N).intValue() / numOfContexts);
            metric.getValue().compute(MetricEnums.MetricValues.AVG_MAX_CONTINUOUS_N, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_MAX_CONTINUOUS_N).intValue() / numOfContexts);
            metric.getValue().compute(MetricEnums.MetricValues.AVG_CONTINUOUS_MAX_VALUE, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_CONTINUOUS_MAX_VALUE).intValue() / numOfContexts);
            metric.getValue().compute(MetricEnums.MetricValues.AVG_MAX_CONTINUOUS_MAX_VALUE, (k, v) -> (double) metric.getValue().get(MetricEnums.MetricValues.SUM_MAX_CONTINUOUS_MAX_VALUE).intValue() / numOfContexts);
        }
    }


    public void updateMaxValuesResult(int resolution) {
        Metric metric = getMetric();
        metric.getValue().compute(MetricEnums.MetricValues.MIN_RESOLUTION, (k, v) -> Math.max(v.doubleValue(), resolution));
        metric.getValue().compute(MetricEnums.MetricValues.MAX_RESOLUTION, (k, v) -> Math.max(v.doubleValue(), resolution));
        metric.getValue().compute(MetricEnums.MetricValues.SUM_RESOLUTION, (k, v) -> v.doubleValue() + resolution);

        if (numOfContexts != 0) {
            metric.getValue().compute(MetricEnums.MetricValues.AVG_RESOLUTION, (k, v) -> metric.getValue().get(MetricEnums.MetricValues.SUM_RESOLUTION).intValue() / numOfContexts);
        }
    }

    /**
     * @param tags - tags of the metrics
     * @return new Metric object for logical time
     */
    protected Metric createNewMetric(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(MetricEnums.MetricValues.MAX_NUM_OF_PARTITIONS, 0L);
        values.put(MetricEnums.MetricValues.SUM_NUM_OF_PARTITIONS, 0L);
        values.put(MetricEnums.MetricValues.AVG_NUM_OF_PARTITIONS, 0L);

        values.put(MetricEnums.MetricValues.MIN_RESOLUTION, 0L);
        values.put(MetricEnums.MetricValues.MAX_RESOLUTION, 0L);
        values.put(MetricEnums.MetricValues.AVG_RESOLUTION, 0L);
        values.put(MetricEnums.MetricValues.SUM_RESOLUTION, 0L);

        values.put(MetricEnums.MetricValues.MAX_CONTINUOUS_MEAN, 0L);
        values.put(MetricEnums.MetricValues.AVG_CONTINUOUS_MEAN, 0L);
        values.put(MetricEnums.MetricValues.SUM_CONTINUOUS_MEAN, 0L);

        values.put(MetricEnums.MetricValues.MAX_MAX_CONTINUOUS_MEAN, 0L);
        values.put(MetricEnums.MetricValues.AVG_MAX_CONTINUOUS_MEAN, 0L);
        values.put(MetricEnums.MetricValues.SUM_MAX_CONTINUOUS_MEAN, 0L);

        values.put(MetricEnums.MetricValues.MAX_CONTINUOUS_SD, 0L);
        values.put(MetricEnums.MetricValues.AVG_CONTINUOUS_SD, 0L);
        values.put(MetricEnums.MetricValues.SUM_CONTINUOUS_SD, 0L);

        values.put(MetricEnums.MetricValues.MAX_MAX_CONTINUOUS_SD, 0L);
        values.put(MetricEnums.MetricValues.AVG_MAX_CONTINUOUS_SD, 0L);
        values.put(MetricEnums.MetricValues.SUM_MAX_CONTINUOUS_SD, 0L);

        values.put(MetricEnums.MetricValues.MAX_CONTINUOUS_N, 0L);
        values.put(MetricEnums.MetricValues.AVG_CONTINUOUS_N, 0L);
        values.put(MetricEnums.MetricValues.SUM_CONTINUOUS_N, 0L);

        values.put(MetricEnums.MetricValues.MAX_MAX_CONTINUOUS_N, 0L);
        values.put(MetricEnums.MetricValues.AVG_MAX_CONTINUOUS_N, 0L);
        values.put(MetricEnums.MetricValues.SUM_MAX_CONTINUOUS_N, 0L);

        values.put(MetricEnums.MetricValues.MAX_CONTINUOUS_MAX_VALUE, 0L);
        values.put(MetricEnums.MetricValues.AVG_CONTINUOUS_MAX_VALUE, 0L);
        values.put(MetricEnums.MetricValues.SUM_CONTINUOUS_MAX_VALUE, 0L);


        values.put(MetricEnums.MetricValues.MAX_MAX_CONTINUOUS_MAX_VALUE, 0L);
        values.put(MetricEnums.MetricValues.AVG_MAX_CONTINUOUS_MAX_VALUE, 0L);
        values.put(MetricEnums.MetricValues.SUM_MAX_CONTINUOUS_MAX_VALUE, 0L);

        values.put(MetricEnums.MetricValues.WRITES, 0L);

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
        return ContinuousMaxHistogramModelBuilderConf.CONTINUOUS_MAX_HISTOGRAM_MODEL_BUILDER;
    }
}
