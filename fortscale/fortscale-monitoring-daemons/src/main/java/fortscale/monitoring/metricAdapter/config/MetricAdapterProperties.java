package fortscale.monitoring.metricAdapter.config;

import java.util.Properties;

/**
 * Created by baraks on 5/1/2016.
 */
public class MetricAdapterProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("metricadapter.db.name","fortscale");
        properties.put("metricadapter.db.fortscale.retention.name","primary_retention");
        properties.put("metricadapter.db.fortscale.retention.primary_retention.duration","8w");
        properties.put("metricadapter.db.fortscale.retention.primary_retention.replication",1);
        properties.put("metricadapter.db.write.waitBetweenRetries.seconds",30);
        properties.put("metricadapter.db.init.waitBetweenRetries.seconds",30);
        properties.put("metricadapter.kafka.metrics.clientid","metricsAdapterClientId");
        properties.put("metricadapter.kafka.metrics.partition",0);
        properties.put("metricadapter.kafka.read.waitBetweenRetries.seconds",30);
        properties.put("metricadapter.version.major",1);
        properties.put("metricadapter.kafka.metric.enginedata.name","EngineData");
        properties.put("metricadapter.kafka.metric.enginedata.package","fortscale.utils.monitoring.stats.models.engine");
        properties.put("metricadapter.initiationwaittime.seconds",60*5);
        return properties;
    }
}
