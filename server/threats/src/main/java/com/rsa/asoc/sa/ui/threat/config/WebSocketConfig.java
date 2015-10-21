package com.rsa.asoc.sa.ui.threat.config;

import com.rsa.asoc.sa.ui.common.config.BaseWebSocketConfig;
import com.rsa.asoc.sa.ui.common.config.EndpointBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the web socket and STOMP endpoints.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
@Configuration
public class WebSocketConfig extends BaseWebSocketConfig {

    @Bean
    @Override
    protected EndpointBuilder getEndpointBuilder() {
        return new EndpointBuilder("/threats");
    }
}
