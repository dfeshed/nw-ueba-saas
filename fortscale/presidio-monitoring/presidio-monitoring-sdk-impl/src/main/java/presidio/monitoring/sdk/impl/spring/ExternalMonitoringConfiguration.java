package presidio.monitoring.sdk.impl.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.impl.services.PresidioExternalMonitoringServiceImpl;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

@Configuration
@Import(PresidioMonitoringConfiguration.class)
public class ExternalMonitoringConfiguration {


    @Bean
    public PresidioExternalMonitoringService PresidioExternalMonitoringService() {
        return new PresidioExternalMonitoringServiceImpl();
    }
}
