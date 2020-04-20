package fortscale.presidio.output.client.spring;

import fortscale.presidio.output.client.api.AlertsPresidioOutputClient;
import fortscale.presidio.output.client.api.EntitiesPresidioOutputClient;
import fortscale.presidio.output.client.remote.RemoteAlertClientService;
import fortscale.presidio.output.client.remote.RemoteEntityClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!mock-data")
public class PresidioUiOutputRemoteClientConfig {

        @Bean()
        AlertsPresidioOutputClient remoteAlertsClient() {

            return new RemoteAlertClientService();
        }

        @Bean()
        EntitiesPresidioOutputClient remoteUsersClient() {

            return new RemoteEntityClientService();
        }

}
