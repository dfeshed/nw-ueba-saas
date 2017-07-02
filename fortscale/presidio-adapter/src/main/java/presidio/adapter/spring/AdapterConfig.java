package presidio.adapter.spring;

import fortscale.services.config.ParametersValidationServiceConfig;
import fortscale.services.parameters.ParametersValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.adapter.config.FetchServiceConfig;
import presidio.adapter.services.api.AdapterExecutionService;
import presidio.adapter.services.api.FetchService;
import presidio.adapter.services.impl.AdapterExecutionServiceImpl;
import presidio.sdk.api.services.CoreManagerService;
import presidio.sdk.impl.spring.CoreManagerServiceConfig;

@Configuration
@Import({CoreManagerServiceConfig.class, FetchServiceConfig.class, ParametersValidationServiceConfig.class})
public class AdapterConfig {

    @Autowired
    private CoreManagerService coreManagerService;

    @Autowired
    private FetchService fetchService;

    @Autowired
    private ParametersValidationService parametersValidationService;

    @Bean
    public AdapterExecutionService collectorExecutionService() {
        return new AdapterExecutionServiceImpl(coreManagerService, fetchService, parametersValidationService);
    }




}
