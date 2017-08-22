package presidio.output.processor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.processor.services.alert.AlertEnumsSeverityService;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.alert.AlertServiceImpl;

/**
 * Created by efratn on 24/07/2017.
 */
@Configuration
@Import(AlertEnumsConfig.class)
public class AlertServiceElasticConfig {

    @Autowired
    private AlertEnumsSeverityService alertEnumsSeverityService;

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    @Bean
    public AlertService alertService() {
        return new AlertServiceImpl(alertPersistencyService, alertEnumsSeverityService);
    }
}
