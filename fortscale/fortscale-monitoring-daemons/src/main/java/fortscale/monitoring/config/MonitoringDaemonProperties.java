package fortscale.monitoring.config;

import java.util.Properties;

/**
 * Created by baraks on 5/1/2016.
 */
public class MonitoringDaemonProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("daemon.pid.file.name","metricAdapter");
        return properties;
    }
}
