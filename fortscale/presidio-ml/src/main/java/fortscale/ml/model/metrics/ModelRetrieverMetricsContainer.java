package fortscale.ml.model.metrics;

import fortscale.ml.model.retriever.CategoricalFeatureValueRetrieverConf;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.util.HashMap;
import java.util.Map;

/**
 * Metrics on category rarity model retriever
 */
public abstract class ModelRetrieverMetricsContainer extends ModelMetricsContainer {
    private String metricName;

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public ModelRetrieverMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter,
                                          String metricName) {
        super(metricCollectingService, metricsExporter);
        this.metricName = metricName;
    }

    /**
     * Updates modeling metrics by provided data
     * @param reads amount of read data
     */
    public void updateMetric(int reads) {
        Metric metric = getMetric();
        metric.getValue().compute(MetricEnums.MetricValues.READS, (k, v) -> v.doubleValue() + reads);
    }


    /**
     * @param tags - tags of the metrics
     * @return new Metric object for logical time
     */
    protected Metric createNewMetric(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(MetricEnums.MetricValues.READS, 0L);
        return new Metric.MetricBuilder()
                .setMetricName(metricName)
                .setMetricReportOnce(true)
                .setMetricUnit(MetricEnums.MetricUnitType.NUMBER)
                .setMetricTags(tags)
                .setMetricLogicTime(logicalTime)
                .setMetricMultipleValues(values)
                .build();
    }

    @Override
    public abstract String getFactoryName();
}
