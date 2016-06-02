package fortscale.utils.process.standardProcess.config;

import fortscale.global.configuration.GlobalConfiguration;
import fortscale.utils.monitoring.stats.config.StandardStatsServiceConfig;
import fortscale.utils.process.hostnameService.config.HostnameServiceConfig;
import fortscale.utils.process.metrics.jvm.config.JVMMetricsServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({GlobalConfiguration.class,        // fortscale-config
         StandardStatsServiceConfig.class, // stats service
         HostnameServiceConfig.class,      // Hostname service (required by stats service)
         JVMMetricsServiceConfig.class,    // JVM stats

        })
public class StandardProcessBaseConfig {

}
