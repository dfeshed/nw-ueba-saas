package presidio.output.processor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.output.processor.services.alert.*;

/**
 * Created by efratn on 24/07/2017.
 */
@Configuration
@Import({PresidioOutputPersistencyServiceConfig.class, AlertEnumsConfig.class, AlertNamingConfig.class})
public class AlertServiceElasticConfig {

    @Autowired
    private AlertEnumsSeverityService alertEnumsSeverityService;

    @Autowired
    private AlertClassificationService alertClassificationService;

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    @Bean
    public AlertService alertService() {
        return new AlertServiceImpl(alertPersistencyService, alertEnumsSeverityService, alertClassificationService);
    }
}
