package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import fortscale.monitoring.external.stats.samza.collector.samzaMetrics.KafkaSystemConsumerMetrics;
import fortscale.monitoring.external.stats.samza.collector.samzaMetrics.KeyValueChangelogTopicMetrics;
import fortscale.monitoring.external.stats.samza.collector.service.stats.SamzaMetricCollectorMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.apache.commons.collections.keyvalue.MultiKey;

import java.util.*;

import static fortscale.monitoring.external.stats.samza.collector.service.impl.converter.SamzaMetricsConversionUtil.*;

/**
 * converts samza standard metric to stats.
 * key value changelog metric entries example:
 */
public class KafkaSystemConsumerToStatsConverter extends BaseSamzaMetricsToStatsConverter {
    private static final Logger logger = Logger.getLogger(KafkaSystemConsumerToStatsConverter.class);

    public static final String METRIC_NAME = "org.apache.samza.system.kafka.KafkaSystemConsumerMetrics";
    protected List<String> topicOperations;

    /**
     * ctor
     */
    public KafkaSystemConsumerToStatsConverter(StatsService statsService, SamzaMetricCollectorMetrics samzaMetricCollectorMetrics) {
        super(statsService, samzaMetricCollectorMetrics);
        topicOperations = new LinkedList<>();
        Arrays.asList(operations.values()).stream().forEach(operation -> topicOperations.add(operation.value()));
    }

    /**
     * converts metricmessage entries to stats metrics and manual updates
     *
     * @param metricEntries
     * @param jobName       metric message samza task
     * @param time          metric message update time
     * @param hostname      job hostname
     */
    @Override
    public void convert(Map<String, Object> metricEntries, String jobName, long time, String hostname) {
        super.convert(metricEntries, jobName, time, hostname);
        Map<MultiKey, KafkaSystemConsumerMetrics> updatedMetrics = new HashMap<>();

        for (Map.Entry<String, Object> entry : metricEntries.entrySet()) {
            try {

                String entryKey = entry.getKey();
                String topicName = getTopicName(entryKey, topicOperations);
                if (topicName.contains(hostname)) {
                    continue;
                }
                MultiKey multiKey = new MultiKey(jobName, topicName);

                long entryValue = entryValueToLong(entry.getValue());

                KafkaSystemConsumerMetrics metrics;
                // if there is no metric for this store, create one
                if (!metricsMap.containsKey(multiKey)) {
                    metrics = new KafkaSystemConsumerMetrics(statsService, jobName, topicName);
                    metricsMap.put(multiKey, metrics);
                }
                metrics = (KafkaSystemConsumerMetrics) metricsMap.get(multiKey);

                if (entryKey.contains(operations.OFFSET_CHANGE.value()) ||
                        entryKey.contains(operations.TOPIC_PARTITIONS.value()) ||
                        entryKey.contains(operations.POLL_COUNT.value())) {
                    continue;
                } else if (entryKey.contains(operations.BLOCKING_POLL_COUNT.value())) {
                    metrics.setBlockingPoll(entryValue);
                } else if (entryKey.contains(operations.BLOCKING_POLL_TIMEOUT_COUNT.value())) {
                    metrics.setBlockingPollTimeout(entryValue);
                } else if (entryKey.contains(operations.BUFFERED_MESSAGE_COUNT.value())) {
                    metrics.setBufferedMessages(entryValue);
                } else if (entryKey.contains(operations.BYTES_READ.value())) {
                    metrics.setBytesRead(entryValue);
                } else if (entryKey.contains(operations.HIGH_WATERMARK.value())) {
                    metrics.setHighWaterMark(entryValue);
                } else if (entryKey.contains(operations.MESSAGES_BEHIND_HIGH_WATERMARK.value())) {
                    metrics.setMessagesBehindWatermark(entryValue);
                } else if (entryKey.contains(operations.MESSAGES_READ.value())) {
                    metrics.setMessagesRead(entryValue);
                } else if (entryKey.contains(operations.NO_MORE_MESSAGES.value())) {
                    metrics.setNoMoreMessages(entryValue);
                } else if (entryKey.contains(operations.RECONNECTS.value())) {
                    metrics.setReconnects(entryValue);
                } else if (entryKey.contains(operations.SKIPPED_FETCH_REQUESTS.value())) {
                    metrics.setSkippedFetchRequests(entryValue);
                } else {
                    logger.warn("{} is an unknown operation name", entryKey);
                    samzaMetricCollectorMetrics.entriesConversionFailures++;
                }
                updatedMetrics.put(multiKey, metrics);
                samzaMetricCollectorMetrics.convertedEntries++;
            } catch (Exception e) {
                String errMessage = String.format("failed to convert entry %s: %s", entry.getKey(), entry.getValue());
                logger.error(errMessage, e);
            }
        }
        manualUpdateMetricsMap(updatedMetrics, time);
    }

    public enum operations {
        NO_MORE_MESSAGES("no-more-messages"),
        BLOCKING_POLL_COUNT("blocking-poll-count"),
        BLOCKING_POLL_TIMEOUT_COUNT("blocking-poll-timeout-count"),
        BUFFERED_MESSAGE_COUNT("buffered-message-count"),
        POLL_COUNT("poll-count"),
        RECONNECTS("reconnects"),
        SKIPPED_FETCH_REQUESTS("skipped-fetch-requests"),
        MESSAGES_READ("messages-read"),
        OFFSET_CHANGE("offset-change"),
        MESSAGES_BEHIND_HIGH_WATERMARK("messages-behind-high-watermark"),
        HIGH_WATERMARK("high-watermark"),
        BYTES_READ("bytes-read"),
        TOPIC_PARTITIONS("topic-partitions");
        private final String name;

        operations(String s) {
            name = s;
        }

        public String value() {
            return name;
        }

    }


}
