package presidio.adapter.spring;


import fortscale.common.shell.PresidioExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.adapter.services.impl.FlumeAdapterExecutionService;
import presidio.adapter.util.FlumeConfigurationUtil;
import presidio.adapter.util.ProcessExecutor;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

@Configuration
@Import(PresidioInputPersistencyServiceConfig.class)
public class AdapterConfig {

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Bean
    public PresidioExecutionService adapterExecutionService() {
        return new FlumeAdapterExecutionService(new ProcessExecutor(), new FlumeConfigurationUtil("adapter", "ca"), presidioInputPersistencyService);
    }
}
