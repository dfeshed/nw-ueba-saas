package presidio.adapter.config;

import fortscale.common.shell.PresidioExecutionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.adapter.services.impl.AdapterExecutionServiceImpl;


@Configuration
public class AdapterConfiguration {

    @Bean
    public PresidioExecutionService adapterExecutionService() {
       return new AdapterExecutionServiceImpl();
    }
}
