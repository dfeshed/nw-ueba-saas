package fortscale.monitoring.metricAdapter;

import com.carrotsearch.sizeof.RamUsageEstimator;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.monitoring.metricAdapter.stats.MetricAdapterStats;
import fortscale.utils.influxdb.Exception.InfluxDBNetworkExcpetion;
import fortscale.utils.influxdb.Exception.InfluxDBRuntimeException;
import fortscale.utils.influxdb.InfluxdbClient;
import fortscale.utils.kafka.KafkaTopicSyncReader;
import fortscale.utils.kafka.metricMessageModels.MetricMessage;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.models.engine.*;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

    @Autowired
    private InfluxdbClient influxdbClient;
    @Autowired
    private KafkaTopicSyncReader kafkaTopicSyncReader;
    @Autowired
    private MetricAdapterStats metricAdapterStats;

    @Value("${metricadapter.version.major}")
    private long metricsAdapterMajorVersion;
    @Value("${metricadapter.db.name}")
    private String dbName;
    @Value("${metricadapter.db.fortscale.retention.name}")
    private String retentionName;
    @Value("${metricadapter.db.fortscale.retention.primary_retention.duration}")
    private String retentionDuration;
    @Value("${metricadapter.db.fortscale.retention.primary_retention.replication}")
    private String retentionReplication;
    @Value("#{'${metricadapter.db.write.waitBetweenRetries.seconds}'.concat('000')}")
    private long waitBetweenWriteRetries;
    @Value("#{'${metricadapter.db.init.waitBetweenRetries.seconds}'.concat('000')}")
    private long waitBetweenInitRetries;
    @Value("#{'${metricadapter.kafka.read.waitBetweenRetries.seconds}'.concat('000')}")
    private long waitBetweenReadRetries;



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
                logger.error("failed to initialized influxdb",e);
                sleep(waitBetweenInitRetries);
            }
        }
        while (true) {
            List<MetricMessage> metricMessages = new ArrayList<>();
            try {
                metricMessages = readMetricsTopic();
            }
            catch (Exception e)
            {
                logger.error("failed to read from kafka metrics topic",e);
                sleep(waitBetweenReadRetries);
            }

            if (!metricMessages.isEmpty()) {
                BatchPoints batchPoints;
                batchPoints = MetricsMessagesToBatchPoints(metricMessages);

                while (true) {
                    try {
                        long amountOfBatchPoints = batchPoints.getPoints().size();
                        if (amountOfBatchPoints > 0) {
                            influxdbClient.write(batchPoints);
                            metricAdapterStats.add("numberOfWrittenPoints",amountOfBatchPoints);
                            metricAdapterStats.add("numberOfWrittenPointsBytes",RamUsageEstimator.sizeOf(batchPoints));
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
    protected List<MetricMessage> readMetricsTopic() throws NoSuchFieldException, IllegalAccessException {
        logger.debug("Starts reading from metrics topic");
        List<MetricMessage> metricMessages = kafkaTopicSyncReader.getMessagesAsMetricMessage();
        long numberOfReadMetricsMessages=metricMessages.size();
        logger.debug("Read {} messages from metrics topic", numberOfReadMetricsMessages);
        if (!metricMessages.isEmpty()) {
            metricAdapterStats.add("numberOfReadMetricMessages", numberOfReadMetricsMessages);
            metricAdapterStats.add("numberOfReadMetricMessagesBytes", RamUsageEstimator.sizeOf(metricMessages));
        }
        return metricMessages;
    }

    /**
     * converts MetricMessages to BatchPoints. (if engine data has valid version and not null)
     *
     * @param metricMessages
     * @return BatchPoints
     */
    protected BatchPoints MetricsMessagesToBatchPoints(List<MetricMessage> metricMessages) throws NoSuchFieldException, IllegalAccessException {
        List<Point> points = new ArrayList<>();
        BatchPoints.Builder batchPointsBuilder = BatchPoints.database(dbName);
        logger.debug("converting {} metrics messages to batch points", metricMessages.size());
        for (MetricMessage metricMessage : metricMessages) {
            String dataString = metricMessage.getMetrics().getData();
            if (dataString == null) //in case of generic samza metric, and not an EngineData metric
                continue;
            EngineData data=null;
            ObjectMapper mapper = new ObjectMapper();

            try {
                data = mapper.readValue(metricMessage.getMetrics().getData(), EngineData.class);
            } catch (IOException e) {
                logger.error(String.format("Failed to convert message to EngineData object: %s",
                        metricMessage.getMetrics().getData()), e.getMessage());
                e.printStackTrace();
            }
            if(data ==null) // in case of readValue failure pass to the next message
                continue;
            metricAdapterStats.add("numberOfReadEngineDataMessages",1);
            metricAdapterStats.add("numberOfReadEngineDataMessagesBytes",RamUsageEstimator.sizeOf(data));

            // calculating data major version.
            long version =data.getVersion()/100; //minor version is two last digits

            if (version==metricsAdapterMajorVersion)
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
            logger.debug("converted point: {}",convertedPoint.toString());
            points.add(convertedPoint);
        }
        logger.debug("converted {} metric groups",points.size());
        return points;
    }

}
