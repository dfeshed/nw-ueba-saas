package fortscale.monitoring.external.stats.collector.impl.linux.config;

import fortscale.monitoring.external.stats.collector.impl.linux.memory.LinuxMemoryCollectorImplService;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Spring configuration class for all Linux collectors
 *
 * Created by gaashh on 6/6/16.
 */
@Configuration
public class LinuxCollectorsServicesImplConfig {

    @Autowired
    StatsService statsService;

    // Common values
    @Value("${fortscale.external.collectors.linux.proc.basepath}")
    String procBasePath;

    // Linux memory collector values
    @Value("${fortscale.external.collectors.linux.memory.disabled}")
    long isLinuxMemoryDisabled;

    @Value("${fortscale.external.collectors.linux.memory.tick.seconds}")
    long linuxMemoryTickPeriodSeconds;

    @Value("${fortscale.external.collectors.linux.memory.slip.warn.seconds}")
    long linuxMemoryTickSlipWarnSeconds;

    /**
     *
     * Linux collector services property object configurer bean
     *
     *
     * @return
     */
    @Bean
    private static PropertySourceConfigurer linuxCollectorsServicesImplpropertyConfigurer() {

        // Get the properties object
        Properties properties = LinuxCollectorsServicesImplProperties.getProperties();

        // Create a configurer bean
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(LinuxCollectorsServicesImplConfig.class, properties);

        return configurer;
    }


    /**
     *
     * Linux memory service bean
     *
     * @return
     */
    @Bean
    public LinuxMemoryCollectorImplService linuxMemoryCollectorImplService() {

        // Disabled?
        if (isLinuxMemoryDisabled != 0) {
            return null;
        }

        // Create it
        boolean isTickThreadEnabled = true;
        LinuxMemoryCollectorImplService service = new LinuxMemoryCollectorImplService(
                statsService, procBasePath,
                isTickThreadEnabled, linuxMemoryTickPeriodSeconds, linuxMemoryTickSlipWarnSeconds);

        return service;

    }

}
