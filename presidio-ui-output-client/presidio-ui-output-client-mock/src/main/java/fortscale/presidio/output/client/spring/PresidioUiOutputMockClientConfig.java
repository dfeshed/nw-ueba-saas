package fortscale.presidio.output.client.spring;

import fortscale.presidio.output.client.api.AlertsPresidioOutputClient;
import fortscale.presidio.output.client.api.EntitiesPresidioOutputClient;
import fortscale.presidio.output.client.mock.FakeRemoteAlertClientService;
import fortscale.presidio.output.client.mock.FakeRemoteUserClientService;
import fortscale.presidio.output.client.mock.creators.FakeAlertsCreator;
import fortscale.presidio.output.client.mock.creators.FakeCreatorUtils;
import fortscale.presidio.output.client.mock.creators.FakeEntitiesCreator;
import fortscale.presidio.output.client.mock.creators.FakeIndicatorCreators;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("mock-data")
public class PresidioUiOutputMockClientConfig {

        @Bean()
        FakeAlertsCreator fakeAlertsCreator() {

            return new FakeAlertsCreator(fakeCreatorUtils(),fakeIndicatorCreators());
        }

        @Bean()
        FakeEntitiesCreator fakeUsersCreator() {

            return new FakeEntitiesCreator(fakeCreatorUtils(),fakeAlertsCreator());
        }

        @Bean()
        FakeIndicatorCreators fakeIndicatorCreators() {

            return new FakeIndicatorCreators(fakeCreatorUtils());
        }

        @Bean()
        FakeCreatorUtils fakeCreatorUtils() {

            return new FakeCreatorUtils();
        }


        @Bean()
        AlertsPresidioOutputClient remoteAlertsClient() {

            return new FakeRemoteAlertClientService(fakeAlertsCreator(),fakeIndicatorCreators());
        }

        @Bean()
        EntitiesPresidioOutputClient remoteUsersClient() {

            return new FakeRemoteUserClientService(fakeAlertsCreator(),fakeUsersCreator());
        }


}
