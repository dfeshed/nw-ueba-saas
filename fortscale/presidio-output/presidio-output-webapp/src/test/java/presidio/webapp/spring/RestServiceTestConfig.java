package presidio.webapp.spring;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.webapp.convertors.MetricConverter;
import presidio.webapp.service.*;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;

@Configuration
public class RestServiceTestConfig {
    @MockBean
    AlertPersistencyService alertService;

    @MockBean
    UserPersistencyService userService;

    @MockBean
    EntityPersistencyService entityService;

    @MockBean
    FeedbackService feedbackService;

    @MockBean
    PresidioMetricPersistencyService presidioMetricPersistencyService;


    @Bean
    RestAlertService restAlertService() {
        return new RestAlertServiceImpl(alertService, feedbackService, 0, 2);
    }

    @Bean
    RestUserService restUserService() {
        return new RestUserServiceImpl(restAlertService(), userService, 0, 100);
    }

    @Bean
    RestEntityService restEntityService() {
        return new RestEntityServiceImpl(restAlertService(), entityService, 0, 100);
    }

    @Bean
    RestMetricsService restMetricsService(){
        MetricConverter metricConvertor = new MetricConverter();
        return new RestMetricServiceImpl(presidioMetricPersistencyService, metricConvertor);
    }

}
