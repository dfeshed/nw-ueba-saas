package fortscale.ml.model.metrics;

import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilderConf;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.util.HashMap;
import java.util.Map;

/**
 * Metrics on gaussian prior model builder
 */
public class GaussianPriorModelBuilderMetricsContainer extends ModelMetricsContainer {
    public static final String METRIC_NAME = "gaussian_prior_model_builder";

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public GaussianPriorModelBuilderMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        super(metricCollectingService, metricsExporter);
    }


    /**
     * Updates modeling metrics by provided data
     */
    public void updateMetric(double maxMean, int segmentPriorSize) {
        Metric metric = getMetric();
        metric.getValue().compute(MetricEnums.MetricValues.MAX_MEAN, (k, v) -> maxMean);
        metric.getValue().compute(MetricEnums.MetricValues.AMOUNT_OF_SEGMENT_PRIORS, (k, v) -> v.doubleValue() + segmentPriorSize);
    }




    /**
     * @param tags - tags of the metrics
     * @return new Metric object for logical time
     */
    protected Metric createNewMetric(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(MetricEnums.MetricValues.MAX_MEAN, 0L);
        values.put(MetricEnums.MetricValues.AMOUNT_OF_SEGMENT_PRIORS, 0L);

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
        return GaussianPriorModelBuilderConf.GAUSSIAN_PRIOR_MODEL_BUILDER;
    }
}
