package presidio.collector.spring;

import fortscale.services.config.ParametersValidationServiceConfig;
import fortscale.services.parameters.ParametersValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.collector.config.FetchServiceConfig;
import presidio.collector.services.api.CollectorExecutionService;
import presidio.collector.services.api.FetchService;
import presidio.collector.services.impl.CollectorExecutionServiceImpl;
import presidio.sdk.api.services.CoreManagerService;
import presidio.sdk.impl.spring.CoreManagerServiceConfig;

@Configuration
@Import({CoreManagerServiceConfig.class, FetchServiceConfig.class, ParametersValidationServiceConfig.class})
public class CollectorConfig {

    @Autowired
    private CoreManagerService coreManagerService;

    @Autowired
    private FetchService fetchService;

    @Autowired
    private ParametersValidationService parametersValidationService;

    @Bean
    public CollectorExecutionService collectorExecutionService() {
        return new CollectorExecutionServiceImpl(coreManagerService, fetchService, parametersValidationService);
    }




}
