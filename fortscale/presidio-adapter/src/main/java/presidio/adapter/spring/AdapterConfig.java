package presidio.adapter.spring;


import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.adapter.services.impl.FlumeAdapterExecutionService;
import presidio.adapter.util.AdapterConfigurationUtil;
import presidio.adapter.util.FlumeConfigurationUtil;
import presidio.adapter.util.ProcessExecutor;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;
import presidio.input.sdk.impl.factory.PresidioInputPersistencyServiceFactory;
import presidio.sdk.api.services.PresidioInputPersistencyService;

@Configuration
@Import(value = {ConfigServerClientServiceConfiguration.class, MongoConfig.class})
public class AdapterConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    ConfigurationServerClientService configurationServerClientService;

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${aws.tenant}")
    private String tenant;

    @Value("${aws.account}")
    private String account;

    @Value("${aws.region}")
    private String region;

    @Bean
    public PresidioExecutionService adapterExecutionService() throws Exception {
        PresidioInputPersistencyServiceFactory inputPersistencyServiceFactory = new PresidioInputPersistencyServiceFactory();
        final PresidioInputPersistencyService presidioInputPersistencyService = inputPersistencyServiceFactory.createPresidioInputPersistencyService();
        return new FlumeAdapterExecutionService(new ProcessExecutor(),
                new FlumeConfigurationUtil("adapter", "nw", bucketName, tenant, account, region), new AdapterConfigurationUtil(configurationServerClientService), presidioInputPersistencyService, mongoTemplate);
    }
}
