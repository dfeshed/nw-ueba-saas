package fortscale.monitoring.external.stats.samza.collector.service.impl;

import fortscale.monitoring.external.stats.samza.collector.samzaMetrics.*;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.samza.metricMessageModels.MetricMessage;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by cloudera on 5/10/16.
 */
public class SamzaMetricToStatsServiceConverter {
    private static final Logger logger = Logger.getLogger(SamzaMetricToStatsServiceConverter.class);
    private Map<String, KafkaSystemProducerMetricsService> kafkaSystemProducerMetricServices;
    private Map<String, KeyValueStoreMetricsService> keyValueStoreMetricsServices;
    private Map<String, KafkaSystemConsumerMetricsService> kafkaSystemConsumerMetricsServices;
    private Map<String, KeyValueChangeLogTopicMetricsService> keyValueChangeLogTopicMetricsServices;
    private Map<String, KeyValueStorageMetricsService> keyValueStorageMetricsServices;
    private Map<String, SamzaContainerMetricsService> samzaContainerMetricsServices;
    private Map<String, TaskInstanceMetricsService> taskInstanceMetricsServices;
    List<String> topicOperations;
    List<String> storeOperations;

    private StatsService statsService;

    public SamzaMetricToStatsServiceConverter(StatsService statsService) {
        kafkaSystemProducerMetricServices = new HashMap<>();
        keyValueStoreMetricsServices = new HashMap<>();
        kafkaSystemConsumerMetricsServices = new HashMap<>();
        keyValueChangeLogTopicMetricsServices = new HashMap<>();
        keyValueStorageMetricsServices = new HashMap<>();
        samzaContainerMetricsServices = new HashMap<>();
        taskInstanceMetricsServices = new HashMap<>();
        this.statsService = statsService;
        topicOperations = new ArrayList<>();
        updateTopicOperations();
        storeOperations = new ArrayList<>();
        updateStoreOperations();


    }

    /**
     * get store operations values from metrics enums and update storeOperations list
     */
    private void updateStoreOperations() {
        Arrays.asList(KeyValueStoreMetrics.StoreOperation.values()).stream().forEach(operation -> storeOperations.add(operation.value()));
        Arrays.asList(KeyValueChangeLogTopicMetrics.StoreOperation.values()).stream().forEach(operation -> storeOperations.add(operation.value()));
        Arrays.asList(KeyValueStorageMetrics.StoreOperation.values()).stream().forEach(operation -> storeOperations.add(operation.value()));

    }

    /**
     * get topic operations values from metrics enums and update topicOperations list
     */
    private void updateTopicOperations() {
        Arrays.asList(KafkaSystemConsumerMetrics.TopicOperation.values()).stream().forEach(operation -> topicOperations.add(operation.value()));
        Arrays.asList(KafkaSystemConsumerMetrics.TopicStatus.values()).stream().forEach(status -> topicOperations.add(status.value()));
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
     * gets topic name - cleans unnecessary strings
     *
     * @param rawTopicName
     * @return
     */
    protected String getTopicName(String rawTopicName) {
        String topicName = rawTopicName;

        if (topicName.startsWith("kafka-")) {
            topicName = topicName.substring("kafka-".length());
        }
        if (topicName.contains("-offset")) {
            topicName = topicName.replaceAll("-offset", "");
        }
        if (topicName.contains("-0"))
            topicName = topicName.replaceAll("-0", "");
        if (topicName.contains("-SystemStreamPartition")) {
            topicName = topicName.replaceAll("-SystemStreamPartition", "").split(",")[1];
        }
        for (String topicOperation : topicOperations) {
            String updatedTopicOperation = String.format("-%s", topicOperation);
            if (topicName.contains(updatedTopicOperation)) {
                topicName = topicName.replaceAll(String.format("-%s", updatedTopicOperation), "");
            }
        }

        topicName = topicName.trim();
        return topicName;
    }

    /**
     * gets store name - cleans unnecessery strings
     *
     * @param rawStoreName
     * @return
     */
    protected String getStoreName(String rawStoreName) {
        String storeName = rawStoreName;

        for (String operation : storeOperations) {
            String updatedStoreOperation = String.format("-%s", operation);
            if (storeName.contains(updatedStoreOperation)) {
                storeName = storeName.replaceAll(String.format("-%s", updatedStoreOperation), "");
            }
        }

        storeName = storeName.trim();
        return storeName;
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

            if (KafkaSystemProducerMetrics.Operation.SEND_SUCCESS.equals(operation)) {
                continue;
            }

            // if there is no metric for this topic, create one
            if (kafkaSystemProducerMetricServices.get(serviceKey) == null) {
                KafkaSystemProducerMetricsService metricsService = new KafkaSystemProducerMetricsService(statsService, serviceKey);
                kafkaSystemProducerMetricServices.put(serviceKey, metricsService);
            }

            if (KafkaSystemProducerMetrics.Operation.FLUSH_MS.equals(operation)) {
                kafkaSystemProducerMetricServices.get(serviceKey).getKafkaSystemProducerMetrics().setFlushSeconds((double) entry.getValue());
            }
            if (KafkaSystemProducerMetrics.Operation.FLUSHES.equals(operation)) {
                kafkaSystemProducerMetricServices.get(serviceKey).getKafkaSystemProducerMetrics().setFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.FLUSH_FAILED.equals(operation)) {
                kafkaSystemProducerMetricServices.get(serviceKey).getKafkaSystemProducerMetrics().setFlushesFailures(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.PRODUCER_RETRIES.equals(operation)) {
                kafkaSystemProducerMetricServices.get(serviceKey).getKafkaSystemProducerMetrics().setRetries(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.PRODUCER_SENDS.equals(operation)) {
                kafkaSystemProducerMetricServices.get(serviceKey).getKafkaSystemProducerMetrics().setMessagesSent(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemProducerMetrics.Operation.SEND_FAILED.equals(operation)) {
                kafkaSystemProducerMetricServices.get(serviceKey).getKafkaSystemProducerMetrics().setMessagesSentFailures(((Integer) entry.getValue()).longValue());
            }

            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
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
                storeName = getStoreName(entry.getKey());
            }

            if (operation == null) {
                String errorMsg = String.format("store %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            serviceKey = String.format("%s__%s", storeName, metricMessage.getHeader().getJobName());


            // if there is no metric for this topic, create one
            if (keyValueStoreMetricsServices.get(serviceKey) == null) {
                KeyValueStoreMetricsService metricsSerivce = new KeyValueStoreMetricsService(statsService, metricMessage.getHeader().getJobName(), storeName);
                keyValueStoreMetricsServices.put(serviceKey, metricsSerivce);
            }


            if (KeyValueStoreMetrics.StoreOperation.ALLS.equals(operation)) {
                keyValueStoreMetricsServices.get(serviceKey).getKeyValueStoreMetrics().setRecordsInStore(((Integer) entry.getValue()).longValue());

            }
            if (KeyValueStoreMetrics.StoreOperation.FLUSHES.equals(operation)) {
                keyValueStoreMetricsServices.get(serviceKey).getKeyValueStoreMetrics().setFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.GETS.equals(operation)) {
                keyValueStoreMetricsServices.get(serviceKey).getKeyValueStoreMetrics().setQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.GET_ALLS.equals(operation)) {
                keyValueStoreMetricsServices.get(serviceKey).getKeyValueStoreMetrics().setFullTableScans(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.RANGES.equals(operation)) {
                keyValueStoreMetricsServices.get(serviceKey).getKeyValueStoreMetrics().setRangeQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.DELETES.equals(operation)) {
                keyValueStoreMetricsServices.get(serviceKey).getKeyValueStoreMetrics().setDeletes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.DELETE_ALLS.equals(operation)) {
                keyValueStoreMetricsServices.get(serviceKey).getKeyValueStoreMetrics().setDeleteAlls(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.BYTES_WRITTEN.equals(operation)) {
                if (entry.getValue().getClass().equals(Integer.class))
                    keyValueStoreMetricsServices.get(serviceKey).getKeyValueStoreMetrics().setBytesWritten(((Integer) entry.getValue()).longValue());
                if (entry.getValue().getClass().equals(Long.class))
                    keyValueStoreMetricsServices.get(serviceKey).getKeyValueStoreMetrics().setBytesWritten(((Long) entry.getValue()).longValue());

            }
            if (KeyValueStoreMetrics.StoreOperation.BYTES_READ.equals(operation)) {
                if (entry.getValue().getClass().equals(Integer.class))
                    keyValueStoreMetricsServices.get(serviceKey).getKeyValueStoreMetrics().setBytesRead(((Integer) entry.getValue()).longValue());
                else
                    keyValueStoreMetricsServices.get(serviceKey).getKeyValueStoreMetrics().setBytesRead(((Long) entry.getValue()).longValue());
            }
            if (KeyValueStoreMetrics.StoreOperation.PUTS.equals(operation)) {
                keyValueStoreMetricsServices.get(serviceKey).getKeyValueStoreMetrics().setWrites(((Integer) entry.getValue()).longValue());
            }
            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
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
            }

            topicName = getTopicName(entry.getKey());
            if (topicName.contains(metricMessage.getHeader().getHost())) {
                continue;
            }
            // entry can be from pattern: ${topic_status}-SystemStreamPartition-[kafka,${topic_name},${partition}] i.e. "kafka-fortscale-aggr-feature-events-score-0-bytes-read"
            Optional optinalTopicStatus = Stream.of(KafkaSystemConsumerMetrics.TopicStatus.values()).filter(x -> entry.getKey().contains(x.value())).findFirst();
            KafkaSystemConsumerMetrics.TopicStatus topicStatus = null;
            if (optinalTopicStatus != Optional.empty()) {
                topicStatus = (KafkaSystemConsumerMetrics.TopicStatus) optinalTopicStatus.get();
                // remove SystemStreamPartition and topic status from topic name
                if (topicStatus.equals(KafkaSystemConsumerMetrics.TopicStatus.POLL_COUNT)) {
                    continue;
                }
            }
            if (topicOperation == null && topicStatus == null) {
                String errorMsg = String.format("topic %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            serviceKey = String.format("%s__%s", metricMessage.getHeader().getJobName(), topicName);
            // we do not monitor topic partitions count
            if (KafkaSystemConsumerMetrics.TopicOperation.TOPIC_PARTITIONS.equals(topicOperation)) {
                continue;
            }

            // if there is no metric for this topic, create one
            if (kafkaSystemConsumerMetricsServices.get(serviceKey) == null) {
                KafkaSystemConsumerMetricsService kafkaSystemConsumerMetricsService = new KafkaSystemConsumerMetricsService(statsService, metricMessage.getHeader().getJobName(), topicName);
                kafkaSystemConsumerMetricsServices.put(serviceKey, kafkaSystemConsumerMetricsService);
            }


            if (KafkaSystemConsumerMetrics.TopicOperation.RECONNECTS.equals(topicOperation)) {
                kafkaSystemConsumerMetricsServices.get(serviceKey).getKafkaSystemConsumerMetrics().setReconnects(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.SKIPPED_FETCH_REQUESTS.equals(topicOperation)) {
                kafkaSystemConsumerMetricsServices.get(serviceKey).getKafkaSystemConsumerMetrics().setSkippedFetchRequests(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.MESSAGES_READ.equals(topicOperation)) {
                kafkaSystemConsumerMetricsServices.get(serviceKey).getKafkaSystemConsumerMetrics().setMessagesRead(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.OFFSET_CHANGE.equals(topicOperation)) {
                kafkaSystemConsumerMetricsServices.get(serviceKey).getKafkaSystemConsumerMetrics().setOffsetChange(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.MESSAGES_BEHIND_HIGH_WATERMARK.equals(topicOperation)) {
                kafkaSystemConsumerMetricsServices.get(serviceKey).getKafkaSystemConsumerMetrics().setMessagesBehindWatermark(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.BYTES_READ.equals(topicOperation)) {
                if (entry.getValue().getClass().equals(Integer.class))
                    kafkaSystemConsumerMetricsServices.get(serviceKey).getKafkaSystemConsumerMetrics().setBytesRead(((Integer) entry.getValue()).longValue());
                else
                    kafkaSystemConsumerMetricsServices.get(serviceKey).getKafkaSystemConsumerMetrics().setBytesRead(((Long) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicOperation.HIGH_WATERMARK.equals(topicOperation)) {
                kafkaSystemConsumerMetricsServices.get(serviceKey).getKafkaSystemConsumerMetrics().setHighWaterMark(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.BLOCKING_POLL_COUNT.equals(topicStatus)) {
                kafkaSystemConsumerMetricsServices.get(serviceKey).getKafkaSystemConsumerMetrics().setBlockingPoll(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.NO_MORE_MESSAGES.equals(topicStatus)) {
                long noMoreMessages = 0;
                if (topicStatus.equals("true")) {
                    noMoreMessages = 1;
                }
                kafkaSystemConsumerMetricsServices.get(serviceKey).getKafkaSystemConsumerMetrics().setNoMoreMessages(noMoreMessages);
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.BLOCKING_POLL_TIMEOUT_COUNT.equals(topicStatus)) {
                kafkaSystemConsumerMetricsServices.get(serviceKey).getKafkaSystemConsumerMetrics().setBlockingPollTimeout(((Integer) entry.getValue()).longValue());
            }
            if (KafkaSystemConsumerMetrics.TopicStatus.BUFFERED_MESSAGE_COUNT.equals(topicStatus)) {
                kafkaSystemConsumerMetricsServices.get(serviceKey).getKafkaSystemConsumerMetrics().setBufferedMessage(((Integer) entry.getValue()).longValue());
            }
            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
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
                storeName = getStoreName(entry.getKey());
            }

            if (operation == null) {
                String errorMsg = String.format("store %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            String serviceKey = String.format("%s__%s", jobName, storeName);
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
                storeName = getStoreName(entry.getKey());
            }

            if (operation == null) {
                String errorMsg = String.format("store %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            String serviceKey = String.format("%s__%s", jobName, storeName);

            // if there is no metric for this topic, create one
            if (keyValueStorageMetricsServices.get(serviceKey) == null) {
                KeyValueStorageMetricsService metricsService = new KeyValueStorageMetricsService(statsService, storeName, jobName);
                keyValueStorageMetricsServices.put(serviceKey, metricsService);
            }

            if (KeyValueStorageMetrics.StoreOperation.FLUSHES.equals(operation)) {
                keyValueStorageMetricsServices.get(serviceKey).getKeyValueStorageMetrics().setFlushes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.GETS.equals(operation)) {
                keyValueStorageMetricsServices.get(serviceKey).getKeyValueStorageMetrics().setQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.ALLS.equals(operation)) {
                keyValueStorageMetricsServices.get(serviceKey).getKeyValueStorageMetrics().setRecordsInStore(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.RANGES.equals(operation)) {
                keyValueStorageMetricsServices.get(serviceKey).getKeyValueStorageMetrics().setRangeQueries(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.DELETES.equals(operation)) {
                keyValueStorageMetricsServices.get(serviceKey).getKeyValueStorageMetrics().setDeletes(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.PUTS.equals(operation)) {
                keyValueStorageMetricsServices.get(serviceKey).getKeyValueStorageMetrics().setWrites(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.MESSAGES_RESTORED.equals(operation)) {
                keyValueStorageMetricsServices.get(serviceKey).getKeyValueStorageMetrics().setMessagesRestored(((Integer) entry.getValue()).longValue());
            }
            if (KeyValueStorageMetrics.StoreOperation.RESTORED_BYTES.equals(operation)) {
                if (entry.getValue().getClass().equals(Integer.class))
                    keyValueStorageMetricsServices.get(serviceKey).getKeyValueStorageMetrics().setRestoredBytes(((Integer) entry.getValue()).longValue());
                else
                    keyValueStorageMetricsServices.get(serviceKey).getKeyValueStorageMetrics().setRestoredBytes((Long) entry.getValue());
            }
            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
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
            String serviceKey;
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

            // if there is no metric for this topic, create one
            if (samzaContainerMetricsServices.get(serviceKey) == null) {
                SamzaContainerMetricsService metricsService = new SamzaContainerMetricsService(statsService, jobName);
                samzaContainerMetricsServices.put(serviceKey, metricsService);
            }

            if (SamzaContainerMetrics.JobContainerOperation.COMMITS.equals(operation)) {
                samzaContainerMetricsServices.get(serviceKey).getSamzaContainerMetrics().setCommitCalls(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.WINDOWS.equals(operation)) {
                samzaContainerMetricsServices.get(serviceKey).getSamzaContainerMetrics().setWindowCalls(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.PROCESSES.equals(operation)) {
                samzaContainerMetricsServices.get(serviceKey).getSamzaContainerMetrics().setProcessCalls(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.SENDS.equals(operation)) {
                samzaContainerMetricsServices.get(serviceKey).getSamzaContainerMetrics().setSendCalls(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.ENVELOPES.equals(operation)) {
                samzaContainerMetricsServices.get(serviceKey).getSamzaContainerMetrics().setProcessEnvelopes(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.NULL_ENVELOPES.equals(operation)) {
                samzaContainerMetricsServices.get(serviceKey).getSamzaContainerMetrics().setProcessNullEnvelopes(((Integer) entry.getValue()).longValue());
            }
            if (SamzaContainerMetrics.JobContainerOperation.CHOOSE_MS.equals(operation)) {
                samzaContainerMetricsServices.get(serviceKey).getSamzaContainerMetrics().setChooseSeconds(((double) entry.getValue()));
            }
            if (SamzaContainerMetrics.JobContainerOperation.WINDOW_MS.equals(operation)) {
                samzaContainerMetricsServices.get(serviceKey).getSamzaContainerMetrics().setWindowSeconds(((double) entry.getValue()));
            }
            if (SamzaContainerMetrics.JobContainerOperation.PROCESS_MS.equals(operation)) {
                samzaContainerMetricsServices.get(serviceKey).getSamzaContainerMetrics().setProcessSeconds(((double) entry.getValue()));
            }
            if (SamzaContainerMetrics.JobContainerOperation.COMMIT_MS.equals(operation)) {
                samzaContainerMetricsServices.get(serviceKey).getSamzaContainerMetrics().setCommitSeconds(((double) entry.getValue()));
            }
            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
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
            String serviceKey;
            Optional optionalOperation = Stream.of(TaskInstanceMetrics.TaskOperation.values()).filter(x -> entry.getKey().endsWith(x.value())).findFirst();

            if (optionalOperation != Optional.empty()) {
                operation = (TaskInstanceMetrics.TaskOperation) optionalOperation.get();
                serviceKey = jobName;
            } else {
                topicName = getTopicName(entry.getKey());
                if (topicName.contains(metricMessage.getHeader().getHost())) {
                    continue;
                }
                serviceKey = String.format("%s__%s", jobName, topicName);
            }

            if (operation == null && topicName == null) {
                String errorMsg = String.format("task %s has an unknown action name", entry.getKey());
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            if (entry.getKey().contains("offset") && ((entry.getValue() == null) || (entry.getValue().equals("null")))) {
                continue;
            }
            // if there is no metric for this topic, create one
            if (taskInstanceMetricsServices.get(serviceKey) == null) {
                TaskInstanceMetricsService metricsService;
                if (operation != null) {
                    metricsService = new TaskInstanceMetricsService(statsService, jobName);

                } else {
                    metricsService = new TaskInstanceMetricsService(statsService, jobName, topicName);
                }
                taskInstanceMetricsServices.put(serviceKey, metricsService);
            }
            if (entry.getKey().contains("offset")) {
                taskInstanceMetricsServices.get(serviceKey).getTaskInstanceOffsetsMetrics().setTopicOffset(Long.parseLong((String) entry.getValue()));

            } else {
                if (operation.equals(TaskInstanceMetrics.TaskOperation.COMMITS)) {
                    taskInstanceMetricsServices.get(serviceKey).getTaskInstanceMetrics().setCommitsCalls(((Integer) entry.getValue()).longValue());
                } else if (operation.equals(TaskInstanceMetrics.TaskOperation.WINDOWS)) {
                    taskInstanceMetricsServices.get(serviceKey).getTaskInstanceMetrics().setWindowCalls(((Integer) entry.getValue()).longValue());
                } else if (operation.equals(TaskInstanceMetrics.TaskOperation.PROCESSES)) {
                    taskInstanceMetricsServices.get(serviceKey).getTaskInstanceMetrics().setProcessCalls(((Integer) entry.getValue()).longValue());
                } else if (operation.equals(TaskInstanceMetrics.TaskOperation.SENDS)) {
                    taskInstanceMetricsServices.get(serviceKey).getTaskInstanceMetrics().setSendCalls(((Integer) entry.getValue()).longValue());
                } else if (operation.equals(TaskInstanceMetrics.TaskOperation.FLUSH_CALLS)) {
                    taskInstanceMetricsServices.get(serviceKey).getTaskInstanceMetrics().setFlushCalls(((Integer) entry.getValue()).longValue());
                } else if (operation.equals(TaskInstanceMetrics.TaskOperation.MESSAGES_SENT)) {
                    taskInstanceMetricsServices.get(serviceKey).getTaskInstanceMetrics().setMessagesSent(((Integer) entry.getValue()).longValue());
                } else {
                    logger.error("hello {}", operation.value());
                }

            }
            if (!serviceKeys.contains(serviceKey)) {
                serviceKeys.add(serviceKey);
            }
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
