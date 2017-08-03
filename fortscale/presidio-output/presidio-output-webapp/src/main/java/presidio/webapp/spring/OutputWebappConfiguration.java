package presidio.webapp.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.AlertPersistencyService;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.webapp.controllers.AlertsController;
import presidio.webapp.service.RestAlertService;
import presidio.webapp.service.RestAlertServiceImpl;

@Import({PresidioOutputPersistencyServiceConfig.class})
@Configuration
public class OutputWebappConfiguration {

    @Autowired
    AlertPersistencyService alertService;

    @Bean
    RestAlertService restAlertService(){
        return new RestAlertServiceImpl(alertService);
    }

    @Bean
    AlertsController getAlertsController() {
        return new AlertsController(restAlertService());
    }

}
