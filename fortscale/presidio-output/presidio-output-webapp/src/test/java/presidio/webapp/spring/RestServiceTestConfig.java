package presidio.webapp.spring;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.commons.services.alert.FeedbackService;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.webapp.service.RestAlertService;
import presidio.webapp.service.RestAlertServiceImpl;
import presidio.webapp.service.RestUserService;
import presidio.webapp.service.RestUserServiceImpl;

@Configuration
public class RestServiceTestConfig {
    @MockBean
    AlertPersistencyService alertService;

    @MockBean
    UserPersistencyService userService;

    @MockBean
    FeedbackService feedbackService;

    @Bean
    RestAlertService restAlertService() {
        return new RestAlertServiceImpl(alertService, feedbackService, 0, 2);
    }

    @Bean
    RestUserService restUserService() {
        return new RestUserServiceImpl(restAlertService(), userService, 0, 100);
    }

}
