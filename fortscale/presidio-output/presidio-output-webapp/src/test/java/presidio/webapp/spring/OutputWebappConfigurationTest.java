package presidio.webapp.spring;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.webapp.controllers.AlertsController;
import presidio.webapp.service.RestAlertService;
import presidio.webapp.service.RestAlertServiceImpl;

@Configuration
public class OutputWebappConfigurationTest {
    @MockBean
    AlertPersistencyService alertService;

    @Bean
    RestAlertService restAlertService() {
        return new RestAlertServiceImpl(alertService);
    }

    @Bean
    AlertsController getAlertsController() {
        return new AlertsController(restAlertService());
    }
}
