package presidio.collector.spring;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.collector.services.api.CollectorExecutionService;
import presidio.collector.services.api.FetchService;
import presidio.collector.services.impl.CollectorExecutionServiceImpl;
import presidio.sdk.api.services.CoreManagerSdk;
import presidio.sdk.impl.spring.CoreManagerSdkConfig;

@Configuration
@Import({CoreManagerSdkConfig.class, MongoConfig.class})
public class CollectorConfig {

    @Autowired
    private CoreManagerSdk coreManagerSdk;

    @Autowired
    private FetchService fetchService;

    @Bean
    CollectorExecutionService collectorExecutionService() {
        return new CollectorExecutionServiceImpl(coreManagerSdk, fetchService);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {

        return params -> collectorExecutionService().run(params);
    }


}
