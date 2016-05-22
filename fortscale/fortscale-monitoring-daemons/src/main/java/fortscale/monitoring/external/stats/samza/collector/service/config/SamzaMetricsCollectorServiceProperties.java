package fortscale.monitoring.external.stats.samza.collector.service.config;

import java.util.Properties;

/**
 * collector service properties
 */
public class SamzaMetricsCollectorServiceProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("fortscale.samzametricscollector.kafka.read.sleepBetweenRetries.millis",30*1000);
        properties.put("fortscale.samzametricscollector.kafka.read.sleepBetweenEmptyMessages.millis",30*1000);
        return properties;
    }
}
