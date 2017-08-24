package presidio.connector.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.config.server.client.ConfigurationServerClientService;
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
