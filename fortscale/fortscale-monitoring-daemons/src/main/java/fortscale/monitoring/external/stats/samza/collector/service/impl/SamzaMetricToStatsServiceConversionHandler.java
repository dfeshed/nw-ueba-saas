package fortscale.monitoring.external.stats.samza.collector.service.impl;

import fortscale.monitoring.external.stats.samza.collector.service.impl.converter.*;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.samza.metricMessageModels.MetricMessage;

import java.util.*;

/**
 * converts metric message to relevant stats metrics
 */
public class SamzaMetricToStatsServiceConversionHandler {
    private static final Logger logger = Logger.getLogger(SamzaMetricToStatsServiceConversionHandler.class);

    private KafkaSystemConsumerToStatsConverter kafkaSystemConsumerToStatsConverter;
    private KafkaSystemProducerToStatsConverter kafkaSystemProducerToStatsConverter;
    private KeyValueChanglogTopicToStatsConverter keyValueChanglogTopicToStatsConverter;
    private KeyValueStorageMetricsToStatsConverter keyValueStorageMetricsToStatsConverter;
    private KeyValueStoreMetricsToStatsConverter keyValueStoreMetricsToStatsConverter;
    private SamzaContainerToStatsConverter samzaContainerToStatsConverter;
    private StatsService statsService;

    /**
     * ctor
     * @param statsService
     */
    public SamzaMetricToStatsServiceConversionHandler(StatsService statsService) {
        kafkaSystemConsumerToStatsConverter = new KafkaSystemConsumerToStatsConverter(statsService);
        kafkaSystemProducerToStatsConverter = new KafkaSystemProducerToStatsConverter(statsService);
        keyValueChanglogTopicToStatsConverter = new KeyValueChanglogTopicToStatsConverter(statsService);
        keyValueStorageMetricsToStatsConverter = new KeyValueStorageMetricsToStatsConverter(statsService);
        keyValueStoreMetricsToStatsConverter = new KeyValueStoreMetricsToStatsConverter(statsService);
        samzaContainerToStatsConverter = new SamzaContainerToStatsConverter(statsService);
        this.statsService = statsService;
    }


    /**
     * rewrite samza metrics to kafka metrics topic as a tagged EngineData object
     *
     * @param metricMessage metric message
     */
    public void handleSamzaMetric(MetricMessage metricMessage) {
        try {
            Map<String, Map<String, Object>> metric = metricMessage.getMetrics().getAdditionalProperties();
            String jobName = metricMessage.getHeader().getJobName();
            long metricTime = SamzaMetricsConversionUtil.getMetricMessageTime(metricMessage);
            String hostname = metricMessage.getHeader().getHost();
            Map<String, Map<String, Object>> metricEntries = metricMessage.getMetrics().getAdditionalProperties();
            if (metric.containsKey(KafkaSystemConsumerToStatsConverter.METRIC_NAME)) {
                kafkaSystemConsumerToStatsConverter.convert(metricEntries.get(KafkaSystemConsumerToStatsConverter.METRIC_NAME)
                        , jobName, metricTime, hostname);
                statsService.ManualUpdatePush();
            }
            if (metric.containsKey(KafkaSystemProducerToStatsConverter.METRIC_NAME)) {
                kafkaSystemProducerToStatsConverter.convert(metricEntries.get(KafkaSystemProducerToStatsConverter.METRIC_NAME)
                        , jobName, metricTime, hostname);
                statsService.ManualUpdatePush();
            }
            if (metric.containsKey(KeyValueChanglogTopicToStatsConverter.METRIC_NAME)) {
                keyValueChanglogTopicToStatsConverter.convert(metricEntries.get(KeyValueChanglogTopicToStatsConverter.METRIC_NAME)
                        , jobName, metricTime, hostname);
                statsService.ManualUpdatePush();
            }
            if (metric.containsKey(KeyValueStorageMetricsToStatsConverter.METRIC_NAME)) {
                keyValueStorageMetricsToStatsConverter.convert(metricEntries.get(KeyValueStorageMetricsToStatsConverter.METRIC_NAME)
                        , jobName, metricTime, hostname);
                statsService.ManualUpdatePush();
            }
            if (metric.containsKey(KeyValueStoreMetricsToStatsConverter.METRIC_NAME)) {
                keyValueStoreMetricsToStatsConverter.convert(metricEntries.get(KeyValueStoreMetricsToStatsConverter.METRIC_NAME)
                        , jobName, metricTime, hostname);
                statsService.ManualUpdatePush();
            }
            if (metric.containsKey(SamzaContainerToStatsConverter.METRIC_NAME)) {
                samzaContainerToStatsConverter.convert(metricEntries.get(SamzaContainerToStatsConverter.METRIC_NAME)
                        , jobName, metricTime, hostname);
                statsService.ManualUpdatePush();
            }
        }
        catch (Exception e)
        {
            String message = String.format("unexcpected error happend while trying to convert metric message %s to stats",metricMessage.toString());
            logger.error(message,e);
        }
    }


}
