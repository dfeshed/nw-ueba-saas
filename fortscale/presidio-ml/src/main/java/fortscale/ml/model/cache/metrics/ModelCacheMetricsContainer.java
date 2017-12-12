package fortscale.ml.model.cache.metrics;

import org.apache.commons.lang3.builder.ToStringBuilder;
import presidio.monitoring.flush.FlushableMetricContainer;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by barak_schuster on 12/10/17.
 */
public class ModelCacheMetricsContainer implements FlushableMetricContainer {

    public static final String METRIC_NAME = "modelCache";
    private MetricCollectingService metricCollectingService;
    private MetricsExporter metricsExporter;
    private Instant logicalStartTime;

    // cached map of metrics by tags and logical time
    private Map<ModelCacheMetricsKey, Metric> metrics;

    public ModelCacheMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        this.metricCollectingService = metricCollectingService;
        this.metricsExporter = metricsExporter;
        this.metrics = new HashMap<>();
    }

    public void setLogicalStartTime(Instant logicalStartTime) {
        this.logicalStartTime = logicalStartTime;
    }

    private Metric getMetric(String modelConfName) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MetricEnums.MetricTagKeysEnum.MODEL, modelConfName);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, MetricEnums.MetricUnitType.NUMBER.toString());
        ModelCacheMetricsKey key = new ModelCacheMetricsKey(logicalStartTime, tags);

        Metric metric = metrics.computeIfAbsent(key, k -> createNewMetric(tags));

        return metric;
    }

    private Metric createNewMetric(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(MetricEnums.MetricValues.MISS, 0L);
        values.put(MetricEnums.MetricValues.HIT, 0L);
        values.put(MetricEnums.MetricValues.EMPTY_MODEL, 0L);
        return new Metric.MetricBuilder()
                .setMetricName(METRIC_NAME)
                .setMetricReportOnce(true)
                .setMetricUnit(MetricEnums.MetricUnitType.NUMBER)
                .setMetricTags(tags)
                .setMetricLogicTime(logicalStartTime)
                .setMetricMultipleValues(values)
                .build();
    }

    public void flush() {
        // subscribe metric to collecting service
        metrics.values().forEach(metric -> {
            metricCollectingService.addMetric(metric);
            // export metrics to elastic
            metricsExporter.manualExportMetrics(MetricsExporter.MetricBucketEnum.APPLICATION);
        });

        logicalStartTime = null;
        // reset cache
        metrics = new HashMap<>();
    }

    public void incModelCache(String modelConfName) {
        incValue(modelConfName, MetricEnums.MetricValues.HIT);
    }

    public void incEmptyModel(String modelConfName) {
        incValue(modelConfName, MetricEnums.MetricValues.EMPTY_MODEL);
    }

    public void incModelFromDb(String modelConfName) {
        incValue(modelConfName, MetricEnums.MetricValues.MISS);
    }

    private void incValue(String modelConfName, MetricEnums.MetricValues key) {
        getMetric(modelConfName).getValue().compute(key, (k, v) -> v.longValue() + 1);
    }

    /**
     * an inner key for metrics cache
     */
    private static class ModelCacheMetricsKey {
        private Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();

        private Instant logicalStartTime;

        public ModelCacheMetricsKey(Instant logicalStartTime, Map<MetricEnums.MetricTagKeysEnum, String> tags) {
            this.logicalStartTime = logicalStartTime;
            this.tags = tags;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ModelCacheMetricsContainer.ModelCacheMetricsKey)) return false;

            ModelCacheMetricsContainer.ModelCacheMetricsKey that = (ModelCacheMetricsContainer.ModelCacheMetricsKey) o;

            if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;
            return logicalStartTime != null ? logicalStartTime.equals(that.logicalStartTime) : that.logicalStartTime == null;
        }

        @Override
        public int hashCode() {
            int result = tags != null ? tags.hashCode() : 0;
            result = 31 * result + (logicalStartTime != null ? logicalStartTime.hashCode() : 0);
            return result;
        }

        /**
         * @return ToString you know...
         */
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

}
