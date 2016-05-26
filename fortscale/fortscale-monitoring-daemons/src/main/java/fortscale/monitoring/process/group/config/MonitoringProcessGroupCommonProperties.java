package fortscale.monitoring.process.group.config;

import java.util.Properties;


public class MonitoringProcessGroupCommonProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        // all group pid's folder
        properties.put("group.pid.folder.name","monitoringDaemon");
        return properties;
    }
}
