package presidio.output.processor.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.commons.services.alert.AlertSeverityServiceImpl;
import presidio.output.commons.services.alert.AlertSeverityService;


@Configuration
public class AlertEnumsConfig {

    @Value("${severity.critical}")
    private double criticalScore;
    @Value("${severity.high}")
    private double highScore;
    @Value("${severity.mid}")
    private double midScore;
    @Value("${user.score.alert.contribution.low:5}")
    double alertContributionLow;
    @Value("${user.score.alert.contribution.medium:10}")
    double alertContributionMedium;
    @Value("${user.score.alert.contribution.high:15}")
    double alertContributionHigh;
    @Value("${user.score.alert.contribution.critical:20}")
    double alertContributionCritical;
    @Value("${user.severities.percent.threshold.critical:95}")
    private int percentThresholdCritical;
    @Value("${user.severities.percent.threshold.high:80}")
    private int percentThresholdHigh;
    @Value("${user.severities.percent.threshold.medium:70}")
    private int percentThresholdMedium;


    @Bean
    public AlertSeverityService alertEnumsSeverityService() {
        return new AlertSeverityServiceImpl(
                criticalScore,
                highScore,
                midScore,
                alertContributionCritical,
                alertContributionHigh,
                alertContributionMedium,
                alertContributionLow,
                percentThresholdCritical,
                percentThresholdHigh,
                percentThresholdMedium);
    }
}
