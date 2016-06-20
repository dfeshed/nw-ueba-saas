package fortscale.monitoring.external.stats.collector.impl.linux.config;

import org.springframework.beans.factory.annotation.Value;

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
        properties.put("fortscale.external.collectors.linux.memory.disabled",          0);
        properties.put("fortscale.external.collectors.linux.memory.tick.seconds",      TICK_SECONDS);
        properties.put("fortscale.external.collectors.linux.memory.slip.warn.seconds", TICK_SLIP_WARN_SECONDS);

        // Linux processes collector values
        properties.put("fortscale.external.collectors.linux.process.disabled",          0);
        properties.put("fortscale.external.collectors.linux.process.tick.seconds",      TICK_SECONDS);
        properties.put("fortscale.external.collectors.linux.process.slip.warn.seconds", TICK_SLIP_WARN_SECONDS);
        properties.put("fortscale.external.collectors.linux.process.fortscale.pidfiles.dir", "/var/run/fortscale");
        properties.put("fortscale.external.collectors.linux.process.external.pidfiles.list",
                          "/var/run/tomcat.pid:/hadoop/mongodwt/mongod.pid");

        // Linux core collector values
        properties.put("fortscale.external.collectors.linux.core.disabled",          0);
        properties.put("fortscale.external.collectors.linux.core.tick.seconds",      TICK_SECONDS);
        properties.put("fortscale.external.collectors.linux.core.slip.warn.seconds", TICK_SLIP_WARN_SECONDS);

        // linux disk collector values
        properties.put("fortscale.external.collectors.linux.disk.disabled",          false);
        properties.put("fortscale.external.collectors.linux.disk.tick.seconds",      TICK_SECONDS);
        properties.put("fortscale.external.collectors.linux.disk.slip.warn.seconds", TICK_SLIP_WARN_SECONDS);
        properties.put("fortscale.external.collectors.linux.disk.external.disk.list","/hadoop:/home");

        // linux device io collector values
        properties.put("fortscale.external.collectors.linux.device.disabled",          false);
        properties.put("fortscale.external.collectors.linux.device.tick.seconds",      TICK_SECONDS);
        properties.put("fortscale.external.collectors.linux.device.slip.warn.seconds", TICK_SLIP_WARN_SECONDS);
        properties.put("fortscale.external.collectors.linux.device.external.device.startswith.exclusion.list","ram:sr:loop");

        return properties;

    }
}


