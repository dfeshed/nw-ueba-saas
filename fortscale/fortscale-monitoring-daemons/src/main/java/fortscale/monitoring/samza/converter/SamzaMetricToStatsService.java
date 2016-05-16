package fortscale.monitoring.samza.converter;

import fortscale.monitoring.samza.metrics.*;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.samza.metricMessageModels.MetricMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by cloudera on 5/10/16.
 */
public class SamzaMetricToStatsService {
    private static final Logger logger = Logger.getLogger(SamzaMetricToStatsService.class);
    private Map<String, KafkaSystemProducerMetricsService> kafkaSystemProducerMetricServices;
    private Map<String, KeyValueStoreMetricsService> keyValueStoreMetricsServices;
    private Map<String, KafkaSystemConsumerMetricsService> kafkaSystemConsumerMetricsServices;
    private Map<String, KeyValueChangeLogTopicMetricsService> keyValueChangeLogTopicMetricsServices;
    private Map<String, KeyValueStorageMetricsService> keyValueStorageMetricsServices;
    private Map<String, SamzaContainerMetricsService> samzaContainerMetricsServices;
    private Map<String, TaskInstanceMetricsService> taskInstanceMetricsServices;

    private StatsService statsService;

    public SamzaMetricToStatsService(StatsService statsService) {
        kafkaSystemProducerMetricServices = new HashMap<>();
        keyValueStoreMetricsServices = new HashMap<>();
        kafkaSystemConsumerMetricsServices = new HashMap<>();
        keyValueChangeLogTopicMetricsServices = new HashMap<>();
        keyValueStorageMetricsServices = new HashMap<>();
        samzaContainerMetricsServices = new HashMap<>();
        taskInstanceMetricsServices = new HashMap<>();
        this.statsService = statsService;
    }
    /**
     * rewrite samza metrics to kafka metrics topic as a tagged EngineData object
     *
     * @param metricMessage
     */
    public void handleSamzaMetric(MetricMessage metricMessage) {
        Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();

        if (metric.get(KafkaSystemProducerMetrics.METRIC_NAME) != null) {
            updateKafkaSystemProducerMetric(metricMessage);
            statsService.ManualUpdatePush();

        }
        if (metric.get(KeyValueStoreMetrics.METRIC_NAME) != null) {
            updateKeyValueStoreMetrics(metricMessage);
            statsService.ManualUpdatePush();

        }
        if (metric.get(KafkaSystemConsumerMetrics.METRIC_NAME) != null) {
            updateKafkaSystemConsumerMetrics(metricMessage);
            statsService.ManualUpdatePush();

        }
        if (metric.get(KeyValueChangeLogTopicMetrics.METRIC_NAME) != null) {
            updateKeyValueChangeLogTopicMetrics(metricMessage);
            statsService.ManualUpdatePush();

        }
        if (metric.get(KeyValueStorageMetrics.METRIC_NAME) != null) {
            updateKeyValueStorageMetrics(metricMessage);
            statsService.ManualUpdatePush();

        }
        if (metric.get(SamzaContainerMetrics.METRIC_NAME) != null) {
            updatSamzaContainerMetrics(metricMessage);
            statsService.ManualUpdatePush();

        }
        if (metric.get(TaskInstanceMetrics.METRIC_NAME) != null) {
            updatTaskInstanceMetrics(metricMessage);
            statsService.ManualUpdatePush();

        }

        statsService.ManualUpdatePush();

    }

    /**
     * rewrite kafkaSystemProducerMetrics to kafka
     *
     * @param metricMessage
     */
    protected void updateKafkaSystemProducerMetric(MetricMessage metricMessage) {
        logger.debug("Updating KafkaSystemProducerMetrics with: {}", metricMessage.toString());
        Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();
        for (Map.Entry<String, Object> entry : metric.get(KafkaSystemProducerMetrics.METRIC_NAME).entrySet()) {
            String serviceKey = null;

            KafkaSystemProducerMetrics.Operation operation = null;

            Optional optionalOperation = Stream.of(KafkaSystemProducerMetrics.Operation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalOperation != Optional.empty()) {
                operation = (KafkaSystemProducerMetrics.Operation) optionalOperation.get();
            }

            if (operation == null) {
                String errorMsg = String.format("store %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            serviceKey = metricMessage.getHeader().getJobName();
            KafkaSystemProducerMetricsService metricsSerivce = kafkaSystemProducerMetricServices.get(serviceKey);

            // if there is no metric for this topic, create one
            if (metricsSerivce == null) {
                metricsSerivce = new KafkaSystemProducerMetricsService(statsService, serviceKey);
            }

            if (KafkaSystemProducerMetrics.Operation.FLUSH_MS.equals(operation)) {
                metricsSerivce.getKafkaSystemProducerMetrics().setFlushMillis((double) entry.getValue());
            }
            if (KafkaSystemProducerMetrics.Operation.FLUSHES.equals(operation)) {
                metricsSerivce.getKafkaSystemProducerMetrics().setNumberOfFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.FLUSH_FAILED.equals(operation)) {
                metricsSerivce.getKafkaSystemProducerMetrics().setNumberOfFlushFailed(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.PRODUCER_RETRIES.equals(operation)) {
                metricsSerivce.getKafkaSystemProducerMetrics().setNumberOfProducerRetries(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.PRODUCER_SENDS.equals(operation)) {
                metricsSerivce.getKafkaSystemProducerMetrics().setNumberOfProducerSends(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.SEND_FAILED.equals(operation)) {
                metricsSerivce.getKafkaSystemProducerMetrics().setNumberOfSendFailed(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.SEND_SUCCESS.equals(operation)) {
                metricsSerivce.getKafkaSystemProducerMetrics().setNumberOfSendSuccess(((Integer) entry.getValue()).longValue());
            }

            // update metric time
            metricsSerivce.getKafkaSystemProducerMetrics().manualUpdate(metricMessage.getHeader().getTime()/1000);
            kafkaSystemProducerMetricServices.put(serviceKey, metricsSerivce);
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
        for (Map.Entry<String, Object> entry : metric.get(KeyValueStoreMetrics.METRIC_NAME).entrySet()) {
            String storeName = "";
            String serviceKey = null;

            KeyValueStoreMetrics.StoreOperation operation = null;
            // entry is from pattern: ${store_name}-${store-operation} i.e. "entity_events_store-puts"
            // we want to get the entry store operation name
            Optional optionalStoreOperation = Stream.of(KeyValueStoreMetrics.StoreOperation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalStoreOperation != Optional.empty()) {
                operation = (KeyValueStoreMetrics.StoreOperation) optionalStoreOperation.get();
                // remove store operation from store name
                storeName = entry.getKey().replaceAll(String.format("-%s", operation.value()), "");
            }

            if (operation == null) {
                String errorMsg = String.format("store %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            serviceKey = String.format("%s%s", storeName, metricMessage.getHeader().getJobName());
            KeyValueStoreMetricsService metricsSerivce = keyValueStoreMetricsServices.get(serviceKey);

            // if there is no metric for this topic, create one
            if (metricsSerivce == null) {
                metricsSerivce = new KeyValueStoreMetricsService(statsService, metricMessage.getHeader().getJobName(), storeName);
            }


            if (KeyValueStoreMetrics.StoreOperation.ALLS.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setNumberOfRecordsInStore(((Integer) entry.getValue()).longValue());

            }
            if (KeyValueStoreMetrics.StoreOperation.FLUSHES.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setNumberOfFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.GETS.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setNumberOfQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.GET_ALLS.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setNumberOfFullTableScans(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.RANGES.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setNumberOfRangeQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.DELETES.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setNumberOfDeletes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.DELETE_ALLS.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setNumberOfDeleteAlls(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.BYTES_WRITTEN.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setNumberOfBytesWritten(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.BYTES_READ.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setNumberOfBytesRead(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.PUTS.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setNumberOfWrites(((Integer) entry.getValue()).longValue());
            }
            // update metric time
            metricsSerivce.getKeyValueStoreMetrics().manualUpdate(metricMessage.getHeader().getTime()/1000);
            keyValueStoreMetricsServices.put(serviceKey, metricsSerivce);
        }
    }

    protected void updateKafkaSystemConsumerMetrics(MetricMessage metricMessage) {
        logger.debug("Updating KafkaSystemConsumerMetrics with: {}", metricMessage.toString());
        Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();
        for (Map.Entry<String, Object> entry : metric.get(KafkaSystemConsumerMetrics.METRIC_NAME).entrySet()) {
            KafkaSystemConsumerMetrics.TopicOperation topicOperation = null;
            String topicName = "";
            String serviceKey = null;

            // entry can be from pattern: ${topic_name}-${topic-operation} i.e. "kafka-fortscale-aggr-feature-events-score-0-bytes-read"
            // we want to get the entry topic operation name
            Optional optianalTopicOperation = Stream.of(KafkaSystemConsumerMetrics.TopicOperation.values()).filter(x -> entry.getKey().contains(x.value())).findFirst();

            if (optianalTopicOperation != Optional.empty()) {
                topicOperation = (KafkaSystemConsumerMetrics.TopicOperation) optianalTopicOperation.get();
                // remove topic operation from topic name
                topicName = entry.getKey().replaceAll(topicOperation.value(), "");
                // remove topic partition from topic name
                topicName = entry.getKey().substring(0, topicName.length() - 3);
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
                String errorMsg = String.format("topic %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            serviceKey = String.format("%s%s", metricMessage.getHeader().getJobName(), topicName);
            KafkaSystemConsumerMetricsService kafkaSystemConsumerMetricsService = kafkaSystemConsumerMetricsServices.get(serviceKey);

            // if there is no metric for this topic, create one
            if (kafkaSystemConsumerMetricsService == null) {
                kafkaSystemConsumerMetricsService = new KafkaSystemConsumerMetricsService(statsService, metricMessage.getHeader().getJobName(), topicName);
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
            if (KafkaSystemConsumerMetrics.TopicOperation.HIGH_WATERMARK.equals(topicOperation)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setNumberOfHighWaterMark(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.BLOCKING_POLL_COUNT.equals(topicStatus)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setBlockingPollCount(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.NO_MORE_MESSAGES.equals(topicStatus)) {
                long noMoreMessages = 0;
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
            kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().manualUpdate(metricMessage.getHeader().getTime()/1000);
            kafkaSystemConsumerMetricsServices.put(serviceKey, kafkaSystemConsumerMetricsService);
        }
    }

    private void updateKeyValueChangeLogTopicMetrics(MetricMessage metricMessage) {
        logger.debug("Updating KeyValueChangeLogTopicMetrics with: {}", metricMessage.toString());
        Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();
        String jobName = metricMessage.getHeader().getJobName();
        for (Map.Entry<String, Object> entry : metric.get(KeyValueChangeLogTopicMetrics.METRIC_NAME).entrySet()) {
            String storeName = "";

            KeyValueChangeLogTopicMetrics.StoreOperation operation = null;

            // entry is from pattern: ${store_name}-${store-operation} i.e. "entity_events_store-puts"
            // we want to get the entry store operation name
            Optional optionalStoreOperation = Stream.of(KeyValueChangeLogTopicMetrics.StoreOperation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalStoreOperation != Optional.empty()) {
                operation = (KeyValueChangeLogTopicMetrics.StoreOperation) optionalStoreOperation.get();
                // remove store operation from store name
                storeName = entry.getKey().replaceAll(String.format("-%s", operation.value()), "");
            }

            if (operation == null) {
                String errorMsg = String.format("store %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            String serviceKey = String.format("%s%s", jobName, storeName);
            KeyValueChangeLogTopicMetricsService metricsService = keyValueChangeLogTopicMetricsServices.get(serviceKey);

            // if there is no metric for this topic, create one
            if (metricsService == null) {
                metricsService = new KeyValueChangeLogTopicMetricsService(statsService, storeName, jobName);
            }

            if (KeyValueChangeLogTopicMetrics.StoreOperation.FLUSHES.equals(operation)) {
                metricsService.getKeyValueChangeLogTopicMetrics().setNumberOfFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.GETS.equals(operation)) {
                metricsService.getKeyValueChangeLogTopicMetrics().setNumberOfQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.ALLS.equals(operation)) {
                metricsService.getKeyValueChangeLogTopicMetrics().setNumberOfRecordsInStore(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.RANGES.equals(operation)) {
                metricsService.getKeyValueChangeLogTopicMetrics().setNumberOfRangeQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.DELETES.equals(operation)) {
                metricsService.getKeyValueChangeLogTopicMetrics().setNumberOfDeletes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.PUTS.equals(operation)) {
                metricsService.getKeyValueChangeLogTopicMetrics().setNumberOfWrites(((Integer) entry.getValue()).longValue());
            }
            // update metric time
            metricsService.getKeyValueChangeLogTopicMetrics().manualUpdate(metricMessage.getHeader().getTime()/1000);
            keyValueChangeLogTopicMetricsServices.put(serviceKey, metricsService);
        }
    }


    private void updateKeyValueStorageMetrics(MetricMessage metricMessage) {
        logger.debug("Updating KeyValueStorageMetrics with: {}", metricMessage.toString());
        Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();
        String jobName = metricMessage.getHeader().getJobName();
        for (Map.Entry<String, Object> entry : metric.get(KeyValueStorageMetrics.METRIC_NAME).entrySet()) {
            String storeName = "";

            KeyValueStorageMetrics.StoreOperation operation = null;

            // entry is from pattern: ${store_name}-${store-operation} i.e. "entity_events_store-puts"
            // we want to get the entry store operation name
            Optional optionalStoreOperation = Stream.of(KeyValueStorageMetrics.StoreOperation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalStoreOperation != Optional.empty()) {
                operation = (KeyValueStorageMetrics.StoreOperation) optionalStoreOperation.get();
                // remove store operation from store name
                storeName = entry.getKey().replaceAll(String.format("-%s", operation.value()), "");
            }

            if (operation == null) {
                String errorMsg = String.format("store %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            String serviceKey = String.format("%s%s", jobName, storeName);
            KeyValueStorageMetricsService metricsService = keyValueStorageMetricsServices.get(serviceKey);

            // if there is no metric for this topic, create one
            if (metricsService == null) {
                metricsService = new KeyValueStorageMetricsService(statsService, storeName, jobName);
            }

            if (KeyValueStorageMetrics.StoreOperation.FLUSHES.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setNumberOfFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.GETS.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setNumberOfQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.ALLS.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setNumberOfRecordsInStore(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.RANGES.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setNumberOfRangeQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.DELETES.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setNumberOfDeletes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.PUTS.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setNumberOfWrites(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.MESSAGES_RESTORED.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setNumberOfMessagesRestored(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.RESTORED_BYTES.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setNumberOfRestoredBytes(((Integer) entry.getValue()).longValue());
            }
            // update metric time
            metricsService.getKeyValueStorageMetrics().manualUpdate(metricMessage.getHeader().getTime()/1000);
            keyValueStorageMetricsServices.put(serviceKey, metricsService);
        }
    }

    private void updatSamzaContainerMetrics(MetricMessage metricMessage) {
        logger.debug("Updating SamzaContainerMetrics with: {}", metricMessage.toString());
        Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();
        String jobName = metricMessage.getHeader().getJobName();
        for (Map.Entry<String, Object> entry : metric.get(SamzaContainerMetrics.METRIC_NAME).entrySet()) {

            SamzaContainerMetrics.JobContainerOperation operation = null;

            Optional optionalOperation = Stream.of(SamzaContainerMetrics.JobContainerOperation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalOperation != Optional.empty()) {
                operation = (SamzaContainerMetrics.JobContainerOperation) optionalOperation.get();
            }

            if (operation == null) {
                String errorMsg = String.format("task %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            SamzaContainerMetricsService metricsService = samzaContainerMetricsServices.get(jobName);

            // if there is no metric for this topic, create one
            if (metricsService == null) {
                metricsService = new SamzaContainerMetricsService(statsService, jobName);
            }

            if (SamzaContainerMetrics.JobContainerOperation.COMMITS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setNumberOfCommitCalls(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.WINDOWS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setNumberOfWindowCalls(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.PROCESSES.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setNumberOfProcessCalls(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.SENDS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setNumberOfSendCalls(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.ENVELOPES.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setNumberOfProcessEnvelopes(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.NULL_ENVELOPES.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setNumberOfProcessNullEnvelopes(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.CHOOSE_MS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setNumberOfChooseMillis(((double) entry.getValue()));
            }
            if (SamzaContainerMetrics.JobContainerOperation.WINDOW_MS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setNumberOfWindowMillis(((double) entry.getValue()));
            }
            if (SamzaContainerMetrics.JobContainerOperation.PROCESS_MS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setNumberOfProcessMillis(((double) entry.getValue()));
            }
            if (SamzaContainerMetrics.JobContainerOperation.COMMIT_MS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setNumberOfCommitMillis(((double) entry.getValue()));
            }
            // update metric time
            metricsService.getSamzaContainerMetrics().manualUpdate(metricMessage.getHeader().getTime()/1000);
            samzaContainerMetricsServices.put(jobName, metricsService);
        }
    }

    private void updatTaskInstanceMetrics(MetricMessage metricMessage) {
        logger.debug("Updating SamzaContainerMetrics with: {}", metricMessage.toString());
        Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();
        String jobName = metricMessage.getHeader().getJobName();
        for (Map.Entry<String, Object> entry : metric.get(TaskInstanceMetrics.METRIC_NAME).entrySet()) {

            TaskInstanceMetrics.TaskOperation operation = null;
            String topicName = null;
            String serviceKey = "";
            Optional optionalOperation = Stream.of(TaskInstanceMetrics.TaskOperation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalOperation != Optional.empty()) {
                operation = (TaskInstanceMetrics.TaskOperation) optionalOperation.get();
                serviceKey = jobName;
            } else {
                topicName = entry.getKey().replaceAll("-0-offset", "");
                serviceKey = String.format("%s%s", jobName, topicName);
            }

            if (operation == null && topicName == null) {
                String errorMsg = String.format("task %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            TaskInstanceMetricsService metricsService = taskInstanceMetricsServices.get(serviceKey);

            // if there is no metric for this topic, create one
            if (metricsService == null) {
                if (operation != null)
                    metricsService = new TaskInstanceMetricsService(statsService, jobName);
                else
                    metricsService = new TaskInstanceMetricsService(statsService, jobName, topicName);
            }
            if (entry.getKey().contains("offset")) {
                if (entry.getValue() == null)//oddly, entry value can contain null as string and as value
                    continue;
                if (entry.getValue().equals("null"))
                    continue;

                metricsService.getTaskInstanceOffsetsMetrics().setTopicOffset(Long.parseLong((String) entry.getValue()));
                // update metric time
                metricsService.getTaskInstanceOffsetsMetrics().manualUpdate(metricMessage.getHeader().getTime()/1000);

            } else {
                if (TaskInstanceMetrics.TaskOperation.COMMITS.equals(operation)) {
                    metricsService.getTaskInstanceMetrics().setNumberOfCommitCalls(((Integer) entry.getValue()).longValue());
                }
                if (TaskInstanceMetrics.TaskOperation.WINDOWS.equals(operation)) {
                    metricsService.getTaskInstanceMetrics().setNumberOfWindowCalls(((Integer) entry.getValue()).longValue());
                }
                if (TaskInstanceMetrics.TaskOperation.PROCESSES.equals(operation)) {
                    metricsService.getTaskInstanceMetrics().setNumberOfProcessCalls(((Integer) entry.getValue()).longValue());
                }
                if (TaskInstanceMetrics.TaskOperation.SENDS.equals(operation)) {
                    metricsService.getTaskInstanceMetrics().setNumberOfSendCalls(((Integer) entry.getValue()).longValue());
                }
                if (TaskInstanceMetrics.TaskOperation.FLUSH_CALLS.equals(operation)) {
                    metricsService.getTaskInstanceMetrics().setNumberOfFlushCalls(((Integer) entry.getValue()).longValue());
                }
                if (TaskInstanceMetrics.TaskOperation.MESSAGES_SENT.equals(operation)) {
                    metricsService.getTaskInstanceMetrics().setNumberOfMessagesSent(((Integer) entry.getValue()).longValue());
                }
                // update metric time
                metricsService.getTaskInstanceMetrics().manualUpdate(metricMessage.getHeader().getTime()/1000);
            }
            taskInstanceMetricsServices.put(serviceKey, metricsService);

        }
    }
}
