package presidio.adapter.spring;


import fortscale.common.shell.PresidioExecutionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.adapter.services.impl.FlumeAdapterExecutionService;
import presidio.adapter.util.FlumeConfigurationUtil;
import presidio.adapter.util.ProcessExecutor;

@Configuration
public class AdapterConfig {

    @Bean
    public PresidioExecutionService adapterExecutionService() {
        return new FlumeAdapterExecutionService(new ProcessExecutor(), new FlumeConfigurationUtil("adapter"));
    }
}
