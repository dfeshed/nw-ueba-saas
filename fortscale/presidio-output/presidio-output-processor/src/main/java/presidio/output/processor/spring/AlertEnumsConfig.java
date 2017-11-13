package presidio.output.processor.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.processor.services.alert.AlertEnumsSeverityService;


@Configuration
public class AlertEnumsConfig {

    @Value("${severity.critical}")
    private double criticalScore;
    @Value("${severity.high}")
    private double highScore;
    @Value("${severity.mid}")
    private double midScore;


    @Bean
    public AlertEnumsSeverityService alertEnumsSeverityService() {
        return new AlertEnumsSeverityService(criticalScore, highScore, midScore);
    }
}
