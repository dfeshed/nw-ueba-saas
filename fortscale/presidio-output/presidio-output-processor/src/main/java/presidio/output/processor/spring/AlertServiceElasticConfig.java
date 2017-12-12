package presidio.output.processor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.commons.services.spring.AlertSeverityServiceConfig;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.output.processor.services.alert.AlertClassificationService;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.alert.AlertServiceImpl;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGeneratorFactory;

@Configuration
@Import({
        PresidioOutputPersistencyServiceConfig.class,
        AlertSeverityServiceConfig.class,
        UserServiceConfig.class,
        SupportingInformationServiceConfig.class,
        AlertClassificationPriorityConfig.class
})
public class AlertServiceElasticConfig {
    @Autowired
    private AlertClassificationService alertClassificationService;

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    @Autowired
    private SupportingInformationGeneratorFactory supportingInformationGeneratorFactory;

    @Autowired
    private AlertSeverityService alertSeverityService;

    @Bean
    public AlertService alertService() {
        return new AlertServiceImpl(
                alertPersistencyService,
                alertClassificationService,
                alertSeverityService,
                supportingInformationGeneratorFactory
        );
    }
}
