package fortscale.utils.process.metrics.jvm.config;

import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class JVMMetricsServiceProperties {


    public static Properties getProperties() {
        Properties properties = new Properties();
        properties.put("fortscale.process.jvm.metrics.tick.seconds",60);
        return properties;
    }
}
