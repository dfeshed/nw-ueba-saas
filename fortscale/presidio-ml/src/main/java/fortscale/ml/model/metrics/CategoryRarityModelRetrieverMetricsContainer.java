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
public class CategoryRarityModelRetrieverMetricsContainer extends ModelMetricsContainer {
    public static final String METRIC_NAME = "category_rarity_model_retriever";

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public CategoryRarityModelRetrieverMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        super(metricCollectingService, metricsExporter);
    }

    public void updateMetric(int featureBucketSize) {
        Metric metric = getMetric();
        metric.getValue().compute(MetricEnums.MetricValues.AMOUNT_OF_READ_DATA, (k, v) -> v.doubleValue()+ featureBucketSize);
    }


    /**
     * @param tags - tags of the metrics
     * @return new Metric object for logical time
     */
    protected Metric createNewMetric(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(MetricEnums.MetricValues.AMOUNT_OF_READ_DATA, 0L);
        return new Metric.MetricBuilder()
                .setMetricName(METRIC_NAME)
                .setMetricReportOnce(true)
                .setMetricUnit(MetricEnums.MetricUnitType.NUMBER)
                .setMetricTags(tags)
                .setMetricLogicTime(logicalStartTime)
                .setMetricMultipleValues(values)
                .build();
    }

    @Override
    public String getFactoryName() {
        return CategoricalFeatureValueRetrieverConf.FACTORY_NAME;
    }
}
