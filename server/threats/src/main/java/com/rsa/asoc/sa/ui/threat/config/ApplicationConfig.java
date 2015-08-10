package com.rsa.asoc.sa.ui.threat.config;

import com.rsa.asoc.sa.ui.common.BuildInformation;
import com.rsa.asoc.sa.ui.common.config.SecurityConfig;
import com.rsa.asoc.sa.ui.common.config.WebConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Global application configuration.  Define any beans here that don't fit into a more specific config in
 * this package.
 *
 * @author athielke
 */
@Configuration
@Import({ WebConfig.class, SecurityConfig.class })
public class ApplicationConfig {

    @Bean
    public BuildInformation buildInformation() {
        return new BuildInformation();
    }
}
