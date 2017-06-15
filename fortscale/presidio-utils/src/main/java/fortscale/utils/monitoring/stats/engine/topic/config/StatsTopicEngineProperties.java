package fortscale.utils.monitoring.stats.engine.topic.config;

import java.util.Properties;

/**
 *
 * Stats Topic engine properties
 *
 * Created by gaashh on 5/2/16.
 */
public class StatsTopicEngineProperties {

    static public Properties getProperties() {

        Properties properties = new Properties();

        // Kafaka topic name
        properties.put("fortscale.monitoring.stats.engine.topic.topicName", "fortscale-metrics");

        // Max number of metric groups to be written at one the Kafka message
        properties.put("fortscale.monitoring.stats.engine.metricGroupBatchWriteSize", 20);

        // Writing a message longer than this value will generate a warning (math: kafaka max msg size / 4 )
        properties.put("fortscale.monitoring.stats.engine.messageSizeWarningThreshold", 256 * 1024);

        return properties;

    }
}

