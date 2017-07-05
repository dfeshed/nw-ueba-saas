package presidio.adapter.spring;

import fortscale.common.shell.PresidioExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.adapter.config.FetchServiceConfig;
import presidio.adapter.services.api.FetchService;
import presidio.adapter.services.impl.AdapterExecutionServiceImpl;
import presidio.sdk.api.services.CoreManagerService;
import presidio.sdk.impl.spring.CoreManagerServiceConfig;

@Configuration
@Import({CoreManagerServiceConfig.class, FetchServiceConfig.class})
public class AdapterConfig {

    @Autowired
    private CoreManagerService coreManagerService;

    @Autowired
    private FetchService fetchService;

    @Bean
    public PresidioExecutionService collectorExecutionService() {
        return new AdapterExecutionServiceImpl(coreManagerService, fetchService);
    }




}
