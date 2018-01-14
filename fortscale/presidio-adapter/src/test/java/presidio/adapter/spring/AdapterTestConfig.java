package presidio.adapter.spring;

import fortscale.common.shell.PresidioExecutionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.adapter.services.impl.FlumeAdapterExecutionService;
import presidio.adapter.util.AdapterConfigurationUtil;
import presidio.adapter.util.FlumeConfigurationUtil;
import presidio.adapter.util.ProcessExecutor;
import presidio.input.sdk.impl.factory.PresidioInputPersistencyServiceFactory;
import presidio.sdk.api.services.PresidioInputPersistencyService;

/**
 * Created by shays on 26/06/2017.
 */
@Configuration
public class AdapterTestConfig {

    private AdapterConfigurationUtil mockAdapterConfigurationUtil;
    private MongoTemplate mockedMongoTemplate;

    @Bean
    public PresidioExecutionService adapterExecutionService() throws Exception {
        PresidioInputPersistencyServiceFactory inputPersistencyServiceFactory = new PresidioInputPersistencyServiceFactory();
        final PresidioInputPersistencyService presidioInputPersistencyService = inputPersistencyServiceFactory.createPresidioInputPersistencyServiceForTests();
        return new FlumeAdapterExecutionService(new ProcessExecutor(), new FlumeConfigurationUtil("adapter", "ca"), mockAdapterConfigurationUtil, presidioInputPersistencyService, mockedMongoTemplate);
    }

}
