package presidio.input.core.spring;


import fortscale.services.config.ParametersValidationServiceConfig;
import fortscale.services.parameters.ParametersValidationService;
import fortscale.utils.mongodb.config.MongoConfig;
import fortscale.utils.shell.service.PresidioExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

@Configuration
@Import({MongoConfig.class, ParametersValidationServiceConfig.class, PresidioInputPersistencyServiceConfig.class})
public class InputCoreConfiguration {

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Autowired
    private ParametersValidationService parametersValidationService;

    @Bean
    public PresidioExecutionService inputExecutionService() {
        return new InputExecutionServiceImpl(parametersValidationService, presidioInputPersistencyService);
    }

}
