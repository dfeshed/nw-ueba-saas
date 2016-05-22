package fortscale.monitoring.external.stats.samza.collector.service.impl;

import fortscale.monitoring.external.stats.samza.collector.service.SamzaMetricsCollectorService;
import fortscale.monitoring.external.stats.samza.collector.converter.SamzaMetricToStatsServiceConverter;
import fortscale.monitoring.external.stats.samza.collector.metrics.SamzaMetricCollectorMetricsService;
import fortscale.monitoring.external.stats.samza.collector.topicReader.SamzaMetricsTopicSyncReader;
import fortscale.monitoring.external.stats.samza.collector.topicReader.SamzaMetricsTopicSyncReaderResponse;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * the collector reads metrics messages from samza standard topic "metrics" and re-write them to "fortscale-metrics" topic using the stats service
 */
public class SamzaMetricsCollectorServiceImpl implements SamzaMetricsCollectorService {
    private static final Logger logger = Logger.getLogger(SamzaMetricsCollectorServiceImpl.class);

    private volatile boolean shouldRun;
    private SamzaMetricToStatsServiceConverter converter;
    private StatsService statsService;
    private SamzaMetricsTopicSyncReader topicSyncReader;
    private Thread thread;
    private long waitBetweenReadRetries;
    private long waitBetweenEmptyReads;
    private SamzaMetricCollectorMetricsService samzaMetricCollectorMetricsService;

    /**
     * ctor
     *
     * @param statsService
     * @param topicSyncReader        samza "metrics" topic blocking reader
     * @param waitBetweenReadRetries wait between one read to another in case of read failure (in millis)
     * @param waitBetweenEmptyReads  wait between one read to another in case of empty message response (in millis)
     * @param shouldStartInNewThread should the read start from a new thread
     */
    public SamzaMetricsCollectorServiceImpl(StatsService statsService, SamzaMetricsTopicSyncReader topicSyncReader, long waitBetweenReadRetries, long waitBetweenEmptyReads, boolean shouldStartInNewThread) {
        this.converter = new SamzaMetricToStatsServiceConverter(statsService);
        this.statsService = statsService;
        this.topicSyncReader = topicSyncReader;
        this.shouldRun = true;
        this.waitBetweenReadRetries = waitBetweenReadRetries;
        this.waitBetweenEmptyReads = waitBetweenEmptyReads;
        this.samzaMetricCollectorMetricsService = new SamzaMetricCollectorMetricsService(statsService);

        if (shouldStartInNewThread) {
            thread = new Thread(() -> {
                start();
            });
            thread.start();
        }
    }

    @Override
    public void shutDown() {
        logger.info("metric adapter is shutting down");

        shouldRun = false;
    }

    @Override
    public void start() {
        logger.info("metric adapter starts reading from kafka topic");
        while (shouldRun) {
            List<SamzaMetricsTopicSyncReaderResponse> metricMessages = new ArrayList<>();
            try {
                // reading messages from metrics topic
                metricMessages = readMetricsTopic();
            } catch (Exception e) {
                logger.error("failed to read from kafka metrics topic", e);
                try {
                    // in case of failure, wait and then try again
                    logger.debug("sleeping for {} ,before reading again from kafka ", waitBetweenReadRetries);
                    sleep(waitBetweenReadRetries);

                } catch (InterruptedException e1) {
                    logger.info("unable to wait kafka read between retries, sleep interupted", e1);
                    innerShutDown();
                    continue;
                }
                continue;
            }
            if (metricMessages.isEmpty()) {
                try {
                    logger.debug("sleeping for {} ,before reading again from kafka ", waitBetweenEmptyReads);
                    sleep(waitBetweenEmptyReads);
                } catch (InterruptedException e) {
                    logger.info("unable to wait kafka read between retries, sleep interupted", e);
                    innerShutDown();
                    continue;
                }
                continue;

            }
            // handle samza metrics
            try {
                samzaMetricsToStatsService(metricMessages);
            } catch (Exception e) {
                logger.error("unexcpected error happend when trying to convert samza metric to stats service metric", e);
            }
        }

    }

    /**
     * converts metric messages to engine data via stats service
     *
     * @param metricMessages
     */
    public void samzaMetricsToStatsService(List<SamzaMetricsTopicSyncReaderResponse> metricMessages) {
        metricMessages.forEach(metricMessage -> converter.handleSamzaMetric(metricMessage.getMetricMessage()));
    }


    @Override
    public List<SamzaMetricsTopicSyncReaderResponse> readMetricsTopic() {
        logger.debug("Starts reading from metrics topic");
        List<SamzaMetricsTopicSyncReaderResponse> metricMessages = topicSyncReader.getMessagesAsMetricMessages();
        long numberOfReadMetricsMessages = metricMessages.size();
        logger.debug("Read {} messages from metrics topic", numberOfReadMetricsMessages);
        if (!metricMessages.isEmpty()) {
            samzaMetricCollectorMetricsService.getMetrics().numberOfReadSamzaMetrics += numberOfReadMetricsMessages;
            samzaMetricCollectorMetricsService.getMetrics().numberOfUnresolvedMetricMessages += metricMessages.stream().mapToLong(SamzaMetricsTopicSyncReaderResponse::getNumberOfUnresolvedMessages).sum();
        }
        return metricMessages;
    }


    public void innerShutDown() {
        logger.info("inner shut down is happening");
        shutDown();
    }
}
