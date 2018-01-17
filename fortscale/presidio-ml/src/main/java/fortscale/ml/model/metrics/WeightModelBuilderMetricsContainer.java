package fortscale.ml.model.metrics;

import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.smart.record.conf.ClusterConf;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;
import static presidio.monitoring.sdk.api.services.enums.MetricEnums.MetricTagKeysEnum.FEATURE_NAME;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.ml.model.builder.smart_weights.WeightsModelBuilderConf.WEIGHTS_MODEL_BUILDER;
import static presidio.monitoring.sdk.api.services.enums.MetricEnums.MetricTagKeysEnum.GROUP_NAME;

/**
 * Metrics on weight model builder
 */
public class WeightModelBuilderMetricsContainer extends ModelMetricsContainer {
    public static final String METRIC_NAME = "weight_model_builder";

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public WeightModelBuilderMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        super(metricCollectingService, metricsExporter);
    }


    /**
     * Updates modeling metrics by provided data
     * @param clusterConfs
     */
    public void updateMetric( List<ClusterConf> clusterConfs) {
        clusterConfs.forEach(clusterConf -> {
            Map<MetricEnums.MetricTagKeysEnum, String> metricTags = new HashMap<>(tags);
            metricTags.put(FEATURE_NAME, clusterConf.getAggregationRecordNames().get(0));
            Metric metric = getMetric(metricTags);
            metric.getValue().compute(MetricEnums.MetricValues.WEIGHT, (k, v) -> clusterConf.getWeight());
        });
    }

    /**
     * @param tags - tags of the metrics
     * @return new Metric object for logical time
     */
    protected Metric createNewMetric(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(MetricEnums.MetricValues.WEIGHT, 0L);
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
        return WEIGHTS_MODEL_BUILDER;
    }
}
