package fortscale.ml.scorer.metrics;

import org.apache.commons.lang3.builder.ToStringBuilder;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static presidio.monitoring.sdk.api.services.enums.MetricEnums.MetricTagKeysEnum.ADE_EVENT_TYPE;
import static presidio.monitoring.sdk.api.services.enums.MetricEnums.MetricTagKeysEnum.SCORER;

/**
 *
 * Created by barak_schuster on 11/27/17.
 */
public class ScoringServiceMetricsContainer {
    public static final String METRIC_NAME = "scoring";
    private MetricCollectingService metricCollectingService;
    // cached map of metrics by tags and logical time
    private Map<ScoringMetricsKey, Metric> scoringMetrics;

    /**
     *
     * @param metricCollectingService
     */
    public ScoringServiceMetricsContainer(MetricCollectingService metricCollectingService) {
        this.metricCollectingService = metricCollectingService;
        this.scoringMetrics = new HashMap<>();
    }

    /**
     * updates relvant scoring metrics by provided data
     * @param logicalStartTime - the logical execution time of the processing (not of the event)
     * @param scorerName
     * @param adeEventType
     * @param score
     */
    public void updateMetic(Instant logicalStartTime, String scorerName, String adeEventType, Double score) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(SCORER, scorerName);
        tags.put(ADE_EVENT_TYPE, adeEventType);
        ScoringMetricsKey key = new ScoringMetricsKey(logicalStartTime,tags);
        Metric metric = scoringMetrics.get(key);
        if (metric == null)
        {
            metric = createNewMetric(logicalStartTime,tags);
            // cache the metric
            scoringMetrics.put(key,metric);
            // subscribe metric to collecting service
            metricCollectingService.addMetric(metric);
        }
        updateMetric(metric,score);
    }

    private void updateMetric(Metric metric, Double score) {
        metric.getValue().compute(MetricEnums.MetricValues.AMOUNT_OF_SCORED,(k,v) -> v.longValue()+1);
        if(score>0) {
            metric.getValue().compute(MetricEnums.MetricValues.AMOUNT_OF_NON_ZERO_SCORE, (k, v) -> v.longValue() + 1);
        }
        metric.getValue().compute(MetricEnums.MetricValues.MAX_SCORE,(k,v) -> Math.max(v.doubleValue(),score));
    }


    /**
     * @param logicalStartTime - the logical execution time of the processing (not of the event)
     * @param tags - tags of the metrics
     * @return new Metric object for logical time
     */
    private Metric createNewMetric(Instant logicalStartTime, Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(MetricEnums.MetricValues.AMOUNT_OF_NON_ZERO_SCORE, 0L);
        values.put(MetricEnums.MetricValues.AMOUNT_OF_SCORED, 0L);
        values.put(MetricEnums.MetricValues.MAX_SCORE, 0D);
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
    private static class ScoringMetricsKey {
        private Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();

        private Instant logicalStartTime;

        public ScoringMetricsKey(Instant logicalStartTime, Map<MetricEnums.MetricTagKeysEnum, String> tags) {
            this.logicalStartTime = logicalStartTime;
            this.tags = tags;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ScoringMetricsKey)) return false;

            ScoringMetricsKey that = (ScoringMetricsKey) o;

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
