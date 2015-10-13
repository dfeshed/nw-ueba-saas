package com.rsa.asoc.sa.ui.threat.config;

import com.rsa.asoc.sa.ui.common.BuildInformation;
import com.rsa.asoc.sa.ui.common.CommonBase;
import com.rsa.asoc.sa.ui.threat.convert.DictionaryToIncidentConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Global application configuration.  Define any beans here that don't fit into a more specific config in
 * this package.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
@Configuration
@ComponentScan(basePackageClasses = CommonBase.class)
public class ApplicationConfig {

    @Bean
    public BuildInformation buildInformation() {
        return new BuildInformation();
    }

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new DictionaryToIncidentConverter());
        return conversionService;
    }
}
