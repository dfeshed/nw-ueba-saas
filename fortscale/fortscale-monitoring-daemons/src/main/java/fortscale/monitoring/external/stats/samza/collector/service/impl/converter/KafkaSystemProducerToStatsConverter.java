package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import fortscale.monitoring.external.stats.samza.collector.samzaMetrics.KafkaSystemConsumerMetrics;
import fortscale.monitoring.external.stats.samza.collector.samzaMetrics.KafkaSystemProducerMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.apache.commons.collections.keyvalue.MultiKey;

import java.util.*;

import static fortscale.monitoring.external.stats.samza.collector.service.impl.converter.SamzaMetricsConversionUtil.entryValueToLong;
import static fortscale.monitoring.external.stats.samza.collector.service.impl.converter.SamzaMetricsConversionUtil.getTopicName;

/**
 * converts samza standard metric to stats.
 * key value changelog metric entries example:
 */
public class KafkaSystemProducerToStatsConverter extends BaseSamzaMetricsToStatsConverter {
    private static final Logger logger = Logger.getLogger(KafkaSystemProducerToStatsConverter.class);

    public static final String METRIC_NAME = "org.apache.samza.system.kafka.KafkaSystemProducerMetrics";

    /**
     * ctor
     */
    public KafkaSystemProducerToStatsConverter(StatsService statsService) {
        super(statsService);
    }

    /**
     * converts metricmessage entries to stats metrics
     *
     * @param metricEntries
     * @param jobName       metric message samza task
     * @param time          metric message update time
     * @param hostname      job hostname
     */
    @Override
    public void convert(Map<String, Object> metricEntries, String jobName, long time, String hostname) {
        Map updatedMetrics = new HashMap<>();

        for (Map.Entry<String, Object> entry : metricEntries.entrySet()) {
            try {
                String entryKey = entry.getKey();

                double doubleEntryValue = 0;
                long entryValue = 0;
                if (entry.getValue().getClass().equals(double.class)) {
                    doubleEntryValue = (double) entry.getValue();
                } else {
                    entryValue = entryValueToLong(entry.getValue());
                }
                KafkaSystemProducerMetrics metrics;
                // if there is no metric for this store, create one
                if (!metricsMap.containsKey(jobName)) {
                    metrics = new KafkaSystemProducerMetrics(statsService, jobName);
                    metricsMap.put(jobName, metrics);
                }
                metrics = (KafkaSystemProducerMetrics) metricsMap.get(jobName);

                if (entryKey.equals(operations.FLUSH_MS.value())) {
                    metrics.setFlushSeconds(doubleEntryValue / 1000);
                } else if (entryKey.equals(operations.FLUSH_FAILED.value())) {
                    metrics.setFlushesFailures(entryValue);
                } else if (entryKey.equals(operations.FLUSHES.value())) {
                    metrics.setFlushes(entryValue);
                } else if (entryKey.equals(operations.PRODUCER_RETRIES.value())) {
                    metrics.setRetries(entryValue);
                } else if (entryKey.equals(operations.SEND_FAILED.value())) {
                    metrics.setMessagesSentFailures(entryValue);
                } else if (entryKey.equals(operations.PRODUCER_SENDS.value())) {
                    metrics.setMessagesSent(entryValue);
                } else if (entryKey.equals(operations.SEND_SUCCESS.value())) {
                    continue;
                } else {
                    String errorMsg = String.format("topic %s has an unknown operation name", entry.getKey());
                    logger.error(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
                updatedMetrics.put(jobName, metrics);
            } catch (Exception e) {
                String errMessage = String.format("failed to convert entry %s: %s", entry.getKey(), entry.getValue());
                logger.error(errMessage, e);
            }
        }
        manualUpdateMetricsMap(updatedMetrics, time);
    }



    public enum operations {
        FLUSH_MS("flush-ms"),
        SEND_FAILED("send-failed"),
        SEND_SUCCESS("send-success"),
        FLUSH_FAILED("flush-failed"),
        FLUSHES("flushes"),
        PRODUCER_SENDS("producer-sends"),
        PRODUCER_RETRIES("producer-retries");
        private final String name;

        operations(String s) {
            name = s;
        }

        public String value() {
            return name;
        }

    }


}
