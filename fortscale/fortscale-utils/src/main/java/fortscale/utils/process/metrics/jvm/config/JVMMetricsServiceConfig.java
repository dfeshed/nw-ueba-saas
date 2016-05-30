package fortscale.utils.process.metrics.jvm.config;

import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.process.metrics.jvm.JVMMetricsService;
import fortscale.utils.process.metrics.jvm.impl.JVMMetricsServiceImpl;
import fortscale.utils.process.metrics.jvm.stats.JVMMetrics;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class JVMMetricsServiceConfig {
    @Value("${fortscale.jvm.metrics.tick.seconds}")
    private long tickSeconds;

    @Value("${fortscale.process.name}")
    private String processName;

    @Value("${fortscale.process.pid}")
    private long pid;

    @Autowired
    StatsService statsService;

    @Bean
    public JVMMetricsService jvmMetricsService()
    {
        JVMMetrics jvmMetrics = new JVMMetrics(statsService,processName);
        return new JVMMetricsServiceImpl(jvmMetrics,tickSeconds,pid);
    }

    @Bean
    private static PropertySourceConfigurer JVMMetricsServiceEnvironmentPropertyConfigurer() {
        Properties properties = JVMMetricsServiceProperties.getProperties();

        return new PropertySourceConfigurer(JVMMetricsServiceConfig.class, properties);
    }

}
