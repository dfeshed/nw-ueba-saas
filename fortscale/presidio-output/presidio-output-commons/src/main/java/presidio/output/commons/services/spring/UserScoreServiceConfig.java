package presidio.output.commons.services.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.commons.services.user.UserScoreService;
import presidio.output.commons.services.user.UserScoreServiceImpl;

/**
 * Created by Efrat Noam on 12/4/17.
 */
@Configuration
public class UserScoreServiceConfig {

    @Value("${user.severities.percent.threshold.critical:95}")
    private int percentThresholdCritical;
    @Value("${user.severities.percent.threshold.high:80}")
    private int percentThresholdHigh;
    @Value("${user.severities.percent.threshold.medium:70}")
    private int percentThresholdMedium;

    @Bean
    public UserScoreService userScoreServiceCommon() {
        return new UserScoreServiceImpl(percentThresholdCritical, percentThresholdHigh, percentThresholdMedium);
    }

}
