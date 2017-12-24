package fortscale.aggregation.feature.bucket.metrics;

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
 * Created by barak_schuster on 12/12/17.
 */
public class FeatureBucketAggregatorMetricsContainer implements FlushableMetricContainer {

    private static final String METRIC_NAME = "featureBucketAggregator";
    private MetricCollectingService metricCollectingService;
    private MetricsExporter metricsExporter;

    // cached map of metrics by tags and logical time
    private Map<FeatureBucketAggrMetricsKey, Metric> metrics;

    public FeatureBucketAggregatorMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        this.metricCollectingService = metricCollectingService;
        this.metrics = new HashMap<>();
        this.metricsExporter = metricsExporter;
    }


    @Override
    public void flush() {
        // subscribe metric to collecting service
        metrics.values().forEach(metric -> {
            metricCollectingService.addMetric(metric);
            // export metrics to elastic
            metricsExporter.manualExportMetrics(MetricsExporter.MetricBucketEnum.APPLICATION);
        });

        // reset cache
        metrics = new HashMap<>();
    }

    private Metric getMetric(String featureBucket, Instant logicalStartTime) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MetricEnums.MetricTagKeysEnum.FEATURE_BUCKET, featureBucket);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, MetricEnums.MetricUnitType.NUMBER.toString());
        FeatureBucketAggrMetricsKey key = new FeatureBucketAggrMetricsKey(logicalStartTime, tags);

        Metric metric = metrics.computeIfAbsent(key, k -> createNewMetric(tags, logicalStartTime));

        return metric;
    }

    private Metric createNewMetric(Map<MetricEnums.MetricTagKeysEnum, String> tags, Instant logicalStartTime) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(MetricEnums.MetricValues.NULL_FEATURE_BUCKET_ID, 0L);
        values.put(MetricEnums.MetricValues.FEATURE_BUCKETS, 0L);
        values.put(MetricEnums.MetricValues.FEATURE_BUCKETS_UPDATES, 0L);
        return new Metric.MetricBuilder()
                .setMetricName(METRIC_NAME)
                .setMetricReportOnce(true)
                .setMetricUnit(MetricEnums.MetricUnitType.NUMBER)
                .setMetricTags(tags)
                .setMetricLogicTime(logicalStartTime)
                .setMetricMultipleValues(values)
                .build();
    }

    public void incNullFeatureBucketId(String featureBucket, Instant logicalStartTime) {
        incValue(featureBucket, MetricEnums.MetricValues.HIT, logicalStartTime);
    }

    public void incFeatureBuckets(String featureBucket, Instant logicalStartTime) {
        incValue(featureBucket, MetricEnums.MetricValues.FEATURE_BUCKETS, logicalStartTime);
    }

    public void incFeatureBucketUpdates(String featureBucket, Instant logicalStartTime) {
        incValue(featureBucket, MetricEnums.MetricValues.FEATURE_BUCKETS_UPDATES, logicalStartTime);
    }

    private void incValue(String featureBucket, MetricEnums.MetricValues key, Instant logicalStartTime) {
        getMetric(featureBucket, logicalStartTime).getValue().compute(key, (k, v) -> v.longValue() + 1);
    }


    /**
     * an inner key for metrics cache
     */
    private static class FeatureBucketAggrMetricsKey {
        private Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();

        private Instant logicalStartTime;

        public FeatureBucketAggrMetricsKey(Instant logicalStartTime, Map<MetricEnums.MetricTagKeysEnum, String> tags) {
            this.logicalStartTime = logicalStartTime;
            this.tags = tags;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FeatureBucketAggregatorMetricsContainer.FeatureBucketAggrMetricsKey)) return false;

            FeatureBucketAggregatorMetricsContainer.FeatureBucketAggrMetricsKey that = (FeatureBucketAggregatorMetricsContainer.FeatureBucketAggrMetricsKey) o;

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
