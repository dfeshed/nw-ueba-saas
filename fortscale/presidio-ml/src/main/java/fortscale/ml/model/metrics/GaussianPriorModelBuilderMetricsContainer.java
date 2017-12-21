package fortscale.ml.model.metrics;

import fortscale.ml.model.builder.gaussian.ContinuousMaxHistogramModelBuilderConf;
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
     * @param sd
     */
    public void updateMetric(long sd) {

    }




    /**
     * @param tags - tags of the metrics
     * @return new Metric object for logical time
     */
    protected Metric createNewMetric(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();

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
