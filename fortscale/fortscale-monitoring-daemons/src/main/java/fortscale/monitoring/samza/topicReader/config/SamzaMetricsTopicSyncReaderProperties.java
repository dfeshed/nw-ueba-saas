package fortscale.monitoring.samza.topicReader.config;

import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

/**
 * Created by cloudera on 5/9/16.
 */
public class SamzaMetricsTopicSyncReaderProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("fortscale.samza.topic.metrics.consumer.sockettimeout.milliseconds",30000);
        properties.put("fortscale.samza.topic.metrics.consumer.buffersize",102400);
        properties.put("fortscale.samza.topic.metrics.consumer.fetchsize",1048576);
        properties.put("fortscale.samza.topic.metrics.consumer.clientid","metricsAdapterClientId");
        properties.put("fortscale.samza.topic.metrics.consumer.topic.name","metrics");
        properties.put("fortscale.samza.topic.metrics.consumer.partition",0);
        return properties;
    }
}
