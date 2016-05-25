package fortscale.monitoring.metrics.adapter.impl;

import fortscale.monitoring.metrics.adapter.MetricAdapterService;
import fortscale.monitoring.metrics.adapter.stats.MetricAdapterMetrics;
import fortscale.monitoring.metrics.adapter.topicReader.EngineDataTopicSyncReader;
import fortscale.monitoring.metrics.adapter.topicReader.EngineDataTopicSyncReaderResponse;
import fortscale.utils.influxdb.Exception.InfluxDBNetworkException;
import fortscale.utils.influxdb.Exception.InfluxDBRuntimeException;
import fortscale.utils.influxdb.InfluxdbService;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.models.engine.*;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

/**
 * This process reads metrics from the metrics topic and writes them to the influx DB via the InfluxdbService
 */
public class MetricAdapterServiceImpl implements MetricAdapterService {
    private static final Logger logger = Logger.getLogger(MetricAdapterServiceImpl.class);

    private InfluxdbService influxdbService;
    private EngineDataTopicSyncReader metricsSyncReader;
    private MetricAdapterMetrics metricAdapterSelfMetrics;

    private long metricsAdapterMajorVersion;
    private String dbName;
    private String retentionName;
    private String retentionDuration;
    private String retentionReplication;
    private long waitBetweenWriteRetries;
    private long waitBetweenInitRetries;
    private long waitBetweenReadRetries;
    private long waitBetweenEmptyReads;

    private Thread thread;

    private volatile boolean shouldRun;

    /**
     * ctor
     *
     * @param influxdbService            - time series db java client
     * @param engineDataTopicSyncReader  - kafka metrics topic reader
     * @param metricsAdapterMajorVersion - messages version
     * @param dbName                     - time series db name
     * @param retentionName              - time series retention name
     * @param retentionDuration          - time series retention duration
     * @param retentionReplication       - time series replication
     * @param waitBetweenWriteRetries    - wait period in seconds between write retries to time series db
     * @param waitBetweenInitRetries     - wait period in seconds between init retries to time series db
     * @param waitBetweenReadRetries     - wait period in seconds between read retries to time series db
     * @param shouldStartInNewThread     - boolean, should metric adapter read in the same thread or a different one from kafka metrics topic
     */
    public MetricAdapterServiceImpl(StatsService statsService, InfluxdbService influxdbService,
                                    EngineDataTopicSyncReader engineDataTopicSyncReader,
                                    long metricsAdapterMajorVersion,
                                    String dbName, String retentionName, String retentionDuration,
                                    String retentionReplication, long waitBetweenWriteRetries, long waitBetweenInitRetries,
                                    long waitBetweenReadRetries, long waitBetweenEmptyReads, boolean shouldStartInNewThread) {
        this.influxdbService = influxdbService;
        this.metricsSyncReader = engineDataTopicSyncReader;
        this.dbName = dbName;
        this.retentionName = retentionName;
        this.retentionDuration = retentionDuration;
        this.retentionReplication = retentionReplication;
        this.waitBetweenWriteRetries = waitBetweenWriteRetries;
        this.waitBetweenInitRetries = waitBetweenInitRetries;
        this.waitBetweenReadRetries = waitBetweenReadRetries;
        this.waitBetweenEmptyReads = waitBetweenEmptyReads;
        this.metricsAdapterMajorVersion = metricsAdapterMajorVersion;
        this.metricAdapterSelfMetrics = new MetricAdapterMetrics(statsService);
        this.shouldRun = true;
        if (shouldStartInNewThread) {
            thread = new Thread(() -> {
                init();
                start();
            });
            thread.start();
        }
    }

    /**
     * shut down method
     */
    public void shutDown() {
        logger.info("metric adapter is shutting down");
        shouldRun = false;
    }

    public void innerShutDown() {
        logger.info("inner shut down is happening");
        shutDown();
    }

    /**
     * initiating metrics adapter environment (influxdb).
     * forever reads from metrics topic & writes batch to db
     */
    public void start() {
        logger.info("metric adapter starts reading from kafka topic");
        while (shouldRun) {
            EngineDataTopicSyncReaderResponse metricMessages;
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
            if (metricMessages.getMessages().isEmpty()) {
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
            // convert kafka metric message to time series DTO
            BatchPoints batchPoints = EngineDataToBatchPoints(metricMessages);

            while (shouldRun) {
                try {
                    long amountOfBatchPoints = batchPoints.getPoints().size();
                    if (amountOfBatchPoints > 0) {
                        // write to time series db
                        influxdbService.batchWrite(batchPoints);
                        metricAdapterSelfMetrics.writtenPoints += amountOfBatchPoints;
                    }
                    break;
                }
                // in case of network failure, stay in loop and try again
                catch (InfluxDBNetworkException e) {
                    logger.error("Failed to connect influxdb. Exception message", e);
                    try {
                        logger.debug("sleeping for {} ,before writing again to influxdb", waitBetweenWriteRetries);
                        sleep(waitBetweenWriteRetries);
                    } catch (InterruptedException e1) {
                        logger.error("unable to wait kafka read between retries , sleep interrupted", e1);
                        innerShutDown();
                    }
                }
                // in case that is different from network failure, drop record and continue
                catch (InfluxDBRuntimeException e) {
                    logger.error("Failed to write influxdb. Exception message: ", e);
                    try {
                        logger.debug("sleeping for {} ,before writing again to influxdb", waitBetweenWriteRetries);
                        sleep(waitBetweenWriteRetries);
                        break;
                    } catch (InterruptedException e1) {
                        logger.error("unable to wait between influx write retries, sleep interrupted", e1);
                        innerShutDown();
                    }
                }
            }
        }
    }

    /**
     * initiating time series db with default db name and retention
     */
    public void init() {

        boolean isFirstInitiation = true;
        while (shouldRun) {
            try {
                if (!isFirstInitiation) {
                    sleep(waitBetweenInitRetries);
                }
                if (!influxdbService.isInfluxDBStarted()) {
                    logger.debug("waiting for influxdb first initiation");
                    isFirstInitiation = false;
                    continue;
                }

                logger.info("Initializing influxdb");
                influxdbService.createDatabase(dbName);
                influxdbService.createDBRetention(retentionName, dbName, retentionDuration, retentionReplication);
                logger.info("Finished initializing influxdb");

                // Done
                break;
            }
            // in case of init failure, stay in loop and try again
            catch (InterruptedException e1) {
                logger.warn("failed to wait between influx init retries, sleep interrupted", e1);
                innerShutDown();
            } catch (Exception e) {
                logger.warn("Failed to initialized influxdb, retrying", e);
            }
        }

    }


    /**
     * reads messages from kafka metrics topic
     *
     * @return list of MetricMessage Pojos from kafka metrics topic
     */
    public EngineDataTopicSyncReaderResponse readMetricsTopic() {
        logger.debug("Starts reading from metrics topic");
        EngineDataTopicSyncReaderResponse metricMessages = metricsSyncReader.getMessagesAsEngineDataMetricMessages();
        long numberOfReadMetricsMessages = metricMessages.getMessages().size();
        logger.debug("Read {} messages from metrics topic", numberOfReadMetricsMessages);
        if (!metricMessages.getMessages().isEmpty()) {
            metricAdapterSelfMetrics.readMetricMessages += numberOfReadMetricsMessages;
            metricAdapterSelfMetrics.unresolvedMetricMessages += metricMessages.getNumberOfUnresolvedMessages();
        }
        return metricMessages;
    }

    /**
     * converts MetricMessages to BatchPoints. (if engine data has valid version and not null)
     *
     * @param metricMessages metric messages to convert
     * @return BatchPoints
     */
    public BatchPoints EngineDataToBatchPoints(EngineDataTopicSyncReaderResponse metricMessages) {
        BatchPoints.Builder batchPointsBuilder = BatchPoints.database(dbName);
        logger.debug("converting {} metrics messages to batch points", metricMessages.getMessages().size());
        try {

            for (EngineData metricMessage : metricMessages.getMessages()) {

                metricAdapterSelfMetrics.readEngineDataMessages++;

                // calculating data major version.
                long version = metricMessage.getVersion() / 100; //minor version is two last digits
                if (version != metricsAdapterMajorVersion) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Metric message version: {} is diffrent from supported version: {} in messages: {}", version, metricsAdapterMajorVersion, metricMessage.toString());
                    }
                    metricAdapterSelfMetrics.messagesFromBadVersion++;
                    continue;
                }
                engineDataToPoints(metricMessage).stream().forEach(batchPointsBuilder::point);

            }
        } catch (Exception e) {
            logger.error("Failed to convert metricsMessages To BatchPoints ", e);
        }

        return batchPointsBuilder.build();
    }


    /**
     * converts EngineData POJO to List<Point>. the List is built from the diffrent metrics groups
     * Timeunit is seconds by definition
     *
     * @param data EngineData object to be converted to Points
     * @return list of points (an influxdb DTO)
     */
    public List<Point> engineDataToPoints(EngineData data) {
        List<Point> points = new ArrayList<>();
        logger.debug("converting {} metricGroups engineData to List<points>", data.getMetricGroups().size());
        for (MetricGroup metricGroup : data.getMetricGroups()) {
            logger.debug("converting  metricGroup name: {}", metricGroup.getGroupName());
            String measurement = metricGroup.getGroupName();
            // get tags
            Map<String, String> tags = metricGroup.getTags().stream().collect(Collectors.toMap(Tag::getName, Tag::getValue));
            // get long fields
            Map<String, Object> longFields = metricGroup.getLongFields().stream().collect(Collectors.toMap(LongField::getName, LongField::getValue));
            // get double fields
            Map<String, Object> doubleFields = metricGroup.getDoubleFields().stream().collect(Collectors.toMap(DoubleField::getName, DoubleField::getValue));
            // get string fields
            Map<String, Object> stringFields = metricGroup.getStringFields().stream().collect(Collectors.toMap(StringField::getName, StringField::getValue));
            // get measurement time
            Long measurementTime = metricGroup.getMeasurementEpoch();
            // build point object with relevant fields
            Point.Builder doublePointBuilder = Point.measurement(measurement)
                    .time(measurementTime, TimeUnit.SECONDS)
                    .useInteger(false);
            Point.Builder longPointBuilder = Point.measurement(measurement)
                    .time(measurementTime, TimeUnit.SECONDS)
                    .useInteger(true);
            boolean hasDoubleFields = false;
            boolean hasLongFields = false;
            if (tags.size() > 0) {
                longPointBuilder.tag(tags);
                doublePointBuilder.tag(tags);
            }
            if (longFields.size() > 0) {
                longPointBuilder.fields(longFields);
                hasLongFields=true;
            }
            if (doubleFields.size() > 0) {
                doublePointBuilder.fields(doubleFields);
                hasDoubleFields=true;
            }
            if (stringFields.size() > 0) {
                longPointBuilder.fields(stringFields);
                doublePointBuilder.fields(stringFields);
            }
            Point longConvertedPoint = null;
            Point doubleConvertedPoint = null;
            try {
                if (hasLongFields) {
                    longConvertedPoint = longPointBuilder.build();
                }
                if(hasDoubleFields) {
                    doubleConvertedPoint = doublePointBuilder.build();
                }
            } catch (Exception e) {
                logger.error(String.format("failed to build point %s", metricGroup.toString()), e);
            }
            if (longConvertedPoint != null) {
                points.add(longConvertedPoint);
                logger.debug("converted point: {}", longConvertedPoint.toString());
            }
            if (doubleConvertedPoint != null) {
                points.add(doubleConvertedPoint);
                logger.debug("converted point: {}", doubleConvertedPoint.toString());
            }
        }
        logger.debug("converted {} metric groups", points.size());
        return points;
    }


}
