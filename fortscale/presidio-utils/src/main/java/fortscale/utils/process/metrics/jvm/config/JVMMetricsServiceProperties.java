package fortscale.utils.process.metrics.jvm.config;

import java.util.Properties;

public class JVMMetricsServiceProperties {

    public static Properties getProperties() {
        Properties properties = new Properties();
        properties.put("fortscale.process.jvm.metrics.tick.seconds", 60);
        properties.put("fortscale.process.jvmmetrics.service.disable", 0);
        return properties;
    }
}
