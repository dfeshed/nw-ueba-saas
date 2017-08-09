package fortscale.monitoring.external.stats.collector.impl.mongo.collection.config;

import java.util.Properties;

/**
 *
 * mongo collection collector service properties class.
 *
 *
 */
public class MongoCollectionCollectorImplServiceProperties {

    final static long TICK_SECONDS = 60;
    final static long TICK_SLIP_WARN_SECONDS = 30;

    static public Properties getProperties() {

        Properties properties = new Properties();

        // collector values
        properties.put("fortscale.external.collectors.mongo.collection.disabled", false);
        properties.put("fortscale.external.collectors.mongo.collection.tick.seconds", TICK_SECONDS);
        properties.put("fortscale.external.collectors.mongo.collection.slip.warn.seconds", TICK_SLIP_WARN_SECONDS);

        return properties;

    }
}


