package presidio.output.commons.services.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.commons.services.alert.AlertSeverityServiceImpl;
import presidio.output.commons.services.alert.AlertSeverityService;


@Configuration
public class AlertSeverityServiceConfig {

    @Value("${severity.critical}")
    private double criticalScore;
    @Value("${severity.high}")
    private double highScore;
    @Value("${severity.mid}")
    private double midScore;
    @Value("${entity.score.alert.contribution.low:5}")
    double alertContributionLow;
    @Value("${entity.score.alert.contribution.medium:10}")
    double alertContributionMedium;
    @Value("${entity.score.alert.contribution.high:15}")
    double alertContributionHigh;
    @Value("${entity.score.alert.contribution.critical:20}")
    double alertContributionCritical;

    @Bean
    public AlertSeverityService alertSeverityService() {
        return new AlertSeverityServiceImpl(
                criticalScore,
                highScore,
                midScore,
                alertContributionCritical,
                alertContributionHigh,
                alertContributionMedium,
                alertContributionLow);
    }
}
