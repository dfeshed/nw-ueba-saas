package fortscale.utils.monitoring.stats.config;

import java.util.Properties;

/**
 *
 * Stats service properties class.
 *
 * Note all stats service variant use this class
 *
 * Created by gaashh on 5/2/16.
 */
public class StatsServiceProperties {

    static public Properties getProperties() {

        Properties properties = new Properties();

        // TODO: ...
        properties.put("fortscale.monitoring.stats.service.xxxx", 7777);

        return properties;

    }
}


