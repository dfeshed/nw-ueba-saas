package presidio.sdk.impl.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import presidio.sdk.impl.services.CoreManagerServiceImpl;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
//@Import(InputSdkImplConfig.class)
public class CoreManagerSdkImplConfig {

    @Bean
    CoreManagerServiceImpl coreManagerService(){


        return new CoreManagerServiceImpl();
    }

}
