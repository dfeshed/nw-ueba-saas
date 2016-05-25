package fortscale.monitoring.external.stats.samza.collector.service.impl;

import fortscale.monitoring.external.stats.samza.collector.service.impl.converter.*;
import fortscale.monitoring.external.stats.samza.collector.service.stats.SamzaMetricCollectorMetrics;
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
    private KeyValueChangelogTopicToStatsConverter keyValueChangelogTopicToStatsConverter;
    private KeyValueStorageMetricsToStatsConverter keyValueStorageMetricsToStatsConverter;
    private KeyValueStoreMetricsToStatsConverter keyValueStoreMetricsToStatsConverter;
    private SamzaContainerToStatsConverter samzaContainerToStatsConverter;
    private StatsService statsService;

    /**
     * ctor
     * @param statsService
     */
    public SamzaMetricToStatsServiceConversionHandler(StatsService statsService,SamzaMetricCollectorMetrics samzaMetricCollectorMetrics) {
        kafkaSystemConsumerToStatsConverter = new KafkaSystemConsumerToStatsConverter(statsService,samzaMetricCollectorMetrics);
        kafkaSystemProducerToStatsConverter = new KafkaSystemProducerToStatsConverter(statsService,samzaMetricCollectorMetrics);
        keyValueChangelogTopicToStatsConverter = new KeyValueChangelogTopicToStatsConverter(statsService,samzaMetricCollectorMetrics);
        keyValueStorageMetricsToStatsConverter = new KeyValueStorageMetricsToStatsConverter(statsService,samzaMetricCollectorMetrics);
        keyValueStoreMetricsToStatsConverter = new KeyValueStoreMetricsToStatsConverter(statsService,samzaMetricCollectorMetrics);
        samzaContainerToStatsConverter = new SamzaContainerToStatsConverter(statsService,samzaMetricCollectorMetrics);
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
            }
            if (metric.containsKey(KafkaSystemProducerToStatsConverter.METRIC_NAME)) {
                kafkaSystemProducerToStatsConverter.convert(metricEntries.get(KafkaSystemProducerToStatsConverter.METRIC_NAME)
                        , jobName, metricTime, hostname);
            }
            if (metric.containsKey(KeyValueChangelogTopicToStatsConverter.METRIC_NAME)) {
                keyValueChangelogTopicToStatsConverter.convert(metricEntries.get(KeyValueChangelogTopicToStatsConverter.METRIC_NAME)
                        , jobName, metricTime, hostname);
            }
            if (metric.containsKey(KeyValueStorageMetricsToStatsConverter.METRIC_NAME)) {
                keyValueStorageMetricsToStatsConverter.convert(metricEntries.get(KeyValueStorageMetricsToStatsConverter.METRIC_NAME)
                        , jobName, metricTime, hostname);
            }
            if (metric.containsKey(KeyValueStoreMetricsToStatsConverter.METRIC_NAME)) {
                keyValueStoreMetricsToStatsConverter.convert(metricEntries.get(KeyValueStoreMetricsToStatsConverter.METRIC_NAME)
                        , jobName, metricTime, hostname);
            }
            if (metric.containsKey(SamzaContainerToStatsConverter.METRIC_NAME)) {
                samzaContainerToStatsConverter.convert(metricEntries.get(SamzaContainerToStatsConverter.METRIC_NAME)
                        , jobName, metricTime, hostname);
            }
        }
        catch (Exception e)
        {
            String message = String.format("unexcpected error happend while trying to convert metric message %s to stats",metricMessage.toString());
            logger.error(message,e);
        }
    }


}
