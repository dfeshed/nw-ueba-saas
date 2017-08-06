package presidio.input.core.spring;


import presidio.monitoring.spring.MonitoringConfiguration;
import fortscale.common.shell.PresidioExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.sdk.api.services.PresidioInputPersistencyService;

@Configuration
@Import({ PresidioInputPersistencyServiceConfig.class, AdeDataServiceConfig.class,MonitoringConfiguration.class})
public class InputCoreConfiguration {

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Autowired
    private AdeDataService adeDataService;

    @Autowired
    private OutputDataServiceSDK outputDataServiceSDK;

    @Bean
    public PresidioExecutionService inputExecutionService() {
        return new InputExecutionServiceImpl(presidioInputPersistencyService, adeDataService, outputDataServiceSDK);
    }

}
