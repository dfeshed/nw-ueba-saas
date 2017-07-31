package presidio.adapter.spring;

import fortscale.common.shell.PresidioExecutionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.adapter.services.impl.FlumeAdapterExecutionService;

/**
 * Created by shays on 26/06/2017.
 */
@Configuration
public class AdapterTestConfig {

    @Bean
    public PresidioExecutionService adapterExecutionService() {
        return new FlumeAdapterExecutionService();
    }
}
