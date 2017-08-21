package presidio.output.domain.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.domain.services.alerts.AlertNamingService;

@Configuration
public class AlertNamingConfig {

    @Value("${indicators.list}")
    private String indicators;
    @Value("${alerts.list}")
    private String alerts;
    @Value("${alerts.names.by.priority}")
    private String alertsPriority;

    @Bean
    public AlertNamingService alertNamingService() {
        return new AlertNamingService(alerts, indicators, alertsPriority);
    }
}
