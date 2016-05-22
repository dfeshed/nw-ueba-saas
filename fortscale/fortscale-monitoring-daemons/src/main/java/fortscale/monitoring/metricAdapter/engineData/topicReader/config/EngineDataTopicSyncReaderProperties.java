package fortscale.monitoring.metricAdapter.engineData.topicReader.config;

import java.util.Properties;

/**
 * reader properties
 */
public class EngineDataTopicSyncReaderProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("fortscale.topic.metrics.engineData.consumer.sockettimeout.milliseconds",30000);
        properties.put("fortscale.topic.metrics.engineData.consumer.buffersize",102400);
        properties.put("fortscale.topic.metrics.engineData.consumer.fetchsize",1048576);
        properties.put("fortscale.topic.metrics.engineData.consumer.clientid","metricsAdapterEngineDataClient");
        properties.put("fortscale.topic.metrics.engineData.consumer.topic.name","fortscale-metrics");
        properties.put("fortscale.topic.metrics.engineData.consumer.partition",0);
        return properties;
    }
}
