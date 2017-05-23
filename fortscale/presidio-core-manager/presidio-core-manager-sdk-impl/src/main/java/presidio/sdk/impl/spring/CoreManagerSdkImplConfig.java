package presidio.sdk.impl.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.context.annotation.Import;
    import presidio.input.sdk.impl.spring.InputSdkConfig;

import presidio.sdk.api.services.PresidioInputSdk;
import presidio.sdk.impl.services.CoreManagerServiceImpl;


/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import(InputSdkConfig.class)
public class CoreManagerSdkImplConfig {

    @Autowired
    private PresidioInputSdk presidioInput;

    @Bean
    CoreManagerServiceImpl coreManagerService(){
        return new CoreManagerServiceImpl(presidioInput);

    }

}
