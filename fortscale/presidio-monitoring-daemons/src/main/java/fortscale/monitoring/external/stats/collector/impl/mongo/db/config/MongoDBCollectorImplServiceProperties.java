package fortscale.monitoring.external.stats.collector.impl.mongo.db.config;

import java.util.Properties;

/**
 *
 * mongo db collector service properties class.
 *
 *
 */
public class MongoDBCollectorImplServiceProperties {

    final static long TICK_SECONDS = 60;
    final static long TICK_SLIP_WARN_SECONDS = 30;

    static public Properties getProperties() {

        Properties properties = new Properties();

        // collector values
        properties.put("fortscale.external.collectors.mongo.db.disabled", false);
        properties.put("fortscale.external.collectors.mongo.db.tick.seconds", TICK_SECONDS);
        properties.put("fortscale.external.collectors.mongo.db.slip.warn.seconds", TICK_SLIP_WARN_SECONDS);

        return properties;

    }
}


