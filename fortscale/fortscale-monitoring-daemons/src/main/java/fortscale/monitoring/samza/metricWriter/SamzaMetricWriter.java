package fortscale.monitoring.samza.metricWriter;

import fortscale.monitoring.samza.metrics.*;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.samza.metricMessageModels.MetricMessage;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by cloudera on 5/10/16.
 */
public class SamzaMetricWriter {
    private static final Logger logger = Logger.getLogger(SamzaMetricWriter.class);
    private Map<String, KafkaSystemProducerMetricsService> kafkaSystemProducerMetricServices;
    private Map<String, KeyValueStoreMetricsService> keyValueStoreMetricsServices;
    private Map<String, KafkaSystemConsumerMetricsService> kafkaSystemConsumerMetricsServices;
    private StatsService statsService;

    public SamzaMetricWriter(StatsService statsService)
    {
        this.statsService=statsService;
    }

    /**
     * rewrite kafkaSystemProducerMetrics to kafka
     *
     * @param metricMessage
     */
    protected void updateKafkaSystemProducerMetric(MetricMessage metricMessage) {
        logger.debug("Updating KafkaSystemProducerMetrics with: {}", metricMessage.toString());
        Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();
        for (Map.Entry<String, Object> entry : metric.get("org.apache.samza.system.kafka.KafkaSystemProducerMetrics").entrySet()) {
            String entryName = entry.getKey();
            String topic = entryName.split("-")[0];
            KafkaSystemProducerMetricsService kafkaSystemProducerMetricsService = kafkaSystemProducerMetricServices.get(topic);

            // if there is no metric for this topic, create one
            if (kafkaSystemProducerMetricsService == null) {
                kafkaSystemProducerMetricsService = new KafkaSystemProducerMetricsService(statsService, topic);
                kafkaSystemProducerMetricsService.getKafkaSystemProducerMetrics().manualUpdate(metricMessage.getHeader().getTime());
            }
            if (entryName.endsWith("flushes")) {
                kafkaSystemProducerMetricsService.getKafkaSystemProducerMetrics().setFlushes(((Integer) entry.getValue()).longValue());
            }
            if (entryName.endsWith("flush-failed")) {
                kafkaSystemProducerMetricsService.getKafkaSystemProducerMetrics().setFlushFailed(((Integer) entry.getValue()).longValue());
            }
            if (entryName.endsWith("flush-ns")) {
                kafkaSystemProducerMetricsService.getKafkaSystemProducerMetrics().setFlushSeconds((double) entry.getValue());
            }
            if (entryName.endsWith("producer-retries")) {
                kafkaSystemProducerMetricsService.getKafkaSystemProducerMetrics().setProducerRetries(((Integer) entry.getValue()).longValue());
            }
            if (entryName.endsWith("producer-send-failed")) {
                kafkaSystemProducerMetricsService.getKafkaSystemProducerMetrics().setProducerSendFailed(((Integer) entry.getValue()).longValue());
            }
            if (entryName.endsWith("producer-send-success")) {
                kafkaSystemProducerMetricsService.getKafkaSystemProducerMetrics().setProducerSendSuccess(((Integer) entry.getValue()).longValue());
            }
            if (entryName.endsWith("producer-sends")) {
                kafkaSystemProducerMetricsService.getKafkaSystemProducerMetrics().setProducerSends(((Integer) entry.getValue()).longValue());
            }
            kafkaSystemProducerMetricServices.put(topic, kafkaSystemProducerMetricsService);
        }
    }

    /**
     * rewrite KeyValueStoreMetrics to KeyValueStoreMetrics
     *
     * @param metricMessage
     */
    protected void updateKeyValueStoreMetrics(MetricMessage metricMessage) {
        logger.debug("Updating KeyValueStoreMetrics with: {}", metricMessage.toString());
        Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();
        for (Map.Entry<String, Object> entry : metric.get("org.apache.samza.storage.kv.KeyValueStoreMetrics").entrySet()) {
            String storeName = "";

            KeyValueStoreMetrics.StoreOperation storeOperation =null;
            // entry is from pattern: ${store_name}-${store-operation} i.e. "entity_events_store-puts"
            // we want to get the entry store operation name
            Optional optionalStoreOperation = Stream.of(KeyValueStoreMetrics.StoreOperation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalStoreOperation != Optional.empty()) {
                storeOperation = (KeyValueStoreMetrics.StoreOperation) optionalStoreOperation.get();
                // remove store operation from store name
                storeName = entry.getKey().replaceAll(String.format("-%s",storeOperation.value()), "");
            }

            if (storeOperation == null) {
                String errorMsg = String.format("store %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            KeyValueStoreMetricsService keyValueStoreMetricsService = keyValueStoreMetricsServices.get(storeName);

            // if there is no metric for this topic, create one
            if (keyValueStoreMetricsService == null) {
                keyValueStoreMetricsService = new KeyValueStoreMetricsService(statsService, storeName);
            }


            // we do not monitor "alls" operations
            if (KeyValueStoreMetrics.StoreOperation.ALLS.equals(storeOperation)) {
                continue;
            }

            if (KeyValueStoreMetrics.StoreOperation.FLUSHES.equals(storeOperation)) {
                keyValueStoreMetricsService.getKeyValueStoreMetrics().setNumberOfFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.GETS.equals(storeOperation)) {
                keyValueStoreMetricsService.getKeyValueStoreMetrics().setNumberOfQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.GET_ALLS.equals(storeOperation)) {
                keyValueStoreMetricsService.getKeyValueStoreMetrics().setNumberOfFullTableScans(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.RANGES.equals(storeOperation)) {
                keyValueStoreMetricsService.getKeyValueStoreMetrics().setNumberOfRangeQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.DELETES.equals(storeOperation)) {
                keyValueStoreMetricsService.getKeyValueStoreMetrics().setNumberOfDeletes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.DELETE_ALLS.equals(storeOperation)) {
                keyValueStoreMetricsService.getKeyValueStoreMetrics().setNumberOfDeleteAlls(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.BYTES_WRITTEN.equals(storeOperation)) {
                keyValueStoreMetricsService.getKeyValueStoreMetrics().setNumberOfBytesWritten(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.BYTES_READ.equals(storeOperation)) {
                keyValueStoreMetricsService.getKeyValueStoreMetrics().setNumberOfBytesRead(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.PUTS.equals(storeOperation)) {
                keyValueStoreMetricsService.getKeyValueStoreMetrics().setNumberOfWrites(((Integer) entry.getValue()).longValue());
            }
            // update metric time
            keyValueStoreMetricsService.getKeyValueStoreMetrics().manualUpdate(metricMessage.getHeader().getTime());
            keyValueStoreMetricsServices.put(storeName, keyValueStoreMetricsService);
        }
    }

    protected void updateKafkaSystemConsumerMetrics(MetricMessage metricMessage) {
        logger.debug("Updating KafkaSystemConsumerMetrics with: {}", metricMessage.toString());
        Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();
        for (Map.Entry<String, Object> entry : metric.get("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics").entrySet()) {
            KafkaSystemConsumerMetrics.TopicOperation topicOperation = null;
            String topicName ="";

            // entry can be from pattern: ${topic_name}-${topic-operation} i.e. "kafka-fortscale-aggr-feature-events-score-0-bytes-read"
            // we want to get the entry topic operation name
            Optional optianalTopicOperation = Stream.of(KafkaSystemConsumerMetrics.TopicOperation.values()).filter(x -> entry.getKey().contains(x.value())).findFirst();

            if (optianalTopicOperation != Optional.empty()) {
                topicOperation = (KafkaSystemConsumerMetrics.TopicOperation) optianalTopicOperation.get();
                // remove topic operation from topic name
                topicName = entry.getKey().replaceAll(topicOperation.value(), "");
                // remove topic partition from topic name
                topicName = entry.getKey().substring(0,topicName.length()-3);
            }

            // entry can be from pattern: ${topic_status}-SystemStreamPartition-[kafka,${topic_name},${partition}] i.e. "kafka-fortscale-aggr-feature-events-score-0-bytes-read"
            Optional optinalTopicStatus = Stream.of(KafkaSystemConsumerMetrics.TopicStatus.values()).filter(x -> entry.getKey().contains(x.value())).findFirst();
            KafkaSystemConsumerMetrics.TopicStatus topicStatus = null;
            if (optinalTopicStatus != Optional.empty()) {
                topicStatus = (KafkaSystemConsumerMetrics.TopicStatus) optinalTopicStatus.get();
                // remove SystemStreamPartition and topic status from topic name
                if (topicStatus.equals(KafkaSystemConsumerMetrics.TopicStatus.POLL_COUNT))
                    continue;
                topicName = (entry.getKey().replaceAll(String.format("%s-SystemStreamPartition", topicStatus.value()), "")).split(",")[1];

            }
            if (topicOperation == null && topicStatus == null) {
                String errorMsg = String.format("topic %s has an unknown action name",  entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            KafkaSystemConsumerMetricsService kafkaSystemConsumerMetricsService = kafkaSystemConsumerMetricsServices.get(topicName);

            // if there is no metric for this topic, create one
            if (kafkaSystemConsumerMetricsService == null) {
                kafkaSystemConsumerMetricsService = new KafkaSystemConsumerMetricsService(statsService, topicName);
            }


            // we do not monitor topic partitions count
            if (KafkaSystemConsumerMetrics.TopicOperation.TOPIC_PARTITIONS.equals(topicOperation)) {
                continue;
            }

            if (KafkaSystemConsumerMetrics.TopicOperation.RECONNECTS.equals(topicOperation)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setNumberOfReconnects(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.SKIPPED_FETCH_REQUESTS.equals(topicOperation)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setNumberOfSkippedFetchRequests(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.MESSAGES_READ.equals(topicOperation)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setNumberOfMessagesRead(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.OFFSET_CHANGE.equals(topicOperation)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setOffsetChange(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.MESSAGES_BEHIND_HIGH_WATERMARK.equals(topicOperation)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setNumberOfMessagesBehindWatermark(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.BYTES_READ.equals(topicOperation)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setNumberOfBytesRead(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.HIGH_WATERMARK.equals(topicOperation))
            {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setNumberOfHighWaterMark(((Integer)entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.BLOCKING_POLL_COUNT.equals(topicStatus)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setBlockingPollCount(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.NO_MORE_MESSAGES.equals(topicStatus)) {
                int noMoreMessages = 0;
                if (topicStatus.equals("true")) {
                    noMoreMessages = 1;
                }
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setNoMoreMessages(noMoreMessages);
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.BLOCKING_POLL_TIMEOUT_COUNT.equals(topicStatus)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setBlockingPollTimeoutCount(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.BUFFERED_MESSAGE_COUNT.equals(topicStatus)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setBufferedMessageCount(((Integer) entry.getValue()).longValue());
            }
            // update metric time
            kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().manualUpdate(metricMessage.getHeader().getTime());
            kafkaSystemConsumerMetricsServices.put(topicName, kafkaSystemConsumerMetricsService);
        }
    }

    /**
     * rewrite samza metrics to kafka metrics topic as a tagged EngineData object
     *
     * @param metricMessage
     */
    public void handleSamzaMetric(MetricMessage metricMessage) {
        Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();
        if (metric.get("org.apache.samza.storage.kv.KeyValueStoreMetrics") != null) {
            updateKeyValueStoreMetrics(metricMessage);
        }
        if (metric.get("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics") != null) {
            updateKafkaSystemConsumerMetrics(metricMessage);
        }
        if (metric.get(KeyValueChangeLogTopicMetrics.METRIC_NAME)!=null)
        {
            updateKeyValueChangeLogTopicMetrics(metricMessage);
        }
        statsService.ManualUpdatePush();
        // todo: add org.apache.samza.metrics.JvmMetrics
        // todo: add org.apache.samza.container.TaskInstanceMetrics
    }

    private void updateKeyValueChangeLogTopicMetrics(MetricMessage metricMessage) {
        logger.debug("Updating KeyValueChangeLogTopicMetrics with: {}", metricMessage.toString());
        Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();
        for (Map.Entry<String, Object> entry : metric.get(KeyValueChangeLogTopicMetrics.METRIC_NAME).entrySet()) {
            String storeName = "";

            KeyValueChangeLogTopicMetrics.StoreOperation storeOperation =null;
            // entry is from pattern: ${store_name}-${store-operation} i.e. "entity_events_store-puts"
            // we want to get the entry store operation name
            Optional optionalStoreOperation = Stream.of(KeyValueChangeLogTopicMetrics.StoreOperation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalStoreOperation != Optional.empty()) {
                storeOperation = (KeyValueChangeLogTopicMetrics.StoreOperation) optionalStoreOperation.get();
                // remove store operation from store name
                storeName = entry.getKey().replaceAll(String.format("-%s",storeOperation.value()), "");
            }

            if (storeOperation == null) {
                String errorMsg = String.format("store %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            KeyValueStoreMetricsService keyValueStoreMetricsService = keyValueStoreMetricsServices.get(storeName);

            // if there is no metric for this topic, create one
            if (keyValueStoreMetricsService == null) {
                keyValueStoreMetricsService = new KeyValueStoreMetricsService(statsService, storeName);
            }


            // we do not monitor "alls" operations
            if (KeyValueStoreMetrics.StoreOperation.ALLS.equals(storeOperation)) {
                continue;
            }

            if (KeyValueChangeLogTopicMetrics.StoreOperation.FLUSHES.equals(storeOperation)) {
                KeyValueChangeLogTopicMetrics.getKeyValueStoreMetrics().setNumberOfFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.GETS.equals(storeOperation)) {
                KeyValueChangeLogTopicMetrics.getKeyValueStoreMetrics().setNumberOfQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.ALLS.equals(storeOperation)) {
                KeyValueChangeLogTopicMetrics.getKeyValueStoreMetrics().setNumberOfFullTableScans(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.RANGES.equals(storeOperation)) {
                KeyValueChangeLogTopicMetrics.getKeyValueStoreMetrics().setNumberOfRangeQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.DELETES.equals(storeOperation)) {
                KeyValueChangeLogTopicMetrics.getKeyValueStoreMetrics().setNumberOfDeletes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.DELETE_ALLS.equals(storeOperation)) {
                KeyValueChangeLogTopicMetrics.getKeyValueStoreMetrics().setNumberOfDeleteAlls(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.BYTES_WRITTEN.equals(storeOperation)) {
                KeyValueChangeLogTopicMetrics.getKeyValueStoreMetrics().setNumberOfBytesWritten(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.BYTES_READ.equals(storeOperation)) {
                KeyValueChangeLogTopicMetrics.getKeyValueStoreMetrics().setNumberOfBytesRead(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.PUTS.equals(storeOperation)) {
                KeyValueChangeLogTopicMetrics.getKeyValueStoreMetrics().setNumberOfWrites(((Integer) entry.getValue()).longValue());
            }
            // update metric time
            KeyValueChangeLogTopicMetricsService.getKeyValueStoreMetrics().manualUpdate(metricMessage.getHeader().getTime());
            KeyValueChangeLogTopicMetricsServices.put(storeName, keyValueStoreMetricsService);
        }

    }
}
