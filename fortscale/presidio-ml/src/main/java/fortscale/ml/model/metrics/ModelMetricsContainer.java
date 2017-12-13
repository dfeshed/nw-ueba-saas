package fortscale.ml.model.metrics;

import org.apache.commons.lang3.builder.ToStringBuilder;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


public abstract class ModelMetricsContainer implements IModelMetricsContainer {
    protected MetricCollectingService metricCollectingService;
    protected MetricsExporter metricsExporter;
    protected Map<MetricEnums.MetricTagKeysEnum, String> tags;
    protected Instant logicalStartTime;
    protected int numOfContexts;
    // cached map of metrics by tags and logical time
    protected Map<ModelingMetricsKey, Metric> modelingMetrics;



    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public ModelMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        this.metricCollectingService = metricCollectingService;
        this.modelingMetrics = new HashMap<>();
        this.metricsExporter = metricsExporter;
        this.tags = new HashMap<>();
        this.numOfContexts = 0;

    }

    public void addTags(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        this.tags.putAll(tags);
        this.tags.put(MetricEnums.MetricTagKeysEnum.UNIT, MetricEnums.MetricUnitType.NUMBER.toString());
    }

    public void setLogicalStartTime(Instant logicalStartTime) {
        this.logicalStartTime = logicalStartTime;
    }

    public void setNumOfContexts(int numOfContexts) {
        this.numOfContexts = numOfContexts;
    }

    public void flush() {
        // subscribe metric to collecting service
        modelingMetrics.values().forEach(metric -> {
            metricCollectingService.addMetric(metric);
            // export metrics to elastic
            metricsExporter.manualExportMetrics(MetricsExporter.MetricBucketEnum.APPLICATION);
        });

        // reset cache
        modelingMetrics = new HashMap<>();
    }


    /**
     *
     * @return metric
     */
    protected Metric getMetric() {
        Map<MetricEnums.MetricTagKeysEnum, String> metricTags = new HashMap<>(tags);
        ModelingMetricsKey key = new ModelingMetricsKey(logicalStartTime, metricTags);
        Metric metric = modelingMetrics.get(key);
        if (metric == null) {
            metric = createNewMetric(metricTags);
            // cache the metric
            modelingMetrics.put(key, metric);
        }

        return metric;
    }

    /**
     * Create new metric
     * @param tags
     * @return
     */
    protected abstract Metric  createNewMetric(Map<MetricEnums.MetricTagKeysEnum, String> tags);


    /**
     * an inner key for metrics cache
     */
    private static class ModelingMetricsKey {
        private Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        private Instant logicalStartTime;

        public ModelingMetricsKey(Instant logicalStartTime, Map<MetricEnums.MetricTagKeysEnum, String> tags) {
            this.logicalStartTime = logicalStartTime;
            this.tags = tags;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ModelingMetricsKey)) return false;

            ModelingMetricsKey that = (ModelingMetricsKey) o;

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

    @Override
    public abstract String getFactoryName();

}
