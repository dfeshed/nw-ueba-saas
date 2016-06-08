package fortscale.monitoring.external.stats.collector.impl.linux.config;

import fortscale.monitoring.external.stats.collector.impl.linux.core.LinuxCoreCollectorImplService;
import fortscale.monitoring.external.stats.collector.impl.linux.memory.LinuxMemoryCollectorImplService;
import fortscale.monitoring.external.stats.collector.impl.linux.process.LinuxProcessCollectorImplService;
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

    // Linux process collector values
    @Value("${fortscale.external.collectors.linux.process.disabled}")
    long isLinuxProcessDisabled;

    @Value("${fortscale.external.collectors.linux.process.tick.seconds}")
    long linuxProcessTickPeriodSeconds;

    @Value("${fortscale.external.collectors.linux.process.slip.warn.seconds}")
    long linuxProcessTickSlipWarnSeconds;

    @Value("${fortscale.external.collectors.linux.process.fortscale.pidfiles.dir}")
    String fortscaleBasePidfilesPath;

    @Value("${fortscale.external.collectors.linux.process.external.pidfiles.list}")
    String externalProcessesPidfileList;

    // Linux core collector values
    @Value("${fortscale.external.collectors.linux.core.disabled}")
    long isLinuxCoreDisabled;

    @Value("${fortscale.external.collectors.linux.core.tick.seconds}")
    long linuxCoreTickPeriodSeconds;

    @Value("${fortscale.external.collectors.linux.core.slip.warn.seconds}")
    long linuxCoreTickSlipWarnSeconds;

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
     * Linux memory colletor service bean
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

    /**
     *
     * Linux process collector service bean
     *
     * @return
     */
    @Bean
    public LinuxProcessCollectorImplService linuxProcessCollectorImplService() {

        // Disabled?
        if (isLinuxProcessDisabled != 0) {
            return null;
        }

        // Create it
        boolean isTickThreadEnabled = true;
        LinuxProcessCollectorImplService service = new LinuxProcessCollectorImplService(
                statsService, procBasePath,
                isTickThreadEnabled, linuxProcessTickPeriodSeconds, linuxProcessTickSlipWarnSeconds,
                fortscaleBasePidfilesPath, externalProcessesPidfileList);

        return service;

    }

    /**
     *
     * Linux core collector service bean
     *
     * @return
     */
    @Bean
    public LinuxCoreCollectorImplService linuxCoreCollectorImplService() {

        // Disabled?
        if (isLinuxCoreDisabled != 0) {
            return null;
        }

        // Create it
        boolean isTickThreadEnabled = true;
        LinuxCoreCollectorImplService service = new LinuxCoreCollectorImplService(
                statsService, procBasePath,
                isTickThreadEnabled, linuxCoreTickPeriodSeconds, linuxCoreTickSlipWarnSeconds);

        return service;

    }

}
