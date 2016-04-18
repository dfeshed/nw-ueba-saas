package fortscale.monitoring.metricAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.monitoring.metricAdapter.init.InfluxDBStatsInit;
import fortscale.services.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.influxdb.InfluxdbClient;
import fortscale.utils.kafka.KafkaTopicSyncReader;
import fortscale.utils.kafka.metricMessageModels.MetricMessage;
import fortscale.utils.logging.Logger;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by baraks on 4/12/2016.
 */
public class MetricAdapter {
    private static final Logger logger = Logger.getLogger(MetricAdapter.class);

    @Autowired
    InfluxDBStatsInit influxDBStatsInit;
    @Autowired
    InfluxdbClient influxdbClient;
    @Autowired
    KafkaTopicSyncReader kafkaTopicSyncReader;

    @Value("${broker.list}")
    private String brokerConnection;
    @Value("${zookeeper.connection}")
    private String zookeeperConnection;
    @Value("${zookeeper.timeout}")
    private int zookeeperTimeout;
    int MILLISECONDS_TO_WAIT=10;
    int checkRetries=1;

    private static final String SPRING_CONTEXT_FILE_PATH = "classpath*:META-INF/spring/monitoring-external-stats-collector-context.xml";
    private ClassPathXmlApplicationContext context;

    public void process()
    {

    }

    public void init()
    {
        logger.info("Loading spring context..");
        context = new ClassPathXmlApplicationContext(SPRING_CONTEXT_FILE_PATH);
        logger.info("Finished loading spring context");

        logger.info("Initializing influxdb");
        influxDBStatsInit.init();
        logger.info("Finished initializing influxdb");
    }
    public void readMetricsTopic()
    {
        List<MetricMessage> metricMessages = new ArrayList<>();
        metricMessages.addAll(kafkaTopicSyncReader.getMessagesAsMetricMessage());

    }

    public static BatchPoints MetricsMessagesToBatchPoints(List<MetricMessage> metricMessages)
    {
        List<Point> points= new ArrayList<>();
        for(MetricMessage metricMessage : metricMessages)
        {
            ObjectMapper mapper = new ObjectMapper();
            try {
                StatsEngineMetricsGroupData data = mapper.readValue(metricMessage.getMetrics().getData(), StatsEngineMetricsGroupData.class);
                points.add(StatsEngineMetricsGroupDataToPoint(data));

            } catch (IOException e) {
                logger.error("Failed to convert message to MetricMessage object: {}. Exception message: {}.",
                        metricMessage.getMetrics().getData(), e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static Point StatsEngineMetricsGroupDataToPoint(StatsEngineMetricsGroupData data)
    {
//        String mesurment=data.getGroupName();
////        Map<String,String> tags = data.getMetricsTags();
//        Point result= Point.measurement(mesurment).tag(tags).useInteger(true).time(data.getMeasurementEpoch(), TimeUnit.SECONDS).field("a","b").build();
        return null;
    }

    public void write(BatchPoints batchPoints)
    {
        influxdbClient.write(batchPoints);
    }
}
