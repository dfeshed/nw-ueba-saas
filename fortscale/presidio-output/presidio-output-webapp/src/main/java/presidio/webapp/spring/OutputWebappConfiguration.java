package presidio.webapp.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.webapp.controllers.alerts.AlertsApi;
import presidio.webapp.controllers.alerts.AlertsController;
import presidio.webapp.controllers.users.UsersApi;
import presidio.webapp.controllers.users.UsersApiController;
import presidio.webapp.service.RestAlertService;
import presidio.webapp.service.RestAlertServiceImpl;
import presidio.webapp.service.RestUserService;
import presidio.webapp.service.RestUserServiceImpl;

@Import({PresidioOutputPersistencyServiceConfig.class})
@Configuration
public class OutputWebappConfiguration {

    @Autowired
    AlertPersistencyService alertService;

    @Autowired
    UserPersistencyService userService;


    @Bean
    RestAlertService restAlertService() {
        return new RestAlertServiceImpl(alertService);
    }

    @Bean
    RestUserService restUserService() {
        return new RestUserServiceImpl(restAlertService(), userService);
    }

    @Bean
    AlertsApi getAlertsController() {
        return new AlertsController(restAlertService());
    }

    @Bean
    UsersApi getUsersController() {
        return new UsersApiController(restUserService(), restAlertService());
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory =
                new TomcatEmbeddedServletContainerFactory();
        return factory;
    }

}
