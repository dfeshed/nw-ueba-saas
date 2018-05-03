package fortscale.spring;

import fortscale.common.dataentity.DataEntitiesConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration

public class PresidioUiCommonConfig {


    @Bean
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(false);
        propertySourcesPlaceholderConfigurer.setLocalOverride(true);
        propertySourcesPlaceholderConfigurer.setLocation(new ClassPathResource("META-INF/entities.properties"));
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean(name = "dataEntitiesConfig")
    DataEntitiesConfig dataEntitiesConfig(){
        return new DataEntitiesConfig();
    }



}
