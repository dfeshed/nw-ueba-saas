package fortscale.monitoring.metricAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.monitoring.metricAdapter.init.InfluxDBStatsInit;
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
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Configurable(preConstruction=true)
public class MetricAdapter {
    private static final Logger logger = Logger.getLogger(MetricAdapter.class);

    @Autowired
    InfluxDBStatsInit influxDBStatsInit;
    @Autowired
    InfluxdbClient influxdbClient;
    @Autowired
    KafkaTopicSyncReader kafkaTopicSyncReader;

    @Value("${influxdb.db.name}")
    static String dbName;
    @Value("${broker.list}")
    private String brokerConnection;
    @Value("${zookeeper.connection}")
    private String zookeeperConnection;
    @Value("${zookeeper.timeout}")
    private int zookeeperTimeout;
    @Value("${metricsadapter.version}")
    private static String metricsAdapterVersion;

    int MILLISECONDS_TO_WAIT = 10;
    int checkRetries = 1;

    private static final String SPRING_CONTEXT_FILE_PATH = "classpath*:META-INF/spring/monitoring-metric-adapter-context.xml";
    private ClassPathXmlApplicationContext context;

    public MetricAdapter(){}
    public void process() {
        init();
        while(true)
        {
            List<MetricMessage> metricMessages = new ArrayList<>();
            metricMessages = readMetricsTopic();
            if(metricMessages!=null)
                MetricsMessagesToBatchPoints(metricMessages);
        }
    }

    public void init() {
        logger.info("Initializing influxdb");
        influxDBStatsInit.init();
        logger.info("Finished initializing influxdb");
    }

    public List<MetricMessage> readMetricsTopic() {
        List<MetricMessage> metricMessages = kafkaTopicSyncReader.getMessagesAsMetricMessage();
        return metricMessages;
    }


    public BatchPoints MetricsMessagesToBatchPoints(List<MetricMessage> metricMessages) {
        List<Point> points = new ArrayList<>();
        for (MetricMessage metricMessage : metricMessages) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                EngineData data = mapper.readValue(metricMessage.getMetrics().getData(), EngineData.class);
                if (data.getVersion().equals(metricsAdapterVersion))
                    points.addAll(engineDataToPoints(data));
            } catch (IOException e) {
                logger.error("Failed to convert message to EngineData object: {}. Exception message: {}.",
                        metricMessage.getMetrics().getData(), e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        return BatchPoints.database(dbName).points((Point[]) points.toArray()).build();
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

    public void write(BatchPoints batchPoints) {
        influxdbClient.write(batchPoints);
    }
}
