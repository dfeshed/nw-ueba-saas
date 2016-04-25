package fortscale.monitoring.metricAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.monitoring.metricAdapter.init.InfluxDBStatsInit;
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
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This process reads metrics from the metrics topic and writes them to the influx DB via the InfluxdbClient
 */
@Configurable(preConstruction = true)
public class MetricAdapter {
    private static final Logger logger = Logger.getLogger(MetricAdapter.class);

    @Autowired
    private InfluxDBStatsInit influxDBStatsInit;
    @Autowired
    private InfluxdbClient influxdbClient;
    @Autowired
    private KafkaTopicSyncReader kafkaTopicSyncReader;
    @Autowired
    private MetricAdapterStats metricAdapterStats;

    @Value("${influxdb.db.name}")
    private String dbName;
    @Value("${influxdb.db.fortscale.batch.flushInterval}")
    private String metricsAdapterVersion;

    public MetricAdapter() {
    }

    /**
     * initiating metrics adapter environment (influxdb).
     * forever reads from metrics topic & writes batch to db
     */
    public void process() {
        while (true) {
            try {
                init();
                break;
            }
            // in case of init failure, stay in loop and try again
            catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        while (true) {
            List<MetricMessage> metricMessages;
            metricMessages = readMetricsTopic();
            if (metricMessages != null) {
                metricAdapterStats.setEventsReadFromMetricsTopic(metricAdapterStats.getEventsReadFromMetricsTopic() + metricMessages.size());
                BatchPoints batchPoints;
                while (true) {
                    try {
                        batchPoints = MetricsMessagesToBatchPoints(metricMessages);
                        metricAdapterStats.setEngineDataEventsReadFromMetricsTopic(metricAdapterStats.getEngineDataEventsReadFromMetricsTopic() + batchPoints.getPoints().size());
                        break;
                    }
                    // while can't read from kafka, continue and try to read again
                    catch (Exception e) {
                        logger.error("Failed to read from metrics topic. Exception message: {}",e.getMessage());
                    }
                }
                while (true) {
                    try {
                        if (batchPoints.getPoints().size() > 0)
                            influxdbClient.write(batchPoints);
                        metricAdapterStats.setEngineDataEventsReadFromMetricsTopic(metricAdapterStats.getEngineDataEventsReadFromMetricsTopic() + batchPoints.getPoints().size());
                        break;
                    }
                    // in case of network failure, stay in loop and try again
                    catch (InfluxDBNetworkExcpetion e) {
                        logger.error("Failed to connect influxdb. Exception message: {} ",e.getMessage());
                    }
                    // in case that is diffrent from network failure, drop record and continue
                    catch (InfluxDBRuntimeException e) {
                        logger.error("Failed to write influxdb. Exception message: {} ",e.getMessage());
                        break;
                    }
                }
            }
        }

    }

    /**
     * initiating the environment with default values from InfluxDBStatsInit
     */
    protected void init() {
        logger.info("Initializing influxdb");
        influxDBStatsInit.init();
        logger.info("Finished initializing influxdb");
    }

    /**
     * reads messages from kafka metrics topic
     *
     * @return list of MetricMessage Pojos from kafka metrics topic
     */
    protected List<MetricMessage> readMetricsTopic() {
        logger.debug("Starts reading from metrics topic");
        List<MetricMessage> metricMessages = kafkaTopicSyncReader.getMessagesAsMetricMessage();
        logger.debug("Read %d messages from metrics topic", metricMessages.size());
        if (metricMessages.isEmpty())
            return null;

        return metricMessages;
    }

    /**
     * converts MetricMessages to BatchPoints. (if engine data has valid version and not null)
     *
     * @param metricMessages
     * @return BatchPoints
     */
    protected BatchPoints MetricsMessagesToBatchPoints(List<MetricMessage> metricMessages) {
        List<Point> points = new ArrayList<>();
        BatchPoints.Builder batchPointsBuilder = BatchPoints.database(dbName);
        logger.debug("converting metrics to batch points");
        for (MetricMessage metricMessage : metricMessages) {
            String dataString = metricMessage.getMetrics().getData();
            if (dataString != null) {
                EngineData data;
                ObjectMapper mapper = new ObjectMapper();
                try {
                    data = mapper.readValue(metricMessage.getMetrics().getData(), EngineData.class);
                } catch (IOException e) {
                    logger.error("Failed to convert message to EngineData object: {}. Exception message: {}.",
                            metricMessage.getMetrics().getData(), e.getMessage());
                    e.printStackTrace();
                    return null;
                }
                if (data.getVersion().equals(metricsAdapterVersion))
                    engineDataToPoints(data).stream().forEach(p -> batchPointsBuilder.point(p));
            }
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
        logger.debug("converting metrics engineData to List<points>");
        for (MetricGroup metricGroup : data.getMetricGroups()) {
            String measurement = metricGroup.getGroupName();
            Map<String, String> tags = metricGroup.getTags().stream().collect(Collectors.toMap(Tag::getName, Tag::getValue));
            boolean containsNumeric = metricGroup.getDoubleFields().size() > 0 || metricGroup.getLongFields().size() > 0;
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
            points.add(pointBuilder.build());
        }
        return points;
    }

}
