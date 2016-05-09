package fortscale.monitoring.grafana.init.config;


import fortscale.monitoring.grafana.init.GrafanaInit;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class GrafanaInitConfig {

    @Value("${grafana.db.source.file.path}")
    private String dbSourceFilePath;
    @Value("${grafana.db.destination.file.path}")
    private String dbDestinationFilePath;

    @Bean
    GrafanaInit grafanaInit() {
        return new GrafanaInit(dbSourceFilePath, dbDestinationFilePath);
    }

    @Bean
    private static PropertySourceConfigurer grafanaInitEnvironmentPropertyConfigurer() {
        Properties properties = GrafanaInitProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(GrafanaInitConfig.class, properties);

        return configurer;
    }


}
