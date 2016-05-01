package fortscale.monitoring.metricAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.monitoring.metricAdapter.stats.MetricAdapterStats;
import fortscale.utils.influxdb.Exception.InfluxDBNetworkExcpetion;
import fortscale.utils.influxdb.Exception.InfluxDBRuntimeException;
import fortscale.utils.influxdb.InfluxdbClient;
import fortscale.utils.kafka.kafkaMetricsTopicSyncReader.KafkaMetricsTopicSyncReader;
import fortscale.utils.kafka.metricMessageModels.KafkaTopicSyncReaderResponse;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.models.engine.*;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private MetricAdapterStats metricAdapterStats;

    private long metricsAdapterMajorVersion;
    private String dbName;
    private String retentionName;
    private String retentionDuration;
    private String retentionReplication;
    private long waitBetweenWriteRetries;
    private long waitBetweenInitRetries;
    private long waitBetweenReadRetries;
    private String metricName;
    private String metricPackage;

    //kafka reader params:
    private String topicClientId;
    private int topicPartition;

    public MetricAdapter(String topicClientId, int topicPartition, InfluxdbClient influxdbClient, KafkaMetricsTopicSyncReader kafkaMetricsTopicSyncReader, MetricAdapterStats metricAdapterStats, long metricsAdapterMajorVersion, String dbName, String retentionName, String retentionDuration, String retentionReplication, long waitBetweenWriteRetries, long waitBetweenInitRetries, long waitBetweenReadRetries, String metricName, String metricPackage) {
        this.topicClientId = topicClientId;
        this.topicPartition = topicPartition;
        this.influxdbClient = influxdbClient;
        this.kafkaMetricsSyncReader = kafkaMetricsTopicSyncReader;
        this.metricAdapterStats = metricAdapterStats;
        this.dbName = dbName;
        this.retentionName = retentionName;
        this.retentionDuration = retentionDuration;
        this.retentionReplication = retentionReplication;
        this.waitBetweenWriteRetries = waitBetweenWriteRetries;
        this.waitBetweenInitRetries = waitBetweenInitRetries;
        this.waitBetweenReadRetries = waitBetweenReadRetries;
        this.metricName = metricName;
        this.metricPackage = metricPackage;
        this.metricsAdapterMajorVersion = metricsAdapterMajorVersion;
    }

    /**
     * initiating metrics adapter environment (influxdb).
     * forever reads from metrics topic & writes batch to db
     */
    public void process() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        while (true) {
            try {
                init();
                break;
            }
            // in case of init failure, stay in loop and try again
            catch (Exception e) {
                logger.error("failed to initialized influxdb", e);
                sleep(waitBetweenInitRetries);
            }
        }
        while (true) {
            List<KafkaTopicSyncReaderResponse> metricMessages = new ArrayList<>();
            try {
                metricMessages = readMetricsTopic();
            } catch (Exception e) {
                logger.error("failed to read from kafka metrics topic", e);
                sleep(waitBetweenReadRetries);
            }
            if (metricMessages.isEmpty()) {
                //sleep(waitBetweenReadRetries);
                continue;
            }
            BatchPoints batchPoints;
            batchPoints = MetricsMessagesToBatchPoints(metricMessages);

            while (true) {
                try {
                    long amountOfBatchPoints = batchPoints.getPoints().size();
                    if (amountOfBatchPoints > 0) {
                        influxdbClient.batchWrite(batchPoints);
                        metricAdapterStats.add("numberOfWrittenPoints", amountOfBatchPoints);
                        metricAdapterStats.add("numberOfWrittenPointsBytes", batchPoints.toString().length());
                    }
                    break;
                }
                // in case of network failure, stay in loop and try again
                catch (InfluxDBNetworkExcpetion e) {
                    logger.error("Failed to connect influxdb. Exception message", e);
                    sleep(waitBetweenWriteRetries);
                }
                // in case that is diffrent from network failure, drop record and continue
                catch (InfluxDBRuntimeException e) {
                    logger.error("Failed to write influxdb. Exception message: ", e);
                    sleep(waitBetweenWriteRetries);
                    break;
                }
            }
        }

    }

    /**
     * initiating the environment with default values from InfluxDBStatsInit
     */
    protected void init() throws Exception {
        logger.info("Initializing influxdb");
        influxdbClient.createDatabase(dbName);
        influxdbClient.createDBRetention(retentionName, dbName, retentionDuration, retentionReplication);
        logger.info("Finished initializing influxdb");
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
            metricAdapterStats.add("numberOfReadMetricMessages", numberOfReadMetricsMessages);
            metricAdapterStats.add("numberOfReadMetricMessagesBytes", metricMessages.stream().mapToLong(KafkaTopicSyncReaderResponse::getMetricMessageSize).sum());
        }
        return metricMessages;
    }

    /**
     * converts MetricMessages to BatchPoints. (if engine data has valid version and not null)
     * @param metricMessages
     * @return BatchPoints
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    protected BatchPoints MetricsMessagesToBatchPoints(List<KafkaTopicSyncReaderResponse> metricMessages) throws NoSuchFieldException, IllegalAccessException {
        List<Point> points = new ArrayList<>();
        BatchPoints.Builder batchPointsBuilder = BatchPoints.database(dbName);
        logger.debug("converting {} metrics messages to batch points", metricMessages.size());
        for (KafkaTopicSyncReaderResponse metricMessage : metricMessages) {
            Map<String, Object> dataString = metricMessage.getMetricMessage().getMetrics().getAdditionalProperties().get(metricPackage);

            if (dataString == null) //in case of generic samza metric, and not an EngineData metric
                continue;

            EngineData data = null;
            ObjectMapper mapper = new ObjectMapper();

            try {
                data = mapper.readValue(dataString.get(metricName).toString(), EngineData.class);
            } catch (IOException e) {
                logger.error(String.format("Failed to convert message to EngineData object: %s",
                        metricMessage.getMetricMessage().getMetrics().getData()), e.getMessage());
                e.printStackTrace();
            }
            if (data == null) // in case of readValue failure pass to the next message
                continue;
            metricAdapterStats.add("numberOfReadEngineDataMessages", 1);
            metricAdapterStats.add("numberOfReadEngineDataMessagesBytes", metricMessage.getMetricMessageSize());

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
     * @param data
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
