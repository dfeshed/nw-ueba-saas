package presidio.webapp.spring;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.AlertService;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.webapp.controllers.AlertsController;
import presidio.webapp.service.RestAlertServiceImpl;
import presidio.webapp.service.RestAlertService;

@Import({MongoConfig.class, PresidioOutputPersistencyServiceConfig.class})
@Configuration
public class WebConf {

    @Autowired
    AlertService alertService;

    @Bean
    RestAlertService restAlertService(){
        return new RestAlertServiceImpl(alertService);
    }

    @Bean
    AlertsController getAlertsController() {
        return new AlertsController(restAlertService());
    }

}
