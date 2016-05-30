package fortscale.monitoring.external.stats.samza.collector.topicReader.config;

import java.util.Properties;


public class SamzaMetricsTopicSyncReaderProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("fortscale.samzametricscollector.topic.metrics.consumer.sockettimeout.milliseconds",30000);
        properties.put("fortscale.samzametricscollector.topic.metrics.consumer.buffersize",102400);
        properties.put("fortscale.samzametricscollector.topic.metrics.consumer.fetchsize",1048576);
        properties.put("fortscale.samzametricscollector.topic.metrics.consumer.clientid","samzaMetricsCollectorClientId");
        properties.put("fortscale.samzametricscollector.topic.metrics.consumer.topic.name","metrics");
        properties.put("fortscale.samzametricscollector.topic.metrics.consumer.partition",0);
        return properties;
    }
}
