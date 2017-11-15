package presidio.output.processor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserScoreService;
import presidio.output.processor.services.user.UserScoreServiceImpl;
import presidio.output.processor.services.user.UserService;
import presidio.output.processor.services.user.UserServiceImpl;

/**
 * Created by efratn on 22/08/2017.
 */
@Configuration
//@Import({EventPersistencyServiceConfig.class})
public class UserServiceConfig {

    @Value("${user.severities.batch.size:2000}")
    private int defaultUsersBatchFile;


    @Value("${alerts.batch.size:2000}")
    private int defaultAlertsBatchFile;

    @Value("${alert.affect.duration.days:1000}")
    private int alertEffectiveDurationInDays;

    @Value("${user.severities.percent.threshold.critical:95}")
    private int percentThresholdCritical;

    @Value("${user.severities.percent.threshold.high:80}")
    private int percentThresholdHigh;

    @Value("${user.severities.percent.threshold.medium:70}")
    private int percentThresholdMedium;


    @Value("${user.score.alert.contribution.low:5}")
    double alertContributionLow;
    @Value("${user.score.alert.contribution.medium:10}")
    double alertContributionMedium;
    @Value("${user.score.alert.contribution.high:15}")
    double alertContributionHigh;
    @Value("${user.score.alert.contribution.critical:20}")
    double alertContributionCritical;

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    @Bean
    public UserService userService() {
        return new UserServiceImpl(eventPersistencyService, userPersistencyService, userScoreService(), alertEffectiveDurationInDays,defaultAlertsBatchFile );
    }

    @Bean
    public UserScoreService userScoreService(){
        return new UserScoreServiceImpl(userPersistencyService,alertPersistencyService,defaultUsersBatchFile,defaultAlertsBatchFile,percentThresholdCritical,percentThresholdHigh,percentThresholdMedium
                ,alertContributionCritical,alertContributionHigh,alertContributionMedium,alertContributionLow);
    }

}
