package presidio.output.processor.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.processor.services.alert.AlertClassificationService;
import presidio.output.processor.services.alert.AlertClassificationServiceImpl;


@Configuration
public class AlertNamingConfig {

    @Value("${indicators.list}")
    private String indicators;
    @Value("${alerts.list}")
    private String classifications;
    @Value("${alerts.names.by.priority}")
    private String classificationsPriority;

    @Bean
    public AlertClassificationService AlertClassificationService() {
        return new AlertClassificationServiceImpl(classifications, indicators, classificationsPriority);
    }
}
