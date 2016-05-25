package fortscale.utils.process.metrics.jvm.config;

import fortscale.utils.process.metrics.jvm.JVMMetricsService;
import fortscale.utils.process.metrics.jvm.impl.JVMMetricsServiceImpl;
import fortscale.utils.process.metrics.jvm.stats.JVMMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.process.pidService.PidService;
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

    @Autowired
    StatsService statsService;

    @Bean
    public JVMMetricsService jvmMetricsService()
    {
        JVMMetrics jvmMetrics = new JVMMetrics(statsService,Long.toString(PidService.getCurrentPid()));
        return new JVMMetricsServiceImpl(jvmMetrics,tickSeconds);
    }

    @Bean
    private static PropertySourceConfigurer JVMMetricsServiceEnvironmentPropertyConfigurer() {
        Properties properties = JVMMetricsServiceProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(JVMMetricsServiceConfig.class, properties);

        return configurer;
    }

}
