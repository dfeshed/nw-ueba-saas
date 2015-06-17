package com.rsa.asoc.sa.ui.investigation.config;

import com.rsa.asoc.sa.ui.common.BuildInformation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Global application configuration.  Define any beans here that don't fit into a more specific config in
 * this package.
 *
 * @author athielke
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public BuildInformation buildInformation() {
        return new BuildInformation();
    }
}
