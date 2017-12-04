package presidio.webapp.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.commons.services.alert.FeedbackService;
import presidio.output.commons.services.alert.FeedbackServiceImpl;
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
public class OutputWebappTestConfiguration {

    @Autowired
    AlertPersistencyService alertService;

    @Autowired
    UserPersistencyService userService;


    @Bean
    FeedbackService feedbackService() {
        return new FeedbackServiceImpl();
    }

    @Bean
    RestAlertService restAlertService() {
        return new RestAlertServiceImpl(alertService, feedbackService(), pageNumberAlert, pageSizeAlert);
    }

    @Value("${default.page.size.for.rest.user}")
    private int pageSizeUser;

    @Value("${default.page.number.for.rest.user}")
    private int pageNumberUser;

    @Value("${default.page.size.for.rest.alert}")
    private int pageSizeAlert;

    @Value("${default.page.number.for.rest.alert}")
    private int pageNumberAlert;

    @Bean
    RestUserService restUserService() {
        return new RestUserServiceImpl(restAlertService(), userService, pageSizeUser, pageNumberUser);
    }

    @Bean
    AlertsApi getAlertsController() {
        return new AlertsController(restAlertService());
    }

    @Bean
    UsersApi getUsersController() {
        return new UsersApiController(restUserService());
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory =
                new TomcatEmbeddedServletContainerFactory();
        return factory;
    }

}
