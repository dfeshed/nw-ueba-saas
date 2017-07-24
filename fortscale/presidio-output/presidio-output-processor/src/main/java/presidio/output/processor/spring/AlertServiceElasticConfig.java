package presidio.output.processor.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.alert.AlertServiceImpl;

/**
 * Created by efratn on 24/07/2017.
 */
@Configuration
public class AlertServiceElasticConfig {

    @Bean
    public AlertService alertService() {
        return new AlertServiceImpl();
    }
}
