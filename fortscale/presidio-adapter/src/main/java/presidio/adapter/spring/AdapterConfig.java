package presidio.adapter.spring;


import fortscale.common.shell.PresidioExecutionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.adapter.services.impl.FlumeAdapterExecutionService;
import presidio.adapter.util.FlumeConfigurationUtil;
import presidio.adapter.util.ProcessExecutor;
import presidio.input.sdk.impl.factory.PresidioInputPersistencyServiceFactory;
import presidio.sdk.api.services.PresidioInputPersistencyService;

@Configuration
public class AdapterConfig {


    @Bean
    public PresidioExecutionService adapterExecutionService() throws Exception {
        PresidioInputPersistencyServiceFactory inputPersistencyServiceFactory = new PresidioInputPersistencyServiceFactory();
        final PresidioInputPersistencyService presidioInputPersistencyService = inputPersistencyServiceFactory.createPresidioInputPersistencyService();
        return new FlumeAdapterExecutionService(new ProcessExecutor(), new FlumeConfigurationUtil("adapter", "ca"), presidioInputPersistencyService);
    }
}
