package fortscale.monitoring.metrics.adapter.config;

import java.util.Properties;

/**
 * Created by baraks on 5/1/2016.
 */
public class MetricAdapterProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("fortscale.metricadapter.db.name","fortscale");
        properties.put("fortscale.metricadapter.db.fortscale.retention.name","primary_retention");
        properties.put("fortscale.metricadapter.db.fortscale.retention.primary_retention.duration","8w");
        properties.put("fortscale.metricadapter.db.fortscale.retention.primary_retention.replication",1);
        properties.put("fortscale.metricadapter.dbclient.write.sleepBetweenRetries.millis",30*1000);
        properties.put("fortscale.metricadapter.dbclient.init.sleepBetweenRetries.millis",30*1000);
        properties.put("fortscale.metricadapter.kafka.read.sleepBetweenRetries.millis",30*1000);
        properties.put("fortscale.metricadapter.kafka.read.sleepBetweenEmptyMessages.millis",30*1000);
        properties.put("fortscale.metricadapter.version.major",1);
        properties.put("fortscale.metricadapter.kafka.metric.enginedata.name","EngineData");
        properties.put("fortscale.metricadapter.kafka.metric.enginedata.package","fortscale.utils.monitoring.stats.models.engine");
        properties.put("fortscale.metricadapter.initiationwaittime.seconds",60*5*1000);
        return properties;
    }
}
