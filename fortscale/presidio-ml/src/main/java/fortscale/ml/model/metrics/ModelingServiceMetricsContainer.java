package fortscale.ml.model.metrics;

import fortscale.ml.model.ModelBuilderData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import presidio.monitoring.flush.FlushableMetricContainer;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static fortscale.ml.model.ModelBuilderData.NoDataReason.ALL_DATA_FILTERED;
import static presidio.monitoring.sdk.api.services.enums.MetricEnums.MetricTagKeysEnum.MODEL;

/**
 * Metrics on models
 */
public class ModelingServiceMetricsContainer implements FlushableMetricContainer {
    public static final String METRIC_NAME = "modeling";
    private MetricCollectingService metricCollectingService;
    private MetricsExporter metricsExporter;

    // cached map of metrics by tags and logical time
    private Map<ModelingMetricsKey, Metric> modelingMetrics;
    //Map<factoryName, IModelMetricsContainer>
    private Map<String, IModelMetricsContainer> modelMetricsContainers;

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public ModelingServiceMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter,
                                           Map<String, IModelMetricsContainer> modelBuilderMetricsContainers) {
        this.metricCollectingService = metricCollectingService;
        this.modelingMetrics = new HashMap<>();
        this.metricsExporter = metricsExporter;
        this.modelMetricsContainers = modelBuilderMetricsContainers;
    }

    /**
     * Set tags to modelMetricsContainers
     *
     * @param factoryNames
     * @param modelConfName
     * @param logicalTime
     * @param numOfContexts
     */
    public void init(Set<String> factoryNames, String modelConfName, Instant logicalTime, int numOfContexts) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MODEL, modelConfName);

        factoryNames.forEach(factoryName -> {
            IModelMetricsContainer modelMetricsContainer = modelMetricsContainers.get(factoryName);
            if (modelMetricsContainer != null) {
                modelMetricsContainers.get(factoryName).addTags(tags);
                modelMetricsContainers.get(factoryName).setLogicalTime(logicalTime);
                modelMetricsContainers.get(factoryName).setNumOfContexts(numOfContexts);
            }
        });
    }

    /**
     * Updates modeling metrics by provided data
     *
     * @param logicalStartTime - the logical execution time of the processing (not of the event)
     * @param modelConfName
     * @param numOfSuccesses
     * @param numOfFailures
     */
    public void updateMetric(Instant logicalStartTime, String modelConfName, long numOfSuccesses, long numOfFailures) {
        Metric metric = getMetric(logicalStartTime, modelConfName);
        metric.getValue().compute(MetricEnums.MetricValues.AMOUNT_OF_SUCCEEDED_MODELS, (k, v) -> v.doubleValue() + numOfSuccesses);
        metric.getValue().compute(MetricEnums.MetricValues.AMOUNT_OF_FAILED_MODELS, (k, v) -> v.doubleValue() + numOfFailures);
        metric.getValue().compute(MetricEnums.MetricValues.AMOUNT_OF_CONTEXTS, (k, v) -> v.doubleValue() + numOfSuccesses + numOfFailures);
    }

    /**
     * updates modeling metrics by provided data
     *
     * @param logicalStartTime
     * @param modelConfName
     * @param noDataReason
     */
    public void updateMetric(Instant logicalStartTime, String modelConfName, ModelBuilderData.NoDataReason noDataReason) {
        Metric metric = getMetric(logicalStartTime, modelConfName);
        if (noDataReason != null && noDataReason.equals(ALL_DATA_FILTERED)) {
            metric.getValue().compute(MetricEnums.MetricValues.AMOUNT_OF_ALL_DATA_FILTERED, (k, v) -> v.doubleValue() + 1);
        }
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

        modelMetricsContainers.values().forEach(metricCollectingService -> metricCollectingService.flush());
    }

    /**
     * @param logicalStartTime
     * @param modelConfName
     * @return metric
     */
    private Metric getMetric(Instant logicalStartTime, String modelConfName) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MODEL, modelConfName);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, MetricEnums.MetricUnitType.NUMBER.toString());

        ModelingMetricsKey key = new ModelingMetricsKey(logicalStartTime, tags);
        Metric metric = modelingMetrics.get(key);
        if (metric == null) {
            metric = createNewMetric(logicalStartTime, tags);
            // cache the metric
            modelingMetrics.put(key, metric);
        }

        return metric;
    }

    /**
     * @param logicalStartTime - the logical execution time of the processing (not of the event)
     * @param tags             - tags of the metrics
     * @return new Metric object for logical time
     */
    private Metric createNewMetric(Instant logicalStartTime, Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(MetricEnums.MetricValues.AMOUNT_OF_SUCCEEDED_MODELS, 0L);
        values.put(MetricEnums.MetricValues.AMOUNT_OF_FAILED_MODELS, 0L);
        values.put(MetricEnums.MetricValues.AMOUNT_OF_ALL_DATA_FILTERED, 0L);
        values.put(MetricEnums.MetricValues.AMOUNT_OF_CONTEXTS, 0L);

        return new Metric.MetricBuilder()
                .setMetricName(METRIC_NAME)
                .setMetricReportOnce(true)
                .setMetricUnit(MetricEnums.MetricUnitType.NUMBER)
                .setMetricTags(tags)
                .setMetricLogicTime(logicalStartTime)
                .setMetricMultipleValues(values)
                .build();
    }


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
}
