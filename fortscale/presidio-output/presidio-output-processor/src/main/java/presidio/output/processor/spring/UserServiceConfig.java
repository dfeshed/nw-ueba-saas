package presidio.output.processor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.commons.services.spring.UserSeverityServiceConfig;
import presidio.output.commons.services.user.UserSeverityService;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.processor.services.user.*;

/**
 * Created by efratn on 22/08/2017.
 */
@Configuration
@Import(UserSeverityServiceConfig.class)
public class UserServiceConfig {

    @Value("${user.batch.size:2000}")
    private int defaultUsersBatchSize;


    @Value("${alerts.batch.size:2000}")
    private int defaultAlertsBatchSize;

    @Value("${alert.affect.duration.days:1000}")
    private int alertEffectiveDurationInDays;

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Autowired
    private AlertSeverityService alertSeverityService;

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Autowired
    private UserSeverityService userSeverityService;

    @Autowired
    private AlertPersistencyService alertPersistencyService;


    @Bean
    public UserService userService() {
        return new UserServiceImpl(eventPersistencyService, userPersistencyService, alertPersistencyService, userScoreService(), alertEffectiveDurationInDays, defaultAlertsBatchSize);
    }

    @Bean
    public UserScoreService userScoreService(){
        return new UserScoreServiceImpl(userPersistencyService,alertPersistencyService, alertSeverityService, defaultAlertsBatchSize, defaultUsersBatchSize);
    }

}
