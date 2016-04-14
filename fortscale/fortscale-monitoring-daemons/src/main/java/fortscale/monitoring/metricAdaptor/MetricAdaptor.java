package fortscale.monitoring.metricAdaptor;

import fortscale.monitoring.metricAdaptor.init.InfluxDBStatsInit;
import fortscale.utils.influxdb.InfluxdbClient;
import fortscale.utils.kafka.MetricsReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by baraks on 4/12/2016.
 */
public class MetricAdaptor {
    @Autowired
    InfluxDBStatsInit influxDBStatsInit;
    @Autowired
    InfluxdbClient influxdbClient;
    @Autowired
    MetricsReader metricsReader;

    @Value("${broker.list}")
    private String brokerConnection;
    @Value("${zookeeper.connection}")
    private String zookeeperConnection;
    @Value("${zookeeper.timeout}")
    private int zookeeperTimeout;
    int MILLISECONDS_TO_WAIT=10;
    int checkRetries=1;

    public void process()
    {

    }

    public void init()
    {
        influxDBStatsInit.init();
    }
    public void readMetricsTopic()
    {

    }
    public void write()
    {

    }
}
