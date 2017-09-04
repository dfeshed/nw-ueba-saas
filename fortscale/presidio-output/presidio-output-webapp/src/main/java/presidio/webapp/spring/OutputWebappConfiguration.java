package presidio.webapp.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.webapp.controllers.alerts.AlertsControllerManualCreated;
import presidio.webapp.service.RestAlertService;
import presidio.webapp.service.RestAlertServiceImpl;

@Import({PresidioOutputPersistencyServiceConfig.class})
@Configuration
public class OutputWebappConfiguration {

    @Autowired
    AlertPersistencyService alertService;

    @Bean
    RestAlertService restAlertService() {
        return new RestAlertServiceImpl(alertService);
    }

    @Bean
    AlertsControllerManualCreated getAlertsController() {
        return new AlertsControllerManualCreated(restAlertService());
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory =
                new TomcatEmbeddedServletContainerFactory();
        return factory;
    }

}
