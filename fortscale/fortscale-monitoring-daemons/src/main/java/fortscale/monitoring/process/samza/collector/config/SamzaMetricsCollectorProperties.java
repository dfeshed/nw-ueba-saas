package fortscale.monitoring.process.samza.collector.config;

import java.util.Properties;

/**
 * process properties file
 */
public class SamzaMetricsCollectorProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("daemon.pid.file.name","samzaMetricsCollector");
        return properties;
    }
}
