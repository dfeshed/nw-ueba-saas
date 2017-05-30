package presidio.sdk.impl.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.services.CoreManagerService;
import presidio.sdk.api.services.PresidioInputPersistencyService;
import presidio.sdk.impl.services.CoreManagerServiceImpl;

@Configuration
@Import(PresidioInputPersistencyServiceConfig.class)
public class CoreManagerServiceConfig {

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Bean
    public CoreManagerService CoreManagerSdkImpl() {
        return new CoreManagerServiceImpl(presidioInputPersistencyService);

    }

}
