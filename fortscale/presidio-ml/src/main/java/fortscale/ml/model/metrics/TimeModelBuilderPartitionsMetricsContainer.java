package fortscale.ml.model.metrics;

import fortscale.ml.model.builder.TimeModelBuilderConf;
import fortscale.utils.time.TimeService;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Metrics on time model builder
 */
public class TimeModelBuilderPartitionsMetricsContainer extends ModelMetricsContainer {
    public static final String METRIC_NAME = "time_model_builder_partitions";
    public int metricTimePartitionResolution;

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public TimeModelBuilderPartitionsMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter, int metricTimePartitionResolution) {
        super(metricCollectingService, metricsExporter);
        this.metricTimePartitionResolution = metricTimePartitionResolution;
    }


    /**
     * Updates modeling metrics by provided data:
     * update amount of contexts per hour.
     *
     * @param time
     */
    public void incNumOfUsers(long time, int timeResolution) {
        Instant floorInstant = TimeService.floorTime(Instant.ofEpochSecond(time), timeResolution);
        int partitionNumber = calcTimePartitionNumber(Instant.ofEpochSecond(time), floorInstant);

        Map<MetricEnums.MetricTagKeysEnum, String> metricTags = new HashMap<>(tags);
        metricTags.put(MetricEnums.MetricTagKeysEnum.TIME, Integer.toString(partitionNumber));

        Metric metric = getMetric(metricTags);
        metric.getValue().compute(MetricEnums.MetricValues.AMOUNT_OF_CONTEXTS, (k, v) -> v.longValue() + 1);
    }


    /**
     * @param tags - tags of the metrics
     * @return new Metric object for logical time
     */
    protected Metric createNewMetric(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(MetricEnums.MetricValues.AMOUNT_OF_CONTEXTS, 0L);

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
        return TimeModelBuilderConf.TIME_MODEL_BUILDER;
    }

    /**
     * Calc partitions number
     * e.g: instant 04.12.17:03:00:00, floorInstant: 04.12.17:00:00:00. result: 3
     *
     * @param instant
     * @param floorInstant
     * @return partitions number of instant depends on floorInstant
     */
    private int calcTimePartitionNumber(Instant instant, Instant floorInstant) {
        Duration duration = Duration.between(floorInstant, instant);
        Long currentTimePartitionNumber = duration.getSeconds() / metricTimePartitionResolution;
        return currentTimePartitionNumber.intValue();
    }
}
