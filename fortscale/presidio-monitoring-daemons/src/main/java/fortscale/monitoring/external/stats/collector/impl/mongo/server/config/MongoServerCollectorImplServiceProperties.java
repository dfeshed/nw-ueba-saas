package fortscale.monitoring.external.stats.collector.impl.mongo.server.config;

import java.util.Properties;

/**
 *
 * mongo db collector service properties class.
 *
 *
 */
public class MongoServerCollectorImplServiceProperties {

    final static long TICK_SECONDS = 60;
    final static long TICK_SLIP_WARN_SECONDS = 30;

    static public Properties getProperties() {

        Properties properties = new Properties();

        // collector values
        properties.put("fortscale.external.collectors.mongo.server.disabled", false);
        properties.put("fortscale.external.collectors.mongo.server.tick.seconds", TICK_SECONDS);
        properties.put("fortscale.external.collectors.mongo.server.slip.warn.seconds", TICK_SLIP_WARN_SECONDS);

        return properties;

    }
}


