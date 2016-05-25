package fortscale.monitoring.external.stats.samza.collector.service.impl;

import fortscale.monitoring.external.stats.samza.collector.service.SamzaMetricsCollectorService;
import fortscale.monitoring.external.stats.samza.collector.service.stats.SamzaMetricCollectorMetrics;
import fortscale.monitoring.external.stats.samza.collector.topicReader.SamzaMetricsTopicSyncReader;
import fortscale.monitoring.external.stats.samza.collector.topicReader.SamzaMetricsTopicSyncReaderResponse;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.samza.metricMessageModels.MetricMessage;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * the collector reads metrics messages from samza standard topic "metrics" and re-write them to "fortscale-metrics" topic using the stats service
 */
public class SamzaMetricsCollectorServiceImpl implements SamzaMetricsCollectorService {
    private static final Logger logger = Logger.getLogger(SamzaMetricsCollectorServiceImpl.class);

    private volatile boolean shouldRun;
    private SamzaMetricToStatsServiceConversionHandler converter;
    private StatsService statsService;
    private SamzaMetricsTopicSyncReader topicSyncReader;
    private Thread thread;
    private long waitBetweenReadRetries;
    private long waitBetweenEmptyReads;
    private SamzaMetricCollectorMetrics samzaMetricCollectorMetrics;

    /**
     * ctor
     *
     * @param statsService           stats service
     * @param topicSyncReader        samza "metrics" topic blocking reader
     * @param waitBetweenReadRetries wait between one read to another in case of read failure (in millis)
     * @param waitBetweenEmptyReads  wait between one read to another in case of empty message response (in millis)
     * @param shouldStartInNewThread should the read start from a new thread
     */
    public SamzaMetricsCollectorServiceImpl(StatsService statsService, SamzaMetricsTopicSyncReader topicSyncReader, long waitBetweenReadRetries, long waitBetweenEmptyReads, boolean shouldStartInNewThread) {
        // self monitoring metrics
        this.statsService = statsService;
        this.samzaMetricCollectorMetrics = new SamzaMetricCollectorMetrics(this.statsService);

        this.converter = new SamzaMetricToStatsServiceConversionHandler(this.statsService, samzaMetricCollectorMetrics);
        this.topicSyncReader = topicSyncReader;
        this.shouldRun = true;
        this.waitBetweenReadRetries = waitBetweenReadRetries;
        this.waitBetweenEmptyReads = waitBetweenEmptyReads;


        if (shouldStartInNewThread) {
            thread = new Thread(this::start);
            thread.start();
        }
    }

    /**
     * shut down method for proper process stop
     * this function must be called from innershutdown() or spring context shutdown hook
     */
    @Override
    public void shutDown() {
        logger.info("Samza metrics collector is shutting down");

        shouldRun = false;
    }


    @Override
    public void start() {
        logger.info("Samza metrics collector start");
        while (shouldRun) {
            List<MetricMessage> metricMessages;
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
                    logger.info("sleep interrupted while waiting between kafka read retries, shutting down", e1);
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
                    logger.info("sleep interrupted while waiting between empty reads, shutting down", e);
                    innerShutDown();
                    continue;
                }
                continue;

            }
            // handle samza metrics
            try {
                samzaMetricsToStatsMetrics(metricMessages);
            } catch (Exception e) {
                logger.error("unexpected error happened when trying to convert samza metric to stats service metric", e);
            }
        }

    }

    /**
     * converts metric messages to engine data via stats service
     *
     * @param metricMessages list of samza metric messages
     */
    public void samzaMetricsToStatsMetrics(List<MetricMessage> metricMessages) {
        metricMessages.forEach(metricMessage -> {
            try {
                converter.handleSamzaMetric(metricMessage);
                samzaMetricCollectorMetrics.convertedMessages++;
            } catch (Exception e) {
                String message = String.format("unexpected error happened while converting metric message %s to stats metric", metricMessage.toString());
                samzaMetricCollectorMetrics.fullMessageConversionFailures++;
                logger.error(message, e);
            }
        });
    }

    /**
     * reads metric messages from standard "metrics topic"
     *
     * @return list of metric messages
     */
    public List<MetricMessage> readMetricsTopic() {
        logger.debug("Starts reading from metrics topic");
        SamzaMetricsTopicSyncReaderResponse metricMessages = topicSyncReader.getMessagesAsMetricMessages();
        long numberOfReadMetricsMessages = metricMessages.getMetricMessages().size();
        logger.debug("Read {} messages from metrics topic", numberOfReadMetricsMessages);
        if (!metricMessages.getMetricMessages().isEmpty()) {
            samzaMetricCollectorMetrics.readSamzaMetrics += numberOfReadMetricsMessages;

        }
        samzaMetricCollectorMetrics.unresolvedMetricMessages += metricMessages.getNumberOfUnresolvedMessages();
        return metricMessages.getMetricMessages();
    }

    /**
     * inner shut down method. should be called in case of internal failure
     */
    public void innerShutDown() {
        logger.info("inner shut down is happening");
        shutDown();
    }
}
