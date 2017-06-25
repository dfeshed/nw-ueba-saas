package presidio.collector.spring;

import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.collector.config.FetchServiceConfig;
import presidio.collector.services.api.FetchService;
import presidio.collector.services.impl.CollectorExecutionServiceImpl;
import presidio.sdk.api.services.CoreManagerService;
import presidio.sdk.impl.spring.CoreManagerServiceConfig;

@Configuration
@Import({CoreManagerServiceConfig.class, MongoConfig.class, FetchServiceConfig.class})
public class CollectorConfig {

    @Autowired
    private CoreManagerService coreManagerService;

    @Autowired
    private FetchService fetchService;

    @Bean
    public PresidioExecutionService collectorExecutionService() {
        return new CollectorExecutionServiceImpl(coreManagerService, fetchService);
    }
}
