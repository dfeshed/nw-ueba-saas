package fortscale.monitoring.external.stats.collector.impl.linux.config;

import fortscale.monitoring.external.stats.collector.impl.linux.core.LinuxCoreCollectorImplService;
import fortscale.monitoring.external.stats.collector.impl.linux.device.LinuxDeviceCollectorImplService;
import fortscale.monitoring.external.stats.collector.impl.linux.fileSystem.LinuxFileSystemCollectorImplService;
import fortscale.monitoring.external.stats.collector.impl.linux.memory.LinuxMemoryCollectorImplService;
import fortscale.monitoring.external.stats.collector.impl.linux.process.LinuxProcessCollectorImplService;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
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

    // Linux disk collector values
    @Value("${fortscale.external.collectors.linux.disk.disabled}")
    boolean isLinuxDiskDisabled;

    @Value("${fortscale.external.collectors.linux.disk.tick.seconds}")
    long linuxDiskTickPeriodSeconds;

    @Value("${fortscale.external.collectors.linux.disk.slip.warn.seconds}")
    long linuxDiskTickSlipWarnSeconds;

    @Value("#{'${fortscale.external.collectors.linux.disk.external.disk.list}'.split(':')}")
    private String[] linuxDiskList;

    // Linux device collector values
    @Value("${fortscale.external.collectors.linux.device.disabled}")
    boolean isLinuxDeviceDisabled;

    @Value("${fortscale.external.collectors.linux.device.tick.seconds}")
    long linuxDeviceTickPeriodSeconds;

    @Value("${fortscale.external.collectors.linux.device.slip.warn.seconds}")
    long linuxDeviceTickSlipWarnSeconds;

    @Value("#{'${fortscale.external.collectors.linux.device.external.device.startswith.exclusion.list}'.split(':')}")
    private  String[] linuxDeviceExclusions;



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




    /**
     *
     * Linux core disk service bean
     *
     * @return
     */
    @Bean
    public LinuxFileSystemCollectorImplService linuxDiskCollectorImplService() {

        // Disabled?
        if (isLinuxDiskDisabled) {
            return null;
        }

        // Create it
        boolean isTickThreadEnabled = true;
        LinuxFileSystemCollectorImplService service = new LinuxFileSystemCollectorImplService(
                statsService, linuxDiskList,
                isTickThreadEnabled, linuxDiskTickPeriodSeconds, linuxDiskTickSlipWarnSeconds);

        return service;

    }

    /**
     *
     * Linux core disk service bean
     *
     * @return
     */
    @Bean
    public LinuxDeviceCollectorImplService linuxDeviceCollectorImplService() {

        // Disabled?
        if (isLinuxDeviceDisabled) {
            return null;
        }

        // Create it
        boolean isTickThreadEnabled = true;
        LinuxDeviceCollectorImplService service = new LinuxDeviceCollectorImplService(
                statsService, linuxDeviceExclusions,
                isTickThreadEnabled, linuxDeviceTickPeriodSeconds, linuxDeviceTickSlipWarnSeconds);

        return service;

    }
}
