package fortscale.utils.process.metrics.jvm.config;

import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.process.metrics.jvm.JVMMetricsService;
import fortscale.utils.process.metrics.jvm.impl.JVMMetricsServiceImpl;
import fortscale.utils.process.metrics.jvm.stats.JVMMetrics;
import fortscale.utils.process.processType.ProcessType;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class JVMMetricsServiceConfig {
    @Value("${fortscale.process.jvm.metrics.tick.seconds}")
    private long tickSeconds;

    @Value("${fortscale.process.pid:0}")  // Default is required if some (test) does not set it
    private long pid;

    @Value("${fortscale.process.type}")
    private ProcessType processType;

    @Value("${fortscale.process.jvmmetrics.service.disable}")
    private long disable;


    @Autowired
    StatsService statsService;

    @Bean
    public JVMMetricsService jvmMetricsService()
    {
        // Disabled?
        if (disable != 0) {
            return null;
        }

        return new JVMMetricsServiceImpl(statsService,tickSeconds,pid,processType);
    }

    @Bean
    private static PropertySourceConfigurer JVMMetricsServiceEnvironmentPropertyConfigurer() {
        Properties properties = JVMMetricsServiceProperties.getProperties();

        return new PropertySourceConfigurer(JVMMetricsServiceConfig.class, properties);
    }

}
