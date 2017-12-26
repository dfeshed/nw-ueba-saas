package fortscale.aggregation.creator.metrics;

import org.apache.commons.lang3.builder.ToStringBuilder;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.monitoring.flush.FlushableMetricContainer;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by barak_schuster on 12/24/17.
 */
public class AggregationRecordsCreatorMetricsContainer implements FlushableMetricContainer {
    private static final String METRIC_NAME = "AggregationRecordsCreator";
    private MetricCollectingService metricCollectingService;
    private MetricsExporter metricsExporter;
    private Map<AggregationRecordsCreatorMetricsKey, Metric> metrics;

    public AggregationRecordsCreatorMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        this.metricCollectingService = metricCollectingService;
        this.metricsExporter = metricsExporter;
        this.metrics = new HashMap<>();
    }

    public void updateMetrics(AdeAggregationRecord adeAggregationRecord) {
        String featureName = adeAggregationRecord.getFeatureName();
        Double featureValue = adeAggregationRecord.getFeatureValue();
        AggregatedFeatureType aggregatedFeatureType = adeAggregationRecord.getAggregatedFeatureType();
        String adeEventType = adeAggregationRecord.getAdeEventType();
        Instant startInstant = adeAggregationRecord.getStartInstant();

        Metric metric = getMetric(featureName, aggregatedFeatureType, adeEventType, startInstant);

        updateMetric(featureValue, metric);
    }

    private void updateMetric(Double featureValue, Metric metric) {
        metric.getValue().compute(MetricEnums.MetricValues.AGGREGATIONS,(k, v) -> v.longValue()+1);
        if(featureValue>0) {
            metric.getValue().compute(MetricEnums.MetricValues.AMOUNT_OF_NON_ZERO_FEATURE_VALUES, (k, v) -> v.longValue() + 1);
        }
        metric.getValue().compute(MetricEnums.MetricValues.MAX_FEATURE_VALUE,(k,v) -> Math.max(v.doubleValue(),featureValue));
    }

    private Metric getMetric(String featureName, AggregatedFeatureType aggregatedFeatureType, String adeEventType, Instant logicalStartTime) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MetricEnums.MetricTagKeysEnum.FEATURE_NAME, featureName);
        tags.put(MetricEnums.MetricTagKeysEnum.AGGREGATED_FEATURE_TYPE, aggregatedFeatureType.toString());
        tags.put(MetricEnums.MetricTagKeysEnum.ADE_EVENT_TYPE, adeEventType);

        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, MetricEnums.MetricUnitType.NUMBER.toString());
        AggregationRecordsCreatorMetricsKey key = new AggregationRecordsCreatorMetricsKey( tags,logicalStartTime);

        Metric metric = metrics.computeIfAbsent(key, k -> createNewMetric(tags, logicalStartTime));

        return metric;
    }

    private Metric createNewMetric(Map<MetricEnums.MetricTagKeysEnum, String> tags, Instant logicalStartTime) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(MetricEnums.MetricValues.AGGREGATIONS, 0L);
        values.put(MetricEnums.MetricValues.AMOUNT_OF_NON_ZERO_FEATURE_VALUES, 0L);
        values.put(MetricEnums.MetricValues.MAX_FEATURE_VALUE, 0D);

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

    /**
     * an inner key for metrics cache
     */
    private static class AggregationRecordsCreatorMetricsKey
    {
        private Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();

        private Instant logicalStartTime;

        public AggregationRecordsCreatorMetricsKey(Map<MetricEnums.MetricTagKeysEnum, String> tags, Instant logicalStartTime) {
            this.tags = tags;
            this.logicalStartTime = logicalStartTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AggregationRecordsCreatorMetricsContainer.AggregationRecordsCreatorMetricsKey)) return false;

            AggregationRecordsCreatorMetricsContainer.AggregationRecordsCreatorMetricsKey that = (AggregationRecordsCreatorMetricsContainer.AggregationRecordsCreatorMetricsKey) o;

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
