package presidio.connector.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.connector.manager.impl.services.ConnectorServiceImpl;
import presidio.sdk.api.service.ConnectorService;

import java.util.Map;

/**
 * Created by shays on 22/08/2017.
 */
@Configuration
public class ConnectorManagerConfiguration {
    public static final String PREISIDO_COLLECTOR_HOST_KEY = "preisido.collector.host";
    public static final String PREISIDO_COLLECTOR_PORT_KEY = "preisido.collector.port";
    public static final String PREISIDO_CONNECTOR_MANAGER_APPNAME = "connector-manager";

    @Autowired
    private ConfigurationServerClientService configurationServerClientService;

    @Bean
    ConnectorService connectorService() throws Exception {
        final RestTemplate restTemplate = new RestTemplate();


        Map<String, String> properties = this.configurationServerClientService.readConfigurationAsProperties(PREISIDO_CONNECTOR_MANAGER_APPNAME, null);
        String connectorHostname = properties.get(PREISIDO_COLLECTOR_HOST_KEY);
        String connectorPort = properties.get(PREISIDO_COLLECTOR_PORT_KEY);
        return new ConnectorServiceImpl(restTemplate, connectorHostname, connectorPort);
    }
}
