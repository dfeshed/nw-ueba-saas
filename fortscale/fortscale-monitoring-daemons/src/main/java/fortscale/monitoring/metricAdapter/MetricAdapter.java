package fortscale.monitoring.metricAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.monitoring.metricAdapter.stats.MetricAdapterMetric;
import fortscale.monitoring.samza.metrics.KafkaSystemProducerMetricService;
import fortscale.utils.influxdb.Exception.InfluxDBNetworkExcpetion;
import fortscale.utils.influxdb.Exception.InfluxDBRuntimeException;
import fortscale.utils.influxdb.InfluxdbClient;
import fortscale.utils.kafka.kafkaMetricsTopicSyncReader.KafkaMetricsTopicSyncReader;
import fortscale.utils.kafka.metricMessageModels.KafkaTopicSyncReaderResponse;
import fortscale.utils.kafka.metricMessageModels.MetricMessage;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
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
    private KafkaMetricsTopicSyncReader kafkaMetricsSyncReader;
    private MetricAdapterMetric metricAdapterMetric;
    private Map<String,KafkaSystemProducerMetricService> kafkaSystemProducerMetricServices;
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
    private StatsService statsService;
    //kafka reader params:
    private String topicClientId;
    private int topicPartition;

    private Thread thread;

    private volatile boolean shouldRun;

    /**
     * ctor
     * @param initiationWaitTimeInSeconds - grace time for influxdb intiation
     * @param topicClientId               - kafka reader client id
     * @param topicPartition              - kafka topic partition - usually 0
     * @param influxdbClient              - time series db java client
     * @param kafkaMetricsTopicSyncReader - kafka metrics topic reader
     * @param statsService                - stats service is a metric collector
     * @param metricAdapterMetric         - metricAdapter metrics, i.e. number of messages read from kafka & number of messages written to time series db
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
    public MetricAdapter(long initiationWaitTimeInSeconds, String topicClientId, int topicPartition, InfluxdbClient influxdbClient, KafkaMetricsTopicSyncReader kafkaMetricsTopicSyncReader, StatsService statsService, MetricAdapterMetric metricAdapterMetric, long metricsAdapterMajorVersion, String dbName, String retentionName, String retentionDuration, String retentionReplication, long waitBetweenWriteRetries, long waitBetweenInitRetries, long waitBetweenReadRetries, String engineDataMetricName, String engineDataMetricPackage, boolean shouldStartInNewThread) {
        this.topicClientId = topicClientId;
        this.topicPartition = topicPartition;
        this.influxdbClient = influxdbClient;
        this.kafkaMetricsSyncReader = kafkaMetricsTopicSyncReader;
        this.metricAdapterMetric = metricAdapterMetric;
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
        this.statsService=statsService;
        this.shouldRun=true;
        this.kafkaSystemProducerMetricServices = new HashMap();

        if(shouldStartInNewThread) {
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
    public void shutDown()
    {
        logger.info("metric adapter is shutting down");
        shouldRun=false;
    }

    /**
     * initiating metrics adapter environment (influxdb).
     * forever reads from metrics topic & writes batch to db
     */
    public void start() {
        logger.info("metric adapter starts reading from kafka topic");
        while (shouldRun) {
            List<KafkaTopicSyncReaderResponse> metricMessages = new ArrayList<>();
            try {
                metricMessages = readMetricsTopic();
            } catch (Exception e) {
                logger.error("failed to read from kafka metrics topic", e);
                try {
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
            BatchPoints batchPoints = metricsMessagesToBatchPoints(metricMessages);

            while (shouldRun) {
                try {
                    long amountOfBatchPoints = batchPoints.getPoints().size();
                    if (amountOfBatchPoints > 0) {
                        influxdbClient.batchWrite(batchPoints);
                        metricAdapterMetric.addLong("numberOfWrittenPoints", amountOfBatchPoints);
                        metricAdapterMetric.addLong("numberOfWrittenPointsBytes", batchPoints.toString().length());
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
                // in case that is diffrent from network failure, drop record and continue
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
     * initiating the environment with default values from InfluxDBStatsInit
     */
    protected void init() {
        DateTime initiationTime= DateTime.now().plus(initiationWaitTimeInSeconds*1000);
        while (DateTime.now().isBefore(initiationTime))
        {
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
                    logger.error("failed to wait between influx init retries, sleep interupted",e1);
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
    protected List<KafkaTopicSyncReaderResponse> readMetricsTopic() throws NoSuchFieldException, IllegalAccessException {
        logger.debug("Starts reading from metrics topic");
        List<KafkaTopicSyncReaderResponse> metricMessages = kafkaMetricsSyncReader.getMessagesAsMetricMessage(topicClientId,topicPartition);
        long numberOfReadMetricsMessages = metricMessages.size();
        logger.debug("Read {} messages from metrics topic", numberOfReadMetricsMessages);
        if (!metricMessages.isEmpty()) {
            metricAdapterMetric.addLong("numberOfReadMetricMessages", numberOfReadMetricsMessages);
            metricAdapterMetric.addLong("numberOfReadMetricMessagesBytes", metricMessages.stream().mapToLong(KafkaTopicSyncReaderResponse::getMetricMessageSize).sum());
        }
        return metricMessages;
    }

    /**
     * converts MetricMessages to BatchPoints. (if engine data has valid version and not null)
     * @param metricMessages
     * @return BatchPoints
     */
    protected BatchPoints metricsMessagesToBatchPoints(List<KafkaTopicSyncReaderResponse> metricMessages)  {
        List<Point> points = new ArrayList<>();
        BatchPoints.Builder batchPointsBuilder = BatchPoints.database(dbName);
        logger.debug("converting {} metrics messages to batch points", metricMessages.size());
        for (KafkaTopicSyncReaderResponse metricMessage : metricMessages) {
            Map<String, Object> dataString = metricMessage.getMetricMessage().getMetrics().getAdditionalProperties().get(engineDataMetricPackage);

            handleSamzaMetric(metricMessage.getMetricMessage());
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
            metricAdapterMetric.addLong("numberOfReadEngineDataMessages", 1);
            metricAdapterMetric.addLong("numberOfReadEngineDataMessagesBytes", metricMessage.getMetricMessageSize());

            // calculating data major version.
            long version = data.getVersion() / 100; //minor version is two last digits

            if (version == metricsAdapterMajorVersion)
                engineDataToPoints(data).stream().forEach(p -> batchPointsBuilder.point(p));
        }

        return batchPointsBuilder.build();
    }


    /**
     * rewrite kafkaSystemProducerMetrics to kafka
     * @param metricMessage
     */
    protected void updateKafkaSystemProducerMetric(MetricMessage metricMessage)
    {
        logger.debug("Updating KafkaSystemProducerMetric with: {}",metricMessage.toString());
        Map<String,Map<String,Object>>metric= metricMessage.getMetrics().getAdditionalProperties();
        for (Map.Entry<String,Object> entry : metric.get("org.apache.samza.system.kafka.KafkaSystemProducerMetrics").entrySet())
        {
            String entryName=entry.getKey();
            String topic = entryName.split("-")[0];
            KafkaSystemProducerMetricService kafkaSystemProducerMetricService= kafkaSystemProducerMetricServices.get(topic);

            // if there is no metric for this topic, create one
            if(kafkaSystemProducerMetricService==null)
            {
                kafkaSystemProducerMetricService = new KafkaSystemProducerMetricService(statsService,topic);
                kafkaSystemProducerMetricService.getKafkaSystemProducerMetric().manualUpdate(metricMessage.getHeader().getTime());
            }
            if (entryName.endsWith("flushes"))
            {
                kafkaSystemProducerMetricService.getKafkaSystemProducerMetric().setFlushes((long) entry.getValue());
            }
            if (entryName.endsWith("flush-failed"))
            {
                kafkaSystemProducerMetricService.getKafkaSystemProducerMetric().setFlushFailed((long) entry.getValue());
            }
            if (entryName.endsWith("flush-ns"))
            {
                kafkaSystemProducerMetricService.getKafkaSystemProducerMetric().setFlushSeconds((double) entry.getValue());
            }
            if (entryName.endsWith("producer-retries"))
            {
                kafkaSystemProducerMetricService.getKafkaSystemProducerMetric().setProducerRetries((long) entry.getValue());
            }
            if (entryName.endsWith("producer-send-failed"))
            {
                kafkaSystemProducerMetricService.getKafkaSystemProducerMetric().setProducerSendFailed((long) entry.getValue());
            }
            if (entryName.endsWith("producer-send-success"))
            {
                kafkaSystemProducerMetricService.getKafkaSystemProducerMetric().setProducerSendSuccess((long) entry.getValue());
            }
            if (entryName.endsWith("producer-sends"))
            {
                kafkaSystemProducerMetricService.getKafkaSystemProducerMetric().setProducerSends((long) entry.getValue());
            }
            kafkaSystemProducerMetricServices.put(topic,kafkaSystemProducerMetricService);
        }
    }

    /**
     * rewrite samza metrics to kafka metrics topic as a tagged EngineData object
     * @param metricMessage
     */
    protected void handleSamzaMetric(MetricMessage metricMessage)
    {
        Map<String,Map<String,Object>>metric= metricMessage.getMetrics().getAdditionalProperties();
        if (metric.get("org.apache.samza.system.kafka.KafkaSystemProducerMetrics")!=null) {
            updateKafkaSystemProducerMetric(metricMessage);
        }
        // todo: add org.apache.samza.storage.kv.KeyValueStoreMetrics
        // todo: add org.apache.samza.system.kafka.KafkaSystemConsumerMetrics
        // todo: add org.apache.samza.metrics.JvmMetrics
        // todo: add org.apache.samza.container.TaskInstanceMetrics


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
            Map<String, String> tags = metricGroup.getTags().stream().collect(Collectors.toMap(Tag::getName, Tag::getValue));
            boolean containsNumeric = true;
            Map<String, Object> longFields = metricGroup.getLongFields().stream().collect(Collectors.toMap(LongField::getName, LongField::getValue));
            Map<String, Object> doubleFields = metricGroup.getDoubleFields().stream().collect(Collectors.toMap(DoubleField::getName, DoubleField::getValue));
            Map<String, Object> stringFields = metricGroup.getStringFields().stream().collect(Collectors.toMap(StringField::getName, StringField::getValue));
            Long measurementTime = metricGroup.getMeasurementEpoch();

            Point.Builder pointBuilder = Point.measurement(measurement).time(measurementTime, TimeUnit.SECONDS).useInteger(containsNumeric);
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
