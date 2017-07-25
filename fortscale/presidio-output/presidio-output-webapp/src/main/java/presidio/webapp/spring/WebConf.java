package presidio.webapp.spring;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.AlertService;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.webapp.controllers.AlertsController;
import presidio.webapp.service.AlertServiceImpl;

@Import({MongoConfig.class, PresidioOutputPersistencyServiceConfig.class})
public class WebConf {

    @Autowired
    AlertService elasticAlertService;

    @Bean
    presidio.webapp.service.AlertService alertService(){
        return new AlertServiceImpl(elasticAlertService);
    }

    @Bean
    AlertsController getAlertsController() {
        return new AlertsController(alertService());
    }

}
