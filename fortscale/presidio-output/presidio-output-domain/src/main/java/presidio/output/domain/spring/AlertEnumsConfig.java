package presidio.output.domain.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.domain.services.alerts.AlertEnumsSeverityService;

@Configuration
public class AlertEnumsConfig {

    @Value("${severity.critical}")
    private double criticalScore;
    @Value("${severity.high}")
    private double highScore;
    @Value("${severity.mid}")
    private double midScore;
    @Value("${severity.low}")
    private double lowScore;

    @Bean
    public AlertEnumsSeverityService alertEnumsSeverityService() {
        return new AlertEnumsSeverityService(criticalScore, highScore, midScore, lowScore);
    }
}
