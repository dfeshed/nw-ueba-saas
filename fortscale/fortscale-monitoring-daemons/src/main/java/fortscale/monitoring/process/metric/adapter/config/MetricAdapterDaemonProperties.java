package fortscale.monitoring.process.metric.adapter.config;

import java.util.Properties;

public class MetricAdapterDaemonProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("daemon.process.name","metricAdapter");
        properties.put("daemon.pid.file.name","metricAdapter");
        return properties;
    }
}
