package presidio.webapp.spring;

import fortscale.utils.mongodb.config.MongoConfig;
import fortscale.utils.rest.HttpMethodOverrideHeaderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.output.commons.services.spring.AlertSeverityServiceConfig;
import presidio.output.commons.services.spring.EntitySeverityServiceConfig;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.webapp.controllers.alerts.AlertsApi;
import presidio.webapp.controllers.alerts.AlertsController;
import presidio.webapp.controllers.licensing.DailyMetricsApi;
import presidio.webapp.controllers.licensing.DailyMetricsController;
import presidio.webapp.controllers.users.UsersApi;
import presidio.webapp.controllers.users.UsersApiController;
import presidio.webapp.convertors.MetricConverter;
import presidio.webapp.service.*;

@Import({PresidioOutputPersistencyServiceConfig.class, AlertSeverityServiceConfig.class, EntitySeverityServiceConfig.class, MongoConfig.class})
@Configuration
public class OutputWebappConfiguration {

    @Autowired
    private AlertPersistencyService alertService;

    @Autowired
    private UserPersistencyService userService;

    @Autowired
    private  PresidioMetricPersistencyService presidioMetricPersistencyService;




    @Bean
    FeedbackService feedbackService() {
        return new FeedbackServiceImpl();
    }

    @Bean
    RestAlertService restAlertService() {
        return new RestAlertServiceImpl(alertService, feedbackService(), pageNumberAlert, pageSizeAlert);
    }


    @Bean
    MetricConverter metricConvertor(){
        return new MetricConverter();
    }

    @Bean
    RestMetricsService restMetricsService() {
        return new RestMetricServiceImpl(presidioMetricPersistencyService,metricConvertor());
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
    DailyMetricsApi getPresidioMetricsController() {
        return new DailyMetricsController(restMetricsService());
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

    @Bean
    public HttpMethodOverrideHeaderFilter overrideHeaderFilter() {
        HttpMethodOverrideHeaderFilter filter = new HttpMethodOverrideHeaderFilter();
        return filter;
    }

}