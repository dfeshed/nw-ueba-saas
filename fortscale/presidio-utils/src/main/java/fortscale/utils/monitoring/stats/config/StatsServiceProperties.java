package fortscale.utils.monitoring.stats.config;

import org.springframework.beans.factory.annotation.Value;

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

        // See StatsServiceImpl ctor documentation
        properties.put("fortscale.monitoring.stats.service.tick.seconds", 5);

        properties.put("fortscale.monitoring.stats.service.periodicMetricsUpdate.seconds", 60);
        properties.put("fortscale.monitoring.stats.service.periodicMetricsUpdate.slip",    30);

        properties.put("fortscale.monitoring.stats.service.periodicMetricsUpdate.seconds.samza", 60);
        properties.put("fortscale.monitoring.stats.service.periodicMetricsUpdate.slip.samza",    30);

        properties.put("fortscale.monitoring.stats.service.enginePush.seconds", 60);
        properties.put("fortscale.monitoring.stats.service.enginePush.slip",    30);

        properties.put("fortscale.monitoring.stats.service.disable",       0);
        properties.put("fortscale.monitoring.stats.service.disable.samza", 0);

        return properties;

    }
}


