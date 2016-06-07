package fortscale.monitoring.external.stats.collector.impl.linux.config;

import java.util.Properties;

/**
 *
 * Linux collectors services properties class.
 *
 *
 * Created by gaashh on 6/6/16.
 */
public class LinuxCollectorsServicesImplProperties {

    final static long TICK_SECONDS = 60;
    final static long TICK_SLIP_WARN_SECONDS = 30;

    static public Properties getProperties() {

        Properties properties = new Properties();

        // Common
        properties.put("fortscale.external.collectors.linux.proc.basepath", "/proc");

        // Linux memory collector values
        properties.put("fortscale.external.collectors.linux.memory.disabled", 0);
        properties.put("fortscale.external.collectors.linux.memory.tick.seconds", TICK_SECONDS);
        properties.put("fortscale.external.collectors.linux.memory.slip.warn.seconds", TICK_SLIP_WARN_SECONDS);

        return properties;

    }
}


