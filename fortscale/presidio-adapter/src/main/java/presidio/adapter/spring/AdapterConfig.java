package presidio.adapter.spring;


import fortscale.common.shell.PresidioExecutionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.adapter.services.impl.FlumeAdapterExecutionService;

@Configuration
public class AdapterConfig {

    @Bean
    public PresidioExecutionService adapterExecutionService() {
        return new FlumeAdapterExecutionService();
    }
}
