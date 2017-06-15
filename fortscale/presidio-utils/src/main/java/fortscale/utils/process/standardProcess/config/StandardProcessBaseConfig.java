package fortscale.utils.process.standardProcess.config;

import fortscale.global.configuration.GlobalConfiguration;
import fortscale.utils.monitoring.stats.config.StandardStatsServiceConfig;
import fortscale.utils.process.hostnameService.config.HostnameServiceConfig;
import fortscale.utils.process.metrics.jvm.config.JVMMetricsServiceConfig;
import fortscale.utils.spring.StandardProcessPropertiesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({GlobalConfiguration.class,        // fortscale-config
         StandardStatsServiceConfig.class, // stats service
         HostnameServiceConfig.class,      // Hostname service (required by stats service)
         JVMMetricsServiceConfig.class,    // JVM stats

        })
public class StandardProcessBaseConfig {
        @Bean
        public static StandardProcessPropertiesPlaceholderConfigurer mainStandardProcessPropertiesConfigurer() {

                StandardProcessPropertiesPlaceholderConfigurer configurer= new StandardProcessPropertiesPlaceholderConfigurer();

                return configurer;
        }
}
