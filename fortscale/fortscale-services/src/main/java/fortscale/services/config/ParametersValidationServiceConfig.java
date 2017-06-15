package fortscale.services.config;

import fortscale.services.parameters.ParametersServiceImpl;
import fortscale.services.parameters.ParametersValidationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParametersValidationServiceConfig {

    @Bean
    public ParametersValidationService parametersValidationService() {
        return new ParametersServiceImpl();
    }
}
