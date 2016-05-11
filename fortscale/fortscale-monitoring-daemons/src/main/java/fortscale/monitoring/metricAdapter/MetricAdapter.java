package fortscale.monitoring.metricAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.monitoring.metricAdapter.stats.MetricAdapterMetrics;
import fortscale.monitoring.metricAdapter.stats.MetricAdapterMetricsService;
import fortscale.monitoring.samza.metricWriter.SamzaMetricWriter;
import fortscale.utils.influxdb.Exception.InfluxDBNetworkExcpetion;
import fortscale.utils.influxdb.Exception.InfluxDBRuntimeException;
import fortscale.utils.influxdb.InfluxdbClient;
import fortscale.monitoring.samza.topicReader.SamzaMetricsTopicSyncReader;
import fortscale.monitoring.samza.topicReader.SamzaMetricsTopicSyncReaderResponse;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.models.engine.*;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

/**
 * This process reads metrics from the metrics topic and writes them to the influx DB via the InfluxdbClient
 */
public class MetricAdapter {
    private static final Logger logger = Logger.getLogger(MetricAdapter.class);

    private InfluxdbClient influxdbClient;
    private SamzaMetricsTopicSyncReader metricsSyncReader;
    private MetricAdapterMetricsService metricAdapterMetricsService;

    private long metricsAdapterMajorVersion;
    private String dbName;
    private String retentionName;
    private String retentionDuration;
    private String retentionReplication;
    private long waitBetweenWriteRetries;
    private long waitBetweenInitRetries;
    private long waitBetweenReadRetries;
    private String engineDataMetricName;
    private String engineDataMetricPackage;
    private long initiationWaitTimeInSeconds;
    private final SamzaMetricWriter samzaMetricWriter;

    private Thread thread;

    private volatile boolean shouldRun;

    /**
     * ctor
     *
     * @param initiationWaitTimeInSeconds - grace time for influxdb intiation
     * @param influxdbClient              - time series db java client
     * @param samzaMetricsTopicSyncReader - kafka metrics topic reader
     * @param samzaMetricWriter           - SamzaMetricWriter - used to convert samza standart metrics to Engine data forma
     * @param metricAdapterMetricsService         - metricAdapter metrics, i.e. number of messages read from kafka & number of messages written to time series db
     * @param metricsAdapterMajorVersion  - messages version
     * @param dbName                      - time series db name
     * @param retentionName               - time series retention name
     * @param retentionDuration           - time series retention duration
     * @param retentionReplication        - time series replication
     * @param waitBetweenWriteRetries     - wait period in seconds between write retries to time series db
     * @param waitBetweenInitRetries      - wait period in seconds between init retries to time series db
     * @param waitBetweenReadRetries      - wait period in seconds between read retries to time series db
     * @param engineDataMetricName        - engine data metric name
     * @param engineDataMetricPackage     - engine data metric package name - used for costume metric object reading
     * @param shouldStartInNewThread      - boolean, should metric adapter read in the same thread or a diffrent one from kafka metrics topic
     */
    public MetricAdapter(long initiationWaitTimeInSeconds, InfluxdbClient influxdbClient, SamzaMetricsTopicSyncReader samzaMetricsTopicSyncReader, SamzaMetricWriter samzaMetricWriter, MetricAdapterMetricsService metricAdapterMetricsService, long metricsAdapterMajorVersion, String dbName, String retentionName, String retentionDuration, String retentionReplication, long waitBetweenWriteRetries, long waitBetweenInitRetries, long waitBetweenReadRetries, String engineDataMetricName, String engineDataMetricPackage, boolean shouldStartInNewThread) {
        this.influxdbClient = influxdbClient;
        this.metricsSyncReader = samzaMetricsTopicSyncReader;
        this.metricAdapterMetricsService = this.metricAdapterMetricsService;
        this.dbName = dbName;
        this.retentionName = retentionName;
        this.retentionDuration = retentionDuration;
        this.retentionReplication = retentionReplication;
        this.waitBetweenWriteRetries = waitBetweenWriteRetries;
        this.waitBetweenInitRetries = waitBetweenInitRetries;
        this.waitBetweenReadRetries = waitBetweenReadRetries;
        this.engineDataMetricName = engineDataMetricName;
        this.engineDataMetricPackage = engineDataMetricPackage;
        this.metricsAdapterMajorVersion = metricsAdapterMajorVersion;
        this.initiationWaitTimeInSeconds = initiationWaitTimeInSeconds;
        this.samzaMetricWriter = samzaMetricWriter;
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

    /**
     * initiating metrics adapter environment (influxdb).
     * forever reads from metrics topic & writes batch to db
     */
    public void start() {
        logger.info("metric adapter starts reading from kafka topic");
        while (shouldRun) {
            List<SamzaMetricsTopicSyncReaderResponse> metricMessages = new ArrayList<>();
            try {
                // reading messages from metrics topic
                metricMessages = readMetricsTopic();
            }
            catch (Exception e) {
                logger.error("failed to read from kafka metrics topic", e);
                try {
                    // in case of failure, wait and then try again
                    sleep(waitBetweenReadRetries);
                } catch (InterruptedException e1) {
                    logger.error("unable to wait kafka read between retries, sleep interupted", e1);
                    shutDown();
                }
            }
            if (metricMessages.isEmpty()) {
                try {
                    sleep(waitBetweenReadRetries);
                    continue;
                } catch (InterruptedException e) {
                    logger.error("unable to wait kafka read between retries, sleep interupted", e);
                    shutDown();
                }
            }
            // convert kafka metric message to time series DTO
            BatchPoints batchPoints = metricsMessagesToBatchPoints(metricMessages);

            while (shouldRun) {
                try {
                    long amountOfBatchPoints = batchPoints.getPoints().size();
                    if (amountOfBatchPoints > 0) {
                        // write to time series db
                        influxdbClient.batchWrite(batchPoints);
                        metricAdapterMetricsService.getMetrics().addLong("numberOfWrittenPoints", amountOfBatchPoints);
                        metricAdapterMetricsService.getMetrics().addLong("numberOfWrittenPointsBytes", batchPoints.toString().length());
                    }
                    break;
                }
                // in case of network failure, stay in loop and try again
                catch (InfluxDBNetworkExcpetion e) {
                    logger.error("Failed to connect influxdb. Exception message", e);
                    try {
                        sleep(waitBetweenWriteRetries);
                    } catch (InterruptedException e1) {
                        logger.error("unable to wait kafka read between retries , sleep interupted", e1);
                        shutDown();
                    }
                }
                // in case that is different from network failure, drop record and continue
                catch (InfluxDBRuntimeException e) {
                    logger.error("Failed to write influxdb. Exception message: ", e);
                    try {
                        sleep(waitBetweenWriteRetries);
                        break;
                    } catch (InterruptedException e1) {
                        logger.error("unable to wait between influx write retries, sleep interupted", e1);
                        shutDown();
                    }
                }
            }
        }
    }

    /**
     * initiating time series db with default db name and retention
     */
    protected void init() {
        DateTime initiationTime = DateTime.now().plus(initiationWaitTimeInSeconds * 1000);
        while (DateTime.now().isBefore(initiationTime)) {
            if (influxdbClient.isInfluxDBStarted())
                break;
        }

        while (shouldRun) {
            try {
                logger.info("Initializing influxdb");
                influxdbClient.createDatabase(dbName);
                influxdbClient.createDBRetention(retentionName, dbName, retentionDuration, retentionReplication);
                logger.info("Finished initializing influxdb");
                break;
            }
            // in case of init failure, stay in loop and try again
            catch (Exception e) {
                logger.error("failed to initialized influxdb", e);
                try {
                    sleep(waitBetweenInitRetries);
                } catch (InterruptedException e1) {
                    logger.error("failed to wait between influx init retries, sleep interupted", e1);
                    shutDown();
                }
            }
        }

    }


    /**
     * reads messages from kafka metrics topic
     *
     * @return list of MetricMessage Pojos from kafka metrics topic
     */
    protected List<SamzaMetricsTopicSyncReaderResponse> readMetricsTopic() throws NoSuchFieldException, IllegalAccessException {
        logger.debug("Starts reading from metrics topic");
        List<SamzaMetricsTopicSyncReaderResponse> metricMessages = metricsSyncReader.getMessagesAsMetricMessages();
        long numberOfReadMetricsMessages = metricMessages.size();
        logger.debug("Read {} messages from metrics topic", numberOfReadMetricsMessages);
        if (!metricMessages.isEmpty()) {
            metricAdapterMetricsService.getMetrics().addLong("numberOfReadMetricMessages", numberOfReadMetricsMessages);
            metricAdapterMetricsService.getMetrics().addLong("numberOfReadMetricMessagesBytes", metricMessages.stream().mapToLong(SamzaMetricsTopicSyncReaderResponse::getMetricMessageSize).sum());
            metricAdapterMetricsService.getMetrics().addLong("numberOfUnresolvedMetricMessages", metricMessages.stream().mapToLong(SamzaMetricsTopicSyncReaderResponse::getNumberOfUnresolvedMessages).sum());
        }
        return metricMessages;
    }

    /**
     * converts MetricMessages to BatchPoints. (if engine data has valid version and not null)
     *
     * @param metricMessages
     * @return BatchPoints
     */
    protected BatchPoints metricsMessagesToBatchPoints(List<SamzaMetricsTopicSyncReaderResponse> metricMessages) {
        List<Point> points = new ArrayList<>();
        BatchPoints.Builder batchPointsBuilder = BatchPoints.database(dbName);
        logger.debug("converting {} metrics messages to batch points", metricMessages.size());
        for (SamzaMetricsTopicSyncReaderResponse metricMessage : metricMessages) {
            Map<String, Object> dataString = metricMessage.getMetricMessage().getMetrics().getAdditionalProperties().get(engineDataMetricPackage);

            samzaMetricWriter.handleSamzaMetric(metricMessage.getMetricMessage());
            if (dataString == null) //in case of generic samza metric, and not an EngineData metric
            {
                continue;
            }
            EngineData data = null;
            ObjectMapper mapper = new ObjectMapper();

            try {
                data = mapper.readValue(dataString.get(engineDataMetricName).toString(), EngineData.class);
            } catch (IOException e) {
                logger.error(String.format("Failed to convert message to EngineData object: %s",
                        metricMessage.getMetricMessage().getMetrics().getAdditionalProperties().get(engineDataMetricPackage)), e);
            }
            if (data == null) // in case of readValue failure pass to the next message
                continue;
            metricAdapterMetricsService.getMetrics().addLong("numberOfReadEngineDataMessages", 1);
            metricAdapterMetricsService.getMetrics().addLong("numberOfReadEngineDataMessagesBytes", metricMessage.getMetricMessageSize());

            // calculating data major version.
            long version = data.getVersion() / 100; //minor version is two last digits

            if (version == metricsAdapterMajorVersion)
                engineDataToPoints(data).stream().forEach(p -> batchPointsBuilder.point(p));
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
    public static List<Point> engineDataToPoints(EngineData data) {
        List<Point> points = new ArrayList<>();
        logger.debug("converting {} metricGroups engineData to List<points>", data.getMetricGroups().size());
        for (MetricGroup metricGroup : data.getMetricGroups()) {
            logger.debug("converting  metricGroup name: {}", metricGroup.getGroupName());
            String measurement = metricGroup.getGroupName();
            // get tags
            Map<String, String> tags = metricGroup.getTags().stream().collect(Collectors.toMap(Tag::getName, Tag::getValue));
            boolean containsNumeric = true;
            // get long fields
            Map<String, Object> longFields = metricGroup.getLongFields().stream().collect(Collectors.toMap(LongField::getName, LongField::getValue));
            // get double fields
            Map<String, Object> doubleFields = metricGroup.getDoubleFields().stream().collect(Collectors.toMap(DoubleField::getName, DoubleField::getValue));
            // get string fields
            Map<String, Object> stringFields = metricGroup.getStringFields().stream().collect(Collectors.toMap(StringField::getName, StringField::getValue));
            // get measurement time
            Long measurementTime = metricGroup.getMeasurementEpoch();

            // build point object with relevant fields
            Point.Builder pointBuilder = Point.measurement(measurement).time(measurementTime, TimeUnit.MILLISECONDS).useInteger(containsNumeric);
            if (tags.size() > 0)
                pointBuilder.tag(tags);
            if (longFields.size() > 0)
                pointBuilder.fields(longFields);
            if (doubleFields.size() > 0)
                pointBuilder.fields(doubleFields);
            if (stringFields.size() > 0)
                pointBuilder.fields(stringFields);
            Point convertedPoint = pointBuilder.build();
            logger.debug("converted point: {}", convertedPoint.toString());
            points.add(convertedPoint);
        }
        logger.debug("converted {} metric groups", points.size());
        return points;
    }


}
