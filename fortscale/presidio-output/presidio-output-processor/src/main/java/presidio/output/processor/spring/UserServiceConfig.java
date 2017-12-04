package presidio.output.processor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.processor.services.user.UserScoreService;
import presidio.output.processor.services.user.UserScoreServiceImpl;
import presidio.output.processor.services.user.UserService;
import presidio.output.processor.services.user.UserServiceImpl;

/**
 * Created by efratn on 22/08/2017.
 */
@Configuration
public class UserServiceConfig {

    @Value("${user.severities.batch.size:2000}")
    private int defaultUsersBatchFile;


    @Value("${alerts.batch.size:2000}")
    private int defaultAlertsBatchFile;

    @Value("${alert.affect.duration.days:1000}")
    private int alertEffectiveDurationInDays;

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Autowired
    private AlertSeverityService alertSeverityService;

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    @Bean
    public presidio.output.commons.services.user.UserScoreService userScoreServiceCommon() {
        return new presidio.output.commons.services.user.UserScoreServiceImpl();
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(eventPersistencyService, userPersistencyService, userScoreService(), userScoreServiceCommon(), alertEffectiveDurationInDays, defaultAlertsBatchFile);
    }

    @Bean
    public UserScoreService userScoreService(){
        return new UserScoreServiceImpl(userPersistencyService,alertPersistencyService, alertSeverityService, userScoreServiceCommon(), defaultUsersBatchFile,defaultAlertsBatchFile);
    }

}
