package fortscale.monitoring.process.group.config;

import java.util.Properties;

/**
 * Created by baraks on 5/1/2016.
 */
public class MonitoringProcessGroupCommonProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("group.pid.folder.name","monitoringDaemon");
        return properties;
    }
}
