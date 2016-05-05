package fortscale.utils.standardProcess.pidService.config;

import java.util.Properties;

/**
 * Created by baraks on 5/2/2016.
 */
public class PidServiceProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("fortscale.pid.folder.path","/var/run/fortscale");
        return properties;
    }
}
