package presidio.connector.manager.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.connector.manager.impl.services.ConnectorServiceImpl;
import presidio.sdk.api.service.ConnectorService;

/**
 * Init the connector manager
 */
@Configuration
public class ConnectorManagerConfiguration {
    @Bean
    public ConnectorService connectorService() {
        return new ConnectorServiceImpl();
    }
}
