package fortscale.monitoring.samza.converter;

import fortscale.monitoring.samza.metrics.*;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.samza.metricMessageModels.MetricMessage;

import java.util.*;
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


    }

    /**
     * rewrite kafkaSystemProducerMetrics to kafka
     *
     * @param metricMessage
     */
    protected void updateKafkaSystemProducerMetric(MetricMessage metricMessage) {
        logger.debug("Updating KafkaSystemProducerMetrics with: {}", metricMessage.toString());
      Map<String, Object> metric = metricMessage.getMetrics().getAdditionalProperties().get(KafkaSystemProducerMetrics.METRIC_NAME);
        HashSet<String> serviceKeys = new HashSet<String>();

        for (Map.Entry<String, Object> entry : metric.entrySet()) {
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
            KafkaSystemProducerMetricsService metricsService = kafkaSystemProducerMetricServices.get(serviceKey);

            // if there is no metric for this topic, create one
            if (metricsService == null) {
                metricsService = new KafkaSystemProducerMetricsService(statsService, serviceKey);
            }

            if (KafkaSystemProducerMetrics.Operation.FLUSH_MS.equals(operation)) {
                metricsService.getKafkaSystemProducerMetrics().setFlushSeconds((double) entry.getValue());
            }
            if (KafkaSystemProducerMetrics.Operation.FLUSHES.equals(operation)) {
                metricsService.getKafkaSystemProducerMetrics().setFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.FLUSH_FAILED.equals(operation)) {
                metricsService.getKafkaSystemProducerMetrics().setFlushesFailures(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.PRODUCER_RETRIES.equals(operation)) {
                metricsService.getKafkaSystemProducerMetrics().setRetries(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.PRODUCER_SENDS.equals(operation)) {
                metricsService.getKafkaSystemProducerMetrics().setMessagesSent(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.SEND_FAILED.equals(operation)) {
                metricsService.getKafkaSystemProducerMetrics().setMessagesSentFailures(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.SEND_SUCCESS.equals(operation)) {
                continue;
            }
            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
            kafkaSystemProducerMetricServices.put(serviceKey, metricsService);
        }
        for (String key : serviceKeys) {
            kafkaSystemProducerMetricServices.get(key).getKafkaSystemProducerMetrics().manualUpdate(metricMessage.getHeader().getTime() / 1000);
        }

    }

    /**
     * rewrite KeyValueStoreMetrics to KeyValueStoreMetrics
     *
     * @param metricMessage
     */

    protected void updateKeyValueStoreMetrics(MetricMessage metricMessage) {
        logger.debug("Updating KeyValueStoreMetrics with: {}", metricMessage.toString());
       Map<String, Object> metric = metricMessage.getMetrics().getAdditionalProperties().get(KeyValueStoreMetrics.METRIC_NAME);
        HashSet<String> serviceKeys = new HashSet<>();

        for (Map.Entry<String, Object> entry : metric.entrySet()) {
            String storeName = "";
            String serviceKey = null;

            KeyValueStoreMetrics.StoreOperation operation = null;
            // entry is from pattern: ${store_name}-${store-operation} i.e. "entity_events_store-puts"
            // we want to get the entry store operation name
            Optional optionalStoreOperation = Stream.of(KeyValueStoreMetrics.StoreOperation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalStoreOperation != Optional.empty()) {
                operation = (KeyValueStoreMetrics.StoreOperation) optionalStoreOperation.get();
                // remove store operation from store name
                storeName = entry.getKey().replaceAll(String.format("-%s", operation.value()), "").trim();
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
                metricsSerivce.getKeyValueStoreMetrics().setRecordsInStore(((Integer) entry.getValue()).longValue());

            }
            if (KeyValueStoreMetrics.StoreOperation.FLUSHES.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.GETS.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.GET_ALLS.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setFullTableScans(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.RANGES.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setRangeQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.DELETES.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setDeletes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.DELETE_ALLS.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setDeleteAlls(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.BYTES_WRITTEN.equals(operation)) {
                if (entry.getValue().getClass().equals(Integer.class))
                    metricsSerivce.getKeyValueStoreMetrics().setBytesWritten(((Integer) entry.getValue()).longValue());
                if (entry.getValue().getClass().equals(Long.class))
                    metricsSerivce.getKeyValueStoreMetrics().setBytesWritten(((Long) entry.getValue()).longValue());

            }
            if (KeyValueStoreMetrics.StoreOperation.BYTES_READ.equals(operation)) {
                if (entry.getValue().getClass().equals(Integer.class))
                    metricsSerivce.getKeyValueStoreMetrics().setBytesRead(((Integer) entry.getValue()).longValue());
                else
                    metricsSerivce.getKeyValueStoreMetrics().setBytesRead(((Long) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.PUTS.equals(operation)) {
                metricsSerivce.getKeyValueStoreMetrics().setWrites(((Integer) entry.getValue()).longValue());
            }
            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
            keyValueStoreMetricsServices.put(serviceKey, metricsSerivce);
        }
        for (String key : serviceKeys) {
            keyValueStoreMetricsServices.get(key).getKeyValueStoreMetrics().manualUpdate(metricMessage.getHeader().getTime() / 1000);
        }
    }

    protected void updateKafkaSystemConsumerMetrics(MetricMessage metricMessage) {
        logger.debug("Updating KafkaSystemConsumerMetrics with: {}", metricMessage.toString());
        Map<String, Object> metric = metricMessage.getMetrics().getAdditionalProperties().get(KafkaSystemConsumerMetrics.METRIC_NAME);
        HashSet<String> serviceKeys = new HashSet<>();
        for (Map.Entry<String, Object> entry : metric.entrySet()) {
            KafkaSystemConsumerMetrics.TopicOperation topicOperation = null;
            String topicName = null;
            String serviceKey = null;

            // entry can be from pattern: ${topic_name}-${topic-operation} i.e. "kafka-fortscale-aggr-feature-events-score-0-bytes-read"
            // we want to get the entry topic operation name
            Optional optianalTopicOperation = Stream.of(KafkaSystemConsumerMetrics.TopicOperation.values()).filter(x -> entry.getKey().contains(x.value())).findFirst();

            if (optianalTopicOperation != Optional.empty()) {
                topicOperation = (KafkaSystemConsumerMetrics.TopicOperation) optianalTopicOperation.get();
                // remove topic operation from topic name
                topicName = entry.getKey().replaceAll(topicOperation.value(), "").replaceAll("-0-", "").trim();
                if (topicName.startsWith("kafka-"))
                    topicName = topicName.substring("kafka-".length());

            }

            // entry can be from pattern: ${topic_status}-SystemStreamPartition-[kafka,${topic_name},${partition}] i.e. "kafka-fortscale-aggr-feature-events-score-0-bytes-read"
            Optional optinalTopicStatus = Stream.of(KafkaSystemConsumerMetrics.TopicStatus.values()).filter(x -> entry.getKey().contains(x.value())).findFirst();
            KafkaSystemConsumerMetrics.TopicStatus topicStatus = null;
            if (optinalTopicStatus != Optional.empty()) {
                topicStatus = (KafkaSystemConsumerMetrics.TopicStatus) optinalTopicStatus.get();
                // remove SystemStreamPartition and topic status from topic name
                if (!topicStatus.equals(KafkaSystemConsumerMetrics.TopicStatus.POLL_COUNT)) {
                    topicName = (entry.getKey().replaceAll(String.format("%s-SystemStreamPartition", topicStatus.value()), "")).split(",")[1].trim();
                } else {
                    continue;
                }
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
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setReconnects(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.SKIPPED_FETCH_REQUESTS.equals(topicOperation)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setSkippedFetchRequests(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.MESSAGES_READ.equals(topicOperation)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setMessagesRead(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.OFFSET_CHANGE.equals(topicOperation)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setOffsetChange(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.MESSAGES_BEHIND_HIGH_WATERMARK.equals(topicOperation)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setMessagesBehindWatermark(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.BYTES_READ.equals(topicOperation)) {
                if (entry.getValue().getClass().equals(Integer.class))
                    kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setBytesRead(((Integer) entry.getValue()).longValue());
                else
                    kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setBytesRead(((Long) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.HIGH_WATERMARK.equals(topicOperation)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setHighWaterMark(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.BLOCKING_POLL_COUNT.equals(topicStatus)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setBlockingPoll(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.NO_MORE_MESSAGES.equals(topicStatus)) {
                long noMoreMessages = 0;
                if (topicStatus.equals("true")) {
                    noMoreMessages = 1;
                }
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setNoMoreMessages(noMoreMessages);
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.BLOCKING_POLL_TIMEOUT_COUNT.equals(topicStatus)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setBlockingPollTimeout(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.BUFFERED_MESSAGE_COUNT.equals(topicStatus)) {
                kafkaSystemConsumerMetricsService.getKafkaSystemConsumerMetrics().setBufferedMessage(((Integer) entry.getValue()).longValue());
            }
            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
            kafkaSystemConsumerMetricsServices.put(serviceKey, kafkaSystemConsumerMetricsService);
        }
        for (String key : serviceKeys) {
            kafkaSystemConsumerMetricsServices.get(key).getKafkaSystemConsumerMetrics().manualUpdate(metricMessage.getHeader().getTime() / 1000);
        }
    }

    private void updateKeyValueChangeLogTopicMetrics(MetricMessage metricMessage) {
        logger.debug("Updating KeyValueChangeLogTopicMetrics with: {}", metricMessage.toString());
        Map<String, Object> metric = metricMessage.getMetrics().getAdditionalProperties().get(KeyValueChangeLogTopicMetrics.METRIC_NAME);
        String jobName = metricMessage.getHeader().getJobName();
        HashSet<String> serviceKeys = new HashSet<>();

        for (Map.Entry<String, Object> entry : metric.entrySet()) {
            String storeName = "";

            KeyValueChangeLogTopicMetrics.StoreOperation operation = null;

            // entry is from pattern: ${store_name}-${store-operation} i.e. "entity_events_store-puts"
            // we want to get the entry store operation name
            Optional optionalStoreOperation = Stream.of(KeyValueChangeLogTopicMetrics.StoreOperation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalStoreOperation != Optional.empty()) {
                operation = (KeyValueChangeLogTopicMetrics.StoreOperation) optionalStoreOperation.get();
                // remove store operation from store name
                storeName = entry.getKey().replaceAll(String.format("-%s", operation.value()), "").trim();
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
                metricsService.getKeyValueChangeLogTopicMetrics().setFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.GETS.equals(operation)) {
                metricsService.getKeyValueChangeLogTopicMetrics().setQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.ALLS.equals(operation)) {
                metricsService.getKeyValueChangeLogTopicMetrics().setRecordsInStore(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.RANGES.equals(operation)) {
                metricsService.getKeyValueChangeLogTopicMetrics().setRangeQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.DELETES.equals(operation)) {
                metricsService.getKeyValueChangeLogTopicMetrics().setDeletes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueChangeLogTopicMetrics.StoreOperation.PUTS.equals(operation)) {
                metricsService.getKeyValueChangeLogTopicMetrics().setWrites(((Integer) entry.getValue()).longValue());
            }
            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
            keyValueChangeLogTopicMetricsServices.put(serviceKey, metricsService);
        }
        for (String key : serviceKeys) {
            // update metric time
            keyValueChangeLogTopicMetricsServices.get(key).getKeyValueChangeLogTopicMetrics().manualUpdate(metricMessage.getHeader().getTime() / 1000);
        }
    }

    private void updateKeyValueStorageMetrics(MetricMessage metricMessage) {
        logger.debug("Updating KeyValueStorageMetrics with: {}", metricMessage.toString());
        Map<String, Object> metric = metricMessage.getMetrics().getAdditionalProperties().get(KeyValueStorageMetrics.METRIC_NAME);
        String jobName = metricMessage.getHeader().getJobName();
        HashSet<String> serviceKeys = new HashSet<>();

        for (Map.Entry<String, Object> entry : metric.entrySet()) {
            String storeName = "";

            KeyValueStorageMetrics.StoreOperation operation = null;

            // entry is from pattern: ${store_name}-${store-operation} i.e. "entity_events_store-puts"
            // we want to get the entry store operation name
            Optional optionalStoreOperation = Stream.of(KeyValueStorageMetrics.StoreOperation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalStoreOperation != Optional.empty()) {
                operation = (KeyValueStorageMetrics.StoreOperation) optionalStoreOperation.get();
                // remove store operation from store name
                storeName = entry.getKey().replaceAll(String.format("-%s", operation.value()), "").trim();
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
                metricsService.getKeyValueStorageMetrics().setFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.GETS.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.ALLS.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setRecordsInStore(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.RANGES.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setRangeQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.DELETES.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setDeletes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.PUTS.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setWrites(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.MESSAGES_RESTORED.equals(operation)) {
                metricsService.getKeyValueStorageMetrics().setMessagesRestored(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.RESTORED_BYTES.equals(operation)) {
                if (entry.getValue().getClass().equals(Integer.class))
                    metricsService.getKeyValueStorageMetrics().setRestoredBytes(((Integer) entry.getValue()).longValue());
                else
                    metricsService.getKeyValueStorageMetrics().setRestoredBytes(((Long) entry.getValue()).longValue());
            }
            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
            keyValueStorageMetricsServices.put(serviceKey, metricsService);

        }
        for (String key : serviceKeys) {
            // update metric time
            keyValueStorageMetricsServices.get(key).getKeyValueStorageMetrics().manualUpdate(metricMessage.getHeader().getTime() / 1000);
        }

    }

    private void updatSamzaContainerMetrics(MetricMessage metricMessage) {
        logger.debug("Updating SamzaContainerMetrics with: {}", metricMessage.toString());
        Map<String, Object> metric = metricMessage.getMetrics().getAdditionalProperties().get(SamzaContainerMetrics.METRIC_NAME);
        String jobName = metricMessage.getHeader().getJobName();
        HashSet<String> serviceKeys = new HashSet<>();

        for (Map.Entry<String, Object> entry : metric.entrySet()) {
            String serviceKey = null;
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
            serviceKey = jobName;
            SamzaContainerMetricsService metricsService = samzaContainerMetricsServices.get(serviceKey);

            // if there is no metric for this topic, create one
            if (metricsService == null) {
                metricsService = new SamzaContainerMetricsService(statsService, jobName);
            }

            if (SamzaContainerMetrics.JobContainerOperation.COMMITS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setCommitCalls(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.WINDOWS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setWindowCalls(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.PROCESSES.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setProcessCalls(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.SENDS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setSendCalls(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.ENVELOPES.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setProcessEnvelopes(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.NULL_ENVELOPES.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setProcessNullEnvelopes(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.CHOOSE_MS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setChooseSeconds(((double) entry.getValue()));
            }
            if (SamzaContainerMetrics.JobContainerOperation.WINDOW_MS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setWindowSeconds(((double) entry.getValue()));
            }
            if (SamzaContainerMetrics.JobContainerOperation.PROCESS_MS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setProcessSeconds(((double) entry.getValue()));
            }
            if (SamzaContainerMetrics.JobContainerOperation.COMMIT_MS.equals(operation)) {
                metricsService.getSamzaContainerMetrics().setCommitSeconds(((double) entry.getValue()));
            }
            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
            samzaContainerMetricsServices.put(serviceKey, metricsService);
        }
        for (String key : serviceKeys) {
            // update metric time
            samzaContainerMetricsServices.get(key).getSamzaContainerMetrics().manualUpdate(metricMessage.getHeader().getTime() / 1000);
        }
    }

    private void updatTaskInstanceMetrics(MetricMessage metricMessage) {
        logger.debug("Updating SamzaContainerMetrics with: {}", metricMessage.toString());
        Map<String, Object> metric = metricMessage.getMetrics().getAdditionalProperties().get(TaskInstanceMetrics.METRIC_NAME);
        String jobName = metricMessage.getHeader().getJobName();
        HashSet<String> serviceKeys = new HashSet<>();

        for (Map.Entry<String, Object> entry : metric.entrySet()) {

            TaskInstanceMetrics.TaskOperation operation = null;
            String topicName = null;
            String serviceKey = "";
            Optional optionalOperation = Stream.of(TaskInstanceMetrics.TaskOperation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalOperation != Optional.empty()) {
                operation = (TaskInstanceMetrics.TaskOperation) optionalOperation.get();
                serviceKey = jobName;
            } else {
                topicName = entry.getKey().replaceAll("-0-offset", "").trim();
                if (topicName.startsWith("kafka-"))
                    topicName = topicName.substring("kafka-".length());
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


            } else {
                if (operation.equals(TaskInstanceMetrics.TaskOperation.COMMITS)) {
                    metricsService.getTaskInstanceMetrics().setCommitsCalls(((Integer) entry.getValue()).longValue());
                } else if (operation.equals(TaskInstanceMetrics.TaskOperation.WINDOWS)) {
                    metricsService.getTaskInstanceMetrics().setWindowCalls(((Integer) entry.getValue()).longValue());
                } else if (operation.equals(TaskInstanceMetrics.TaskOperation.PROCESSES)) {
                    metricsService.getTaskInstanceMetrics().setProcessCalls(((Integer) entry.getValue()).longValue());
                } else if (operation.equals(TaskInstanceMetrics.TaskOperation.SENDS)) {
                    metricsService.getTaskInstanceMetrics().setSendCalls(((Integer) entry.getValue()).longValue());
                } else if (operation.equals(TaskInstanceMetrics.TaskOperation.FLUSH_CALLS)) {
                    metricsService.getTaskInstanceMetrics().setFlushCalls(((Integer) entry.getValue()).longValue());
                } else if (operation.equals(TaskInstanceMetrics.TaskOperation.MESSAGES_SENT)) {
                    metricsService.getTaskInstanceMetrics().setMessagesSent(((Integer) entry.getValue()).longValue());
                } else {
                    logger.error("hello {}", operation.value());
                }

            }
            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
            taskInstanceMetricsServices.put(serviceKey, metricsService);
        }
        for (String key : serviceKeys) {
            // update metric time
            if (taskInstanceMetricsServices.get(key).getTaskInstanceOffsetsMetrics() != null)
                taskInstanceMetricsServices.get(key).getTaskInstanceOffsetsMetrics().manualUpdate(metricMessage.getHeader().getTime() / 1000);
            if (taskInstanceMetricsServices.get(key).getTaskInstanceMetrics() != null)
                taskInstanceMetricsServices.get(key).getTaskInstanceMetrics().manualUpdate(metricMessage.getHeader().getTime() / 1000);
        }
    }
}
